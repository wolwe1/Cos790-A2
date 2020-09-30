package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.ExamSchedule;

import java.util.ArrayList;
import java.util.Set;

public class TwoInARowSoftConstraint implements ISoftConstraintRule {
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
//        for (int i = 0; i < list.size() - 1; i++) {
//            Period currentPeriod = list.get(i);
//            Period nextPeriod = list.get(i + 1 );
//
//             totalViolations += currentPeriod.GetNumberOfSharedStudents(nextPeriod);
//        }

        return totalViolations;
    }
}
