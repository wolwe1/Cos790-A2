package u17112631.infrastructure.implementation;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.primitives.*;
import u17112631.infrastructure.interfaces.IScheduleCreator;

import java.util.*;
import java.util.stream.Collectors;

public class FirstFitCreator implements IScheduleCreator {

    private final HardConstraintCalculator scheduleValidator;
    private final ExamProblemSet problemSet;

    public FirstFitCreator(ExamProblemSet problemSet, HardConstraintCalculator validator) {
        this.problemSet = problemSet;
        this.scheduleValidator = validator;
    }

    @Override
    public ExamSchedule createSchedule() {
        List<Exam> examsToSchedule = this.problemSet.getExams();

        ExamSchedule newSchedule = new ExamSchedule(problemSet.getPeriods());
        scheduleConstrainedExams(newSchedule, examsToSchedule);

        while (examsToSchedule.size() != 0) {

            ListIterator<Exam> examIter = examsToSchedule.listIterator();

            try {
                Exam nextExamToSchedule = examIter.next();

                //Search for next possible addition
                while (!canPlaceExam(nextExamToSchedule, newSchedule)) {
                    examIter.next();
                    nextExamToSchedule = examIter.next();
                }

                //Can schedule exam, remove it and add to schedule
                examIter.remove();
                placeExam(nextExamToSchedule, newSchedule);

            } catch (Exception e) {
                throw new RuntimeException("Unable to generate schedule, no available options");
            }
        }

        if (scheduleValidator.validatesConstraints(newSchedule))
            throw new RuntimeException("Initial solution contains hard constrain violation");

        return newSchedule;
    }

    private void scheduleConstrainedExams(ExamSchedule newSchedule, List<Exam> examsToSchedule) {

        scheduleConstraint("RoomExclusiveRule",examsToSchedule,newSchedule);
        scheduleConstraint("ExclusionRule", examsToSchedule, newSchedule);
        scheduleConstraint("ExamCoincidenceRule", examsToSchedule, newSchedule);
        scheduleConstraint("AfterRule", examsToSchedule, newSchedule);

    }

    private void scheduleConstraint(String rule, List<Exam> examsToSchedule, ExamSchedule newSchedule) {
        Set<Exam> placedExams = new HashSet<>();
        var examIter = examsToSchedule.listIterator();

        while (examIter.hasNext()) {
            Exam nextExam = examIter.next();

            if(!placedExams.contains(nextExam)){
                IHardConstraint constraint = scheduleValidator.isConstrained(nextExam);

                if (constraint != null) {
                    if(constraint.usesRule(rule)){
                        List<Exam> placedExamsByConstraint = constrainedMove(nextExam, constraint, examsToSchedule, newSchedule);
                        placedExams.addAll(placedExamsByConstraint);
                    }
                }
            }
        }
        examsToSchedule.removeAll(placedExams);
    }

    private List<Exam> constrainedMove(Exam nextExamToSchedule, IHardConstraint constraint, List<Exam> examsToSchedule, ExamSchedule newSchedule) {

        Exam otherExam = null;
        int otherExamNumber = constraint.getOtherExam(nextExamToSchedule);

        for (Exam exam : examsToSchedule) {
            if (exam.getExamNumber() == otherExamNumber){
                otherExam = exam;
                break;
            }
        }

        if (constraint.usesRule("AfterRule")) {
            if(otherExam != null){
                List<Exam> examsInOrder = constraint.setPriority(nextExamToSchedule,otherExam);
                afterInsert(examsInOrder.get(0), examsInOrder.get(1), newSchedule);
            }else{
                Exam placedExam = newSchedule.getExam(otherExamNumber);
                return afterInsertWithOneAlreadyPlaced(newSchedule,nextExamToSchedule,placedExam,constraint.isSetFirst(placedExam));
            }

            return Arrays.asList(nextExamToSchedule,otherExam);

        } else if (constraint.usesRule("ExamCoincidenceRule")) {
            if(otherExam != null && newSchedule.isScheduled(otherExam) == null)
                coincidenceInsert(nextExamToSchedule,otherExam,newSchedule);
            else
                coincidenceInsert(nextExamToSchedule,otherExamNumber,newSchedule);
            return Arrays.asList(nextExamToSchedule,otherExam);

        } else if (constraint.usesRule("ExclusionRule")) {
            if(otherExam.hasStudents(nextExamToSchedule.getStudents()))
                return new ArrayList<>();

            exclusionInsert(nextExamToSchedule,otherExam,newSchedule);
            return Arrays.asList(nextExamToSchedule,otherExam);

        } else if (constraint.usesRule("RoomExclusiveRule")){
            roomExclusiveInsert(nextExamToSchedule,newSchedule);
            return Collections.singletonList(nextExamToSchedule);
        }

        return examsToSchedule;
    }

