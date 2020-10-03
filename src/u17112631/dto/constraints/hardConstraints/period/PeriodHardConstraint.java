package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.interfaces.IPeriodHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;

public class PeriodHardConstraint implements IHardConstraint {

    private final int _examOne;
    private final int _examTwo;

    public IPeriodHardConstraintRule get_rule() {
        return _rule;
    }

    private IPeriodHardConstraintRule _rule;

    public PeriodHardConstraint(String input) {
        String[] info = input.split(",");

        _examOne = Integer.parseInt(info[0].strip());
        _examTwo = Integer.parseInt(info[2].strip());

        switch (info[1].strip()){
            case "EXAM_COINCIDENCE" :
                _rule = new ExamCoincidenceRule(_examOne,_examTwo);
                break;
            case "EXCLUSION" :
                _rule = new ExclusionRule(_examOne,_examTwo);
                break;
            case "AFTER" :
                _rule = new AfterRule(_examOne,_examTwo);
                break;
        }
    }

    public boolean contains(Exam exam) {
        return exam.getExamNumber() == _examOne || exam.getExamNumber() == _examTwo;
    }


    @Override
    public boolean containsViolation(ExamSchedule schedule) {
        return _rule.containsViolation(schedule);
    }
}
