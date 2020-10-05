package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.Arrays;
import java.util.List;

/**
 * Checks whether two exams have been scheduled together that are not allowed
 */
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
        return false;
    }

    @Override
    public boolean typeIs(String ruleName) {
        return ruleName.equals("ExclusionRule");
    }

    @Override
    public boolean willCreateViolation(Period periodWithConstraint, Period periodBeingMovedTo) {
        return !periodBeingMovedTo.equals(periodWithConstraint);
    }

    @Override
    public List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam) {
        return Arrays.asList(nextExamToSchedule,otherExam);
    }

    public Period getPeriodWithConstrainedExam(ExamSchedule schedule, int examNumber){
        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(examNumber))
                return period;
        }
        return null;
    }
}
