package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

public class ExamCoincidenceRule implements IPeriodHardConstraintRule {

    private final int examOne;
    private final int examTwo;

    public ExamCoincidenceRule(int examOne, int examTwo) {
        this.examOne = examOne;
        this.examTwo = examTwo;
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {

        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(examOne)){
                return !period.containsExam(examTwo);
            }
        }
        throw new RuntimeException("Constraint not found");
    }
    // Exam �0' and Exam �1' should be timetabled in the same period.
    // If two exams are set associated in this manner yet 'clash' with each other due to student enrolment, this hard constraint is ignored.
}