    private void roomExclusiveInsert(Exam nextExamToSchedule, ExamSchedule newSchedule) {
        var periods = newSchedule.getPeriods();
        var periodsWithEmptyRooms = periods.stream().filter(
                (p)-> p.getRooms().stream().anyMatch((r) -> r.getNumberOfExams() == 0)).collect(Collectors.toList());

        if(periodsWithEmptyRooms.size() == 0) throw new RuntimeException("Unable to satisfy room exclusive constraint");

        var chosenPeriod = periods.get(0);
        var room = getFirstRoomToFitExam(chosenPeriod,nextExamToSchedule);
        if(room == null) throw new RuntimeException("Unable to satisfy room exclusive constraint");

        room.placeExam(nextExamToSchedule);
        chosenPeriod.updateRoom(room);
        newSchedule.updatePeriod(chosenPeriod);

    }

    private void exclusionInsert(Exam nextExamToSchedule, Exam otherExam, ExamSchedule schedule) {

        Period periodForFirstExamScheduled = schedule.isScheduled(nextExamToSchedule);
        Period periodForSecondExamScheduled = schedule.isScheduled(nextExamToSchedule);

        if(periodForFirstExamScheduled != null){
            scheduleExclusiveExam(otherExam, schedule, periodForFirstExamScheduled);
        }else if(periodForSecondExamScheduled != null){
            scheduleExclusiveExam(nextExamToSchedule, schedule, periodForSecondExamScheduled);
        }else{
            Period period = getFirstPeriodToFitExam(nextExamToSchedule,schedule);

            getFirstRoomToFitExam(nextExamToSchedule, schedule, period);

            //Second exam
            scheduleExclusiveExam(otherExam, schedule,period);
        }

    }

    private void getFirstRoomToFitExam(Exam nextExamToSchedule, ExamSchedule schedule, Period period) {
        if(period == null) throw new RuntimeException("Unable to satisfy exclusion rule");

        Room room = getFirstRoomToFitExam(period,nextExamToSchedule);

        if(room == null) throw new RuntimeException("Unable to satisfy exclusion rule");

        room.placeExam(nextExamToSchedule);
        period.updateRoom(room);
        schedule.updatePeriod(period);
    }

    private void scheduleExclusiveExam(Exam otherExam, ExamSchedule schedule, Period periodForFirstExamScheduled) {
        Period periodTwo = getFirstPeriodToFitExamExcluding(otherExam,schedule,periodForFirstExamScheduled);

        getFirstRoomToFitExam(otherExam, schedule, periodTwo);
    }

    private Period getFirstPeriodToFitExamExcluding(Exam exam, ExamSchedule schedule, Period periodExcluded) {
        List<Period> periods = schedule.getPeriods();

        for (Period period : periods) {
            if (!period.equals(periodExcluded) && period.canFitExam(exam))
                if (period.hasNoStudentConflict(exam) && scheduleValidator.willNotViolateConstraint(schedule, exam, period))
                    return period;
        }
        return null;
    }

