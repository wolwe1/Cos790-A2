package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TwoInARowSoftConstraintRule implements ISoftConstraintRule {
    @Override
    public int CountOffenses(ExamSchedule schedule) {

        int totalViolations = 0;

        List<Period> periods = schedule.getPeriods();

        List<Period> list = new ArrayList<>(periods);
        list.sort(Comparator.comparingInt(Period::getPeriodNumber));

        for (int i = 0; i < list.size() - 1; i++) {
            Period currentPeriod = list.get(i);
            Period nextPeriod = list.get(i + 1 );

             totalViolations += currentPeriod.GetNumberOfSharedStudents(nextPeriod);
        }

        return totalViolations;
    }
}
