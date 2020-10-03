package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

/**
 * Exam 2 should be timetabled after Exam 1
 */
public class AfterRule implements IPeriodHardConstraintRule {

    private final int examOne;
    private final int examTwo;


    public AfterRule(int examOne, int examTwo) {
        this.examOne = examOne;
        this.examTwo = examTwo;
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {

        var periods = schedule.getPeriods().listIterator();

        while(periods.hasNext()){
            //If period has second exam then check remaining periods for first exam
            if(periods.next().containsExam(examTwo)){
                while(periods.hasNext()){
                    if(periods.next().containsExam(examOne)) //Period one scheduled after two, no violation
                        return false;
                }
                //First exam not found after second, violation
                return true;
            }
        }

        throw new RuntimeException("Exam could not be found in schedule");
    }

}
