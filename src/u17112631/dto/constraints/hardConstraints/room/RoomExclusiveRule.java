package u17112631.dto.constraints.hardConstraints.room;

import u17112631.dto.constraints.hardConstraints.interfaces.IRoomHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

/**
 * The set exam must be schedule by itself
 */
public class RoomExclusiveRule implements IRoomHardConstraintRule {

    private int exam;

    public RoomExclusiveRule(int examNumber) {
        this.exam = examNumber;
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {

        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(exam)){
                Room roomWithExam = period.findRoomWithExam(exam);
                return roomWithExam.getNumberOfExams() == 1;
            }
        }
        throw new RuntimeException("Exam was not found in schedule");
    }
    //An exam must be timetabled in a room by itself e.g.
}
