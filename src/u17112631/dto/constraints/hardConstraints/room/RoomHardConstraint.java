package u17112631.dto.constraints.hardConstraints.room;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.interfaces.IRoomHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;

public class RoomHardConstraint implements IHardConstraint {

    private final IRoomHardConstraintRule _rule;

    public RoomHardConstraint(String input) {
        String[] info = input.split(",");
        _rule = new RoomExclusiveRule(Integer.parseInt(info[0]) );
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {
        return _rule.containsViolation(schedule);
    }
}
