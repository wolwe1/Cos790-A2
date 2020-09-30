package u17112631.dto.constraints.hardConstraints.period;

import u17112631.dto.primitives.Exam;

public class PeriodHardConstraint {

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
                _rule = new ExamCoincidenceRule();
                break;
            case "EXCLUSION" :
                _rule = new ExclusionRule();
                break;
            case "AFTER" :
                _rule = new AfterRule();
                break;
        }
    }

    public boolean contains(Exam exam) {
        return exam.getExamNumber() == _examOne || exam.getExamNumber() == _examTwo;
    }

    public int GetLinkedExam(Exam exam) {
        return exam.getExamNumber() == _examOne ? _examTwo : _examOne;
    }

}
