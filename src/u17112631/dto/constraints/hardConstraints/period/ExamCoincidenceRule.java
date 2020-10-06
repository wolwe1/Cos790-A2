package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.Arrays;
import java.util.List;

/**
 * Exam one and two must be schedule in the same period
 * If the exams clash due to students then it is ignored
 */
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
                if(period.containsExam(examTwo))
                    return false;
                else{
                    Exam firstExam = period.getExam(examOne);
                    Exam secondExam = schedule.getExam(examTwo);

                    if(secondExam == null) throw new RuntimeException("Exam does not exist in schedule");

                    return !firstExam.hasStudents(secondExam.getStudents());
                }
            }
        }
        throw new RuntimeException("Constraint not found");
    }

    @Override
    public boolean typeIs(String ruleName) {
        return ruleName.equals("ExamCoincidenceRule");
    }

    @Override
    public boolean willCreateViolation(Period periodWithConstraint, Period periodBeingMovedTo) {
        //Check if exam is being moved to the same period
        return periodWithConstraint.equals(periodBeingMovedTo);
    }

    @Override
    public List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam) {
        return Arrays.asList(nextExamToSchedule,otherExam);
    }

    @Override
    public boolean isSetFirst(Exam exam) {
        throw new RuntimeException("This method should not be called");
    }

    public Period getPeriodWithConstrainedExam(ExamSchedule schedule, int examNumber){
        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(examNumber))
                return period;
        }
        return null;
    }

}
