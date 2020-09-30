package u17112631.dto.constraints.softConstraint;


import u17112631.dto.primitives.ExamSchedule;


public class PeriodSpreadSoftConstraint implements ISoftConstraintRule {

    private final int InstitutionalSpread = 5;

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
//
//            for (int j = i + 1; j < i + InstitutionalSpread; j++) {
//                if(j >= list.size())
//                    break;
//                Period periodWithinSpread = list.get(j);
//
//                totalViolations += firstPeriodOfDay.GetNumberOfSharedStudents(periodWithinSpread);
//            }
//        }

        return totalViolations;
    }
}
