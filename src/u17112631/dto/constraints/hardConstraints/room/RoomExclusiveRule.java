package u17112631.dto.constraints.hardConstraints.room;

import u17112631.dto.constraints.hardConstraints.interfaces.IRoomHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

/**
 * The set exam must be schedule by itself
 */
public class RoomExclusiveRule implements IRoomHardConstraintRule {

    private final int examConstrained;

    public RoomExclusiveRule(int examNumber) {
        this.examConstrained = examNumber;
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {

        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(examConstrained)){
                Room roomWithExam = period.findRoomWithExam(examConstrained);
                return roomWithExam.getNumberOfExams() != 1;
            }
        }
        throw new RuntimeException("Exam was not found in schedule");
    }

    @Override
    public boolean typeIs(String ruleName) {
        return ruleName.equals("RoomExclusiveRule");
    }

    @Override
    public boolean moveWillViolateConstraint(Room room, Exam exam) {
        //The room must be empty to have the exam placed

        if(exam.getExamNumber() == examConstrained){
            return room.getNumberOfExams() > 0;
            //Our exam is already placed, any others will violate
        }else return room.containsExam(new Exam(examConstrained));
    }
    //An exam must be timetabled in a room by itself e.g.
}
