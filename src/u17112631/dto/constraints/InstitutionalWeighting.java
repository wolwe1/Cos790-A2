package u17112631.dto.constraints;

import u17112631.dto.constraints.softConstraint.*;
import u17112631.dto.primitives.ExamSchedule;

public class InstitutionalWeighting {

    private ISoftConstraintRule _rule;
    private int _penalty;

    public InstitutionalWeighting(String input) {

        String[] info = input.split(",");

        _penalty = Integer.parseInt(info[1].strip());

        switch (info[0]) {
            case "TWOINAROW":
                _rule = new TwoInARowSoftConstraint();
                break;
            case "TWOINADAY":
                _rule = new TwoInADaySoftConstraint();
                break;
            case "PERIODSPREAD":
                _rule = new PeriodSpreadSoftConstraint();
                break;
            case "NONMIXEDDURATIONS":
                _rule = new NonMixedDurationsSoftConstraint();
                break;
            case "FRONTLOAD":
                _rule = new FrontLoadSoftConstraint(Integer.parseInt(info[1].strip()),Integer.parseInt(info[2].strip()));
                _penalty = Integer.parseInt(info[3].strip());
                break;
        }
    }

    public int CalculatePenalty(ExamSchedule schedule){
        return _rule.CountOffenses(schedule) * _penalty;
    }
}
