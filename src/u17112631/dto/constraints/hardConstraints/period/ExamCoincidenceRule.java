package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;

public class ExamCoincidenceRule implements IPeriodHardConstraintRule {
    // Exam �0' and Exam �1' should be timetabled in the same period.
    // If two exams are set associated in this manner yet 'clash' with each other due to student enrolment, this hard constraint is ignored.
}
