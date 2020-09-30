package u17112631.dto.constraints.hardConstraints.interfaces;

import u17112631.dto.primitives.ExamSchedule;

public interface IHardConstraint {

    boolean containsViolation(ExamSchedule schedule);
}
