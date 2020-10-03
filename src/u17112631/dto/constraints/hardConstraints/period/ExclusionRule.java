package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

public class ExclusionRule implements IPeriodHardConstraintRule {

    private final int examOne;
    private final int examTwo;

    public ExclusionRule(int examOne, int examTwo) {
        this.examOne = examOne;
        this.examTwo = examTwo;
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {

        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(examOne)){
                return period.containsExam(examTwo);
            }
        }
        throw new RuntimeException("Constraint not found");
    }
    //Exam �0' and Exam �2' should be not be timetabled in the same period
}
