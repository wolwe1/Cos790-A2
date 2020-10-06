package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean typeIs(String ruleName) {
        return ruleName.equals("AfterRule");
    }

    @Override
    public boolean willCreateViolation(Period periodWithConstraint, Period periodBeingMovedTo) {

        if(periodWithConstraint.containsExam(examOne))
            return periodBeingMovedTo.getPeriodNumber() >= periodWithConstraint.getPeriodNumber();
        else
            return periodBeingMovedTo.getPeriodNumber() <= periodWithConstraint.getPeriodNumber();

    }

    @Override
    public List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam) {

        List<Exam> swappedMembers = new ArrayList<>();

        if(nextExamToSchedule.getExamNumber() == examOne){
            swappedMembers.add(otherExam);
            swappedMembers.add(nextExamToSchedule);
        }else{
            //No change, send back
            swappedMembers.add(nextExamToSchedule);
            swappedMembers.add(otherExam);
        }

        return swappedMembers;
    }

    @Override
    public boolean isSetFirst(Exam exam) {
        return exam.getExamNumber() == examTwo;
    }
}