    private void coincidenceInsert(Exam nextExamToSchedule, Exam otherExam, ExamSchedule newSchedule) {
        if(nextExamToSchedule.getExamNumber() == 187 || nextExamToSchedule.getExamNumber() == 311){
            System.out.println("gotem");
        }
        Period period = getFirstPeriodToFitExams(nextExamToSchedule,otherExam,newSchedule);

        if(period == null) throw new RuntimeException("Unable to satisfy coincidence rule");

        Room roomOne = getFirstRoomToFitExam(period,nextExamToSchedule);
        if(roomOne == null) throw new RuntimeException("Unable to satisfy coincidence rule");

        roomOne.placeExam(nextExamToSchedule);
        period.updateRoom(roomOne);

        Room roomTwo = getFirstRoomToFitExam(period,otherExam);
        if(roomTwo == null) throw new RuntimeException("Unable to satisfy coincidence rule");

        roomTwo.placeExam(otherExam);
        period.updateRoom(roomTwo);

        newSchedule.updatePeriod(period);
    }

    private void coincidenceInsert(Exam nextExamToSchedule, int otherExamNumber, ExamSchedule newSchedule) {

        Period period = newSchedule.getPeriodWithExam(otherExamNumber);

        if(period == null) throw new RuntimeException("Unable to satisfy coincidence rule");

        Room roomOne = getFirstRoomToFitExam(period,nextExamToSchedule);
        if(roomOne == null) throw new RuntimeException("Unable to satisfy coincidence rule");

        roomOne.placeExam(nextExamToSchedule);
        period.updateRoom(roomOne);

        newSchedule.updatePeriod(period);
    }

