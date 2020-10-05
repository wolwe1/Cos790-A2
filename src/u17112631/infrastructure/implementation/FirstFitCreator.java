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

        var examIter = examsToSchedule.listIterator();
        Set<Exam> placedExams = new HashSet<>();

        while (examIter.hasNext()) {
            Exam nextExam = examIter.next();

            if(!placedExams.contains(nextExam)){
                IHardConstraint constraint = scheduleValidator.isConstrained(nextExam);

                if (constraint != null) {
                    List<Exam> placedExamsByConstraint = constrainedMove(nextExam, constraint, examsToSchedule, newSchedule);
                    placedExams.addAll(placedExamsByConstraint);
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
            List<Exam> examsInOrder = constraint.setPriority(nextExamToSchedule,otherExam);
            afterInsert(examsInOrder.get(0), examsInOrder.get(1), newSchedule);
            return Arrays.asList(nextExamToSchedule,otherExam);

        } else if (constraint.usesRule("ExamCoincidenceRule")) {
            coincidenceInsert(nextExamToSchedule,otherExam,newSchedule);
            return Arrays.asList(nextExamToSchedule,otherExam);

        } else if (constraint.usesRule("ExclusionRule")) {
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
        Period period = getFirstPeriodToFitExam(nextExamToSchedule,schedule);

        if(period == null) throw new RuntimeException("Unable to satisfy exclusion rule");

        Room room = getFirstRoomToFitExam(period,nextExamToSchedule);

        if(room == null) throw new RuntimeException("Unable to satisfy exclusion rule");

        //Second exam
        Period periodTwo = getFirstPeriodToFitExamExcluding(otherExam,schedule,period);

        if(periodTwo == null) throw new RuntimeException("Unable to satisfy exclusion rule");

        Room roomTwo = getFirstRoomToFitExam(periodTwo,otherExam);

        if(roomTwo == null) throw new RuntimeException("Unable to satisfy exclusion rule");

        room.placeExam(nextExamToSchedule);
        roomTwo.placeExam(otherExam);

        period.updateRoom(room);
        periodTwo.updateRoom(roomTwo);

        schedule.updatePeriod(period);
        schedule.updatePeriod(periodTwo);
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

    private Period getFirstPeriodToFitExams(Exam examOne, Exam examTwo, ExamSchedule schedule) {

        List<Period> periods = schedule.getPeriods();

        for (Period period : periods) {
            if (period.getTotalRemainingCapacity() >= examOne.getNumberOfStudents() + examTwo.getNumberOfStudents())
                if (period.hasNoStudentConflict(examOne) && scheduleValidator.willNotViolateConstraint(schedule, examOne, period)
                && period.hasNoStudentConflict(examTwo) && scheduleValidator.willNotViolateConstraint(schedule, examTwo, period))
                    return period;
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
        var secondPeriod = getFirstPeriodToFitExamAfterPeriod(SecondExam, schedule, period);

        if(period == null || secondPeriod == null) throw new RuntimeException("Unable to satisfy after constraint");

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

    private Period getFirstPeriodToFitExamAfterPeriod(Exam exam, ExamSchedule schedule, Period periodToStartFrom) {
        List<Period> periods = schedule.getPeriods();

        List<Period> periodsAfter = periods.stream().filter((p) -> p.getPeriodNumber() > periodToStartFrom.getPeriodNumber()).collect(Collectors.toList());

        for (Period period : periodsAfter) {
            if (period.canFitExam(exam))
                if (period.hasNoStudentConflict(exam) && scheduleValidator.willNotViolateConstraint(schedule, exam, period))
                    return period;
        }
        return null;
    }

}