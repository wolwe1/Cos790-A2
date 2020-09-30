package u17112631.infrastructure.implementation;

import u17112631.dto.primitives.*;
import u17112631.infrastructure.interfaces.IScheduleCreator;

import java.util.List;
import java.util.ListIterator;

public class FirstFitCreator implements IScheduleCreator {

    private final HardConstraintCalculator scheduleValidator;
    private final ExamProblemSet problemSet;

    public FirstFitCreator(ExamProblemSet problemSet,HardConstraintCalculator validator) {
        this.problemSet = problemSet;
        this.scheduleValidator = validator;
    }

    @Override
    public ExamSchedule createSchedule() {
        List<Exam> examsToSchedule = this.problemSet.getExams();

        ExamSchedule newSchedule = new ExamSchedule(problemSet.getPeriods());

        while (examsToSchedule.size() != 0){

            ListIterator<Exam> examIter = examsToSchedule.listIterator();

            try{
                Exam nextExamToSchedule = examIter.next();
                //Search for next possible addition
                while (!canPlaceExam(nextExamToSchedule,newSchedule)){
                    examIter.next();
                    nextExamToSchedule = examIter.next();
                }

                //Can schedule exam, remove it and add to schedule
                examIter.remove();
                placeExam(nextExamToSchedule,newSchedule);

            }catch (Exception e){
                throw new RuntimeException("Unable to generate schedule, no available options");
            }


        }
        return newSchedule;
    }

    private void placeExam(Exam exam, ExamSchedule schedule) {
        //TODO: Implement this
        Period firstPeriodToFitExam = getFirstPeriodToFitExam(exam,schedule);
        Room firstRoomToFitExam = getFirstRoomToFitExam(firstPeriodToFitExam,exam);
        firstPeriodToFitExam.placeExam(exam);

        schedule.updatePeriod(firstPeriodToFitExam);
    }

    private Room getFirstRoomToFitExam(Period period, Exam exam) {

        List<Room> rooms = period.getRooms();

        for (Room room : rooms) {
            if(room.canFitExam(exam))
                return room;
        }
        return null;
    }

    private boolean canPlaceExam(Exam exam, ExamSchedule schedule) {
        return getFirstPeriodToFitExam(exam, schedule) != null;
    }

    private Period getFirstPeriodToFitExam(Exam exam, ExamSchedule schedule){

        List<Period> periods = schedule.getPeriods();

        for (Period period : periods) {
            if(period.canFitExam(exam))
                return period;
        }
        return null;
    }
}
