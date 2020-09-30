package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;

public class AfterRule implements IPeriodHardConstraintRule {

    @Override
    public boolean containsViolation(ExamSchedule schedule) {
        return false;
    }
    //Exam �0' should be timetabled after Exam �3'
}
