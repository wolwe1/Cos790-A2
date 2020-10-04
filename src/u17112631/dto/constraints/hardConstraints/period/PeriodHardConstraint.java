package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.List;

public class PeriodHardConstraint implements IHardConstraint {

    private final int examOne;
    private final int examTwo;

    public IPeriodHardConstraintRule get_rule() {
        return _rule;
    }

    private IPeriodHardConstraintRule _rule;

    public PeriodHardConstraint(String input) {
        String[] info = input.split(",");

        examOne = Integer.parseInt(info[0].strip());
        examTwo = Integer.parseInt(info[2].strip());

        switch (info[1].strip()){
            case "EXAM_COINCIDENCE" :
                _rule = new ExamCoincidenceRule(examOne, examTwo);
                break;
            case "EXCLUSION" :
                _rule = new ExclusionRule(examOne, examTwo);
                break;
            case "AFTER" :
                _rule = new AfterRule(examOne, examTwo);
                break;
        }
    }

    @Override
    public boolean contains(Exam exam) {
        return exam.getExamNumber() == examOne || exam.getExamNumber() == examTwo;
    }

    @Override
    public int getOtherExam(Exam exam) {
        return exam.getExamNumber() == examOne ? examTwo : examOne;
    }

    @Override
    public boolean usesRule(String ruleName) {
        return _rule.typeIs(ruleName);
    }

    @Override
    public List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam) {
        return _rule.setPriority(nextExamToSchedule,otherExam);
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {
        return _rule.containsViolation(schedule);
    }

    public boolean moveWillViolateConstraint(ExamSchedule schedule, Exam examToPlace, Period periodBeingMovedTo){
        //Check if the exam is involved in the constraint
        if(examToPlace.getExamNumber() != examOne && examToPlace.getExamNumber() != examTwo)
            return false;

        Period periodWithOtherExam = null;
        //The exam being placed is the first one in the constraint
        if(examToPlace.getExamNumber() == examOne)
            periodWithOtherExam = getPeriodWithConstrainedExam(schedule,examTwo);
        else if(examToPlace.getExamNumber() == examTwo)
            periodWithOtherExam = getPeriodWithConstrainedExam(schedule,examOne);

        //The second exam has not been placed so no violation
        if(periodWithOtherExam == null)
            return false;

        return _rule.willCreateViolation(periodWithOtherExam,periodBeingMovedTo);
    }

    public Period getPeriodWithConstrainedExam(ExamSchedule schedule, int examNumber){
        for (Period period : schedule.getPeriods()) {
            if(period.containsExam(examNumber))
                return period;
        }
        return null;
    }
}
