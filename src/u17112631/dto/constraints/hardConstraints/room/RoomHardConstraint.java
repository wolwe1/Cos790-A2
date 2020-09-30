package u17112631.dto.constraints.hardConstraints.room;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.interfaces.IRoomHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;

public class RoomHardConstraint implements IHardConstraint {

    private final int _room;
    private IRoomHardConstraintRule _rule;

    public RoomHardConstraint(String input) {
        String[] info = input.split(",");
        _room = Integer.parseInt(info[0]);
        _rule = new RoomExclusiveRule();
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {
        return _rule.containsViolation(schedule);
    }
}
