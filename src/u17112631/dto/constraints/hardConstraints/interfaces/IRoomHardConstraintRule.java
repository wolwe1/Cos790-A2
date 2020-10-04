package u17112631.dto.constraints.hardConstraints.interfaces;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Room;

public interface IRoomHardConstraintRule extends IHardConstraintRule {

    boolean moveWillViolateConstraint(Room room, Exam exam);
}
