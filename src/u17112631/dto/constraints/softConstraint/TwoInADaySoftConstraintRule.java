package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class TwoInADaySoftConstraintRule implements ISoftConstraintRule {
    @Override
    public int CountOffenses(ExamSchedule schedule) {
        int totalViolations = 0;

        List<Period> periods = schedule.getPeriods();

        List<Period> list = new ArrayList<>(periods);
        list.sort(Comparator.comparingInt(Period::getPeriodNumber));

        for (int i = 0; i < list.size() - 1; i+=3) {
            Period firstPeriodOfDay = list.get(i);
            Period lastPeriodOfDay = list.get(i + 2 );

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(firstPeriodOfDay.getDate());
            cal2.setTime(lastPeriodOfDay.getDate());
            boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

            if(sameDay)
                totalViolations += firstPeriodOfDay.GetNumberOfSharedStudents(lastPeriodOfDay);
            else
                break; //Two periods a day not 3, no need to check further
        }

        return totalViolations;
    }
}
