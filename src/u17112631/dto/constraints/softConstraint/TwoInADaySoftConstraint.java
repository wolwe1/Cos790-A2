package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.ExamSchedule;

public class TwoInADaySoftConstraint implements ISoftConstraintRule {
    @Override
    public int CountOffenses(ExamSchedule schedule) {
        int totalViolations = 0;

//        Set<Integer> students = schedule.GetDistinctStudents();
//        Set<Period> periods = schedule.GetDistinctPeriods();
//
//        List<Period> list = new ArrayList<>(periods);
//        list.sort( (a,b) -> {
//            return a.get_periodNumber() > b.get_periodNumber() ? 1 : a.get_periodNumber() < b.get_periodNumber() ? -1 : 0;
//        });
//
//        for (int i = 0; i < list.size() - 1; i+=3) {
//            Period firstPeriodOfDay = list.get(i);
//            Period lastPeriodOfDay = list.get(i + 2 );
//
//            Calendar cal1 = Calendar.getInstance();
//            Calendar cal2 = Calendar.getInstance();
//            cal1.setTime(firstPeriodOfDay.get_date());
//            cal2.setTime(lastPeriodOfDay.get_date());
//            boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
//                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
//
//            if(sameDay)
//                totalViolations += firstPeriodOfDay.GetNumberOfSharedStudents(lastPeriodOfDay);
//            else
//                break; //Two periods a day not 3, no need to check further
//        }

        return totalViolations;
    }
}
