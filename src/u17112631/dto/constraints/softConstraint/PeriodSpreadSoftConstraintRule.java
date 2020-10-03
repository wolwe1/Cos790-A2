package u17112631.dto.constraints.softConstraint;


import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class PeriodSpreadSoftConstraintRule implements ISoftConstraintRule {

    private final int InstitutionalSpread = 5;

    @Override
    public int CountOffenses(ExamSchedule schedule) {
        int totalViolations = 0;

        List<Period> periods = schedule.getPeriods();

        List<Period> list = new ArrayList<>(periods);
        list.sort(Comparator.comparingInt(Period::getPeriodNumber));

        for (int i = 0; i < list.size() - 1; i+=3) {
            Period firstPeriodOfDay = list.get(i);

            for (int j = i + 1; j < i + InstitutionalSpread; j++) {
                if(j >= list.size())
                    break;
                Period periodWithinSpread = list.get(j);

                totalViolations += firstPeriodOfDay.GetNumberOfSharedStudents(periodWithinSpread);
            }
        }

        return totalViolations;
    }
}