    private Period getFirstPeriodToFitExams(Exam examOne, Exam examTwo, ExamSchedule schedule) {

        List<Period> periods = schedule.getPeriods();

        for (Period period : periods) {
            if (period.getTotalRemainingCapacity() >= examOne.getNumberOfStudents() + examTwo.getNumberOfStudents()){
                if (period.hasNoStudentConflict(examOne) && scheduleValidator.willNotViolateConstraint(schedule, examOne, period)){
                    Period periodWithFirstExam = period.getCopy();
                    Room room = getFirstRoomToFitExam(periodWithFirstExam,examOne);

                    if(room != null){
                        room.placeExam(examOne);
                        periodWithFirstExam.updateRoom(room);
                        if(periodWithFirstExam.hasNoStudentConflict(examTwo) && scheduleValidator.willNotViolateConstraint(schedule, examTwo, periodWithFirstExam)){
                            Room roomAbleToFitNextExam = getFirstRoomToFitExam(periodWithFirstExam,examTwo);

                            if(roomAbleToFitNextExam != null)
                                return period;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void placeExam(Exam exam, ExamSchedule schedule) {

        Period firstPeriodToFitExam = getFirstPeriodToFitExam(exam, schedule);
        if (firstPeriodToFitExam == null) throw new RuntimeException("Cannot find period to fit exam");

        Room firstRoomToFitExam = getFirstRoomToFitExam(firstPeriodToFitExam, exam);
        if (firstRoomToFitExam == null) throw new RuntimeException("Period found but no room to place");

        firstRoomToFitExam.placeExam(exam);
        firstPeriodToFitExam.updateRoom(firstRoomToFitExam);
        schedule.updatePeriod(firstPeriodToFitExam);
    }

    private Room getFirstRoomToFitExam(Period period, Exam exam) {

        List<Room> rooms = period.getRooms();

        for (Room room : rooms) {
            if (room.canFitExam(exam) && room.hasNoStudentConflict(exam) && scheduleValidator.willNotViolateConstraint(room,exam) )
                return room;
        }
        return null;
    }

    private boolean canPlaceExam(Exam exam, ExamSchedule schedule) {
        return getFirstPeriodToFitExam(exam, schedule) != null;
    }

    private Period getFirstPeriodToFitExam(Exam exam, ExamSchedule schedule) {

        List<Period> periods = schedule.getPeriods();

        for (Period period : periods) {
            if (period.canFitExam(exam))
                if (period.hasNoStudentConflict(exam) && scheduleValidator.willNotViolateConstraint(schedule, exam, period)){

                    for (Room room : period.getRooms()) {
                        if(room.canFitExam(exam) && scheduleValidator.willNotViolateConstraint(room,exam))
                            return period;
                    }
                }
        }
        return null;
    }


    /**
     * Schedules an exam after another, assumed to not be able to fail
     *
     * @param firstExam The first exam to schedule
     * @param SecondExam      The second exam to place
     * @param schedule       The current schedule
     */
    private void afterInsert(Exam firstExam, Exam SecondExam, ExamSchedule schedule) {

        var period = getFirstPeriodToFitExam(firstExam, schedule);
        if(period == null) throw new RuntimeException("Unable to satisfy after constraint");

        var secondPeriod = getFirstPeriodToFitExamAfterPeriod(SecondExam, schedule, period);
        if(secondPeriod == null) throw new RuntimeException("Unable to satisfy after constraint");

        var roomOne = getFirstRoomToFitExam(period,firstExam);
        var roomTwo = getFirstRoomToFitExam(secondPeriod,SecondExam);

        if(roomOne == null || roomTwo == null) throw new RuntimeException("Unable to satisfy after constraint");

        roomOne.placeExam(firstExam);
        roomTwo.placeExam(SecondExam);

        period.updateRoom(roomOne);
        secondPeriod.updateRoom(roomTwo);

        schedule.updatePeriod(period);
        schedule.updatePeriod(secondPeriod);
    }

    private List<Exam> afterInsertWithOneAlreadyPlaced(ExamSchedule schedule, Exam examToSchedule,Exam placedExam,boolean placedExamComesFirst){

        Period placedPeriod = schedule.getPeriodWithExam(placedExam.getExamNumber());
        Period secondPeriod;

        if(placedExamComesFirst){
            secondPeriod = getFirstPeriodToFitExamAfterPeriod(examToSchedule, schedule, placedPeriod);
        }else{
            secondPeriod = getFirstPeriodToFitExamBeforePeriod(examToSchedule, schedule, placedPeriod);
        }
        placeExamInPeriod(schedule, examToSchedule, secondPeriod);

        return Collections.singletonList(examToSchedule);
    }

    private void placeExamInPeriod(ExamSchedule schedule, Exam examToSchedule, Period secondPeriod) {
        if(secondPeriod == null) throw new RuntimeException("Unable to satisfy after constraint");

        var roomTwo = getFirstRoomToFitExam(secondPeriod,examToSchedule);

        if(roomTwo == null) throw new RuntimeException("Unable to satisfy after constraint");

        roomTwo.placeExam(examToSchedule);
        secondPeriod.updateRoom(roomTwo);
        schedule.updatePeriod(secondPeriod);
    }

    private Period getFirstPeriodToFitExamBeforePeriod(Exam exam, ExamSchedule schedule, Period latestPeriod) {
        List<Period> periods = schedule.getPeriods();

        List<Period> periodsBefore = periods.stream().filter((p) -> p.getPeriodNumber() < latestPeriod.getPeriodNumber()).collect(Collectors.toList());

        return getPeriodForExam(exam, schedule, periodsBefore);
    }

    private Period getPeriodForExam(Exam exam, ExamSchedule schedule, List<Period> periodsBefore) {
        for (Period period : periodsBefore) {
            if (period.canFitExam(exam))
                if (period.hasNoStudentConflict(exam) && scheduleValidator.willNotViolateConstraint(schedule, exam, period))
                    return period;
        }
        return null;
    }

    private Period getFirstPeriodToFitExamAfterPeriod(Exam exam, ExamSchedule schedule, Period periodToStartFrom) {
        List<Period> periods = schedule.getPeriods();

        List<Period> periodsAfter = periods.stream().filter((p) -> p.getPeriodNumber() > periodToStartFrom.getPeriodNumber()).collect(Collectors.toList());

        return getPeriodForExam(exam, schedule, periodsAfter);
    }

}