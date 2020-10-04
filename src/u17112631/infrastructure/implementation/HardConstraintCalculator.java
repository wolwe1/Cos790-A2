package u17112631.infrastructure.implementation;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.period.PeriodHardConstraint;
import u17112631.dto.constraints.hardConstraints.room.RoomHardConstraint;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.List;

public class HardConstraintCalculator {

    List<IHardConstraint> constraints;

    public HardConstraintCalculator(List<IHardConstraint> constraints){
        this.constraints = constraints;
    }

    public boolean validatesConstraints(ExamSchedule schedule) {

        for (IHardConstraint constraint : constraints) {
            if(constraint.containsViolation(schedule))
                return true;
        }
        return false;
    }

    /**
     * Checks if the move will create a period constraint violation
     * @param schedule The current schedule
     * @param exam The exam being placed
     * @param period The period the exam is being moved to
     * @return Whether the move would cause a violation
     */
    public boolean willNotViolateConstraint(ExamSchedule schedule, Exam exam, Period period) {

        for (IHardConstraint constraint : constraints) {
            if(constraint instanceof PeriodHardConstraint)
                if(((PeriodHardConstraint)constraint).moveWillViolateConstraint(schedule,exam,period))
                    return false;
        }

        return true;
    }

    public IHardConstraint isConstrained(Exam nextExamToSchedule) {

        for (IHardConstraint constraint : constraints) {
            if(constraint.contains(nextExamToSchedule))
                return constraint;
        }
        return null;
    }

    public boolean willNotViolateConstraint(Room room, Exam exam) {

        for (IHardConstraint constraint : constraints) {
            if(constraint instanceof RoomHardConstraint)
                if(((RoomHardConstraint)constraint).moveWillViolateConstraint(room,exam))
                    return false;
        }
        return true;
    }
}
