package u17112631.dto.constraints;

import u17112631.dto.constraints.softConstraint.*;
import u17112631.dto.primitives.ExamSchedule;

public class SoftConstraint {

    private ISoftConstraintRule _rule;
    private int _penalty;

    public SoftConstraint(String input) {

        String[] info = input.split(",");

        _penalty = Integer.parseInt(info[1].strip());

        switch (info[0]) {
            case "TWOINAROW":
                _rule = new TwoInARowSoftConstraintRule();
                break;
            case "TWOINADAY":
                _rule = new TwoInADaySoftConstraintRule();
                break;
            case "PERIODSPREAD":
                _rule = new PeriodSpreadSoftConstraintRule();
                break;
            case "NONMIXEDDURATIONS":
                _rule = new NonMixedDurationsSoftConstraintRule();
                break;
            case "FRONTLOAD":
                _rule = new FrontLoadSoftConstraintRule(Integer.parseInt(info[1].strip()),Integer.parseInt(info[2].strip()));
                _penalty = Integer.parseInt(info[3].strip());
                break;
        }
    }

    public int CalculatePenalty(ExamSchedule schedule){
        return _rule.CountOffenses(schedule) * _penalty;
    }
}
