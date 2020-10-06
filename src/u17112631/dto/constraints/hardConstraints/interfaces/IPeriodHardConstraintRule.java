package u17112631.dto.constraints.hardConstraints.interfaces;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;

import java.util.List;

public interface IPeriodHardConstraintRule extends IHardConstraintRule {

    boolean willCreateViolation(Period periodWithConstraint, Period periodBeingMovedTo);

    List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam);

    boolean isSetFirst(Exam exam);
}
