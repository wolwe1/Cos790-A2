package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.ExamSchedule;


public class NonMixedDurationsSoftConstraint implements ISoftConstraintRule {

    @Override
    public int CountOffenses(ExamSchedule schedule) {

        int totalOffenses = 0;

//        for (Period period : schedule.GetDistinctPeriods()) {
//
//            var scheduledExamsInPeriod = schedule.GetExamsScheduledInPeriod(period);
//
//            for (Room room : period.get_rooms()) {
//
//                List<ScheduledExam> examsInRoom = new ArrayList<>();
//                Set<Integer> distinctDurations = new HashSet<>();
//
//                for (ScheduledExam scheduledExam : scheduledExamsInPeriod) {
//                    if(scheduledExam.RoomsUsed.contains(room.get_roomNumber()))
//                        examsInRoom.add(scheduledExam);
//                }
//
//                for (ScheduledExam scheduledExam : examsInRoom) {
//                    distinctDurations.add(scheduledExam.Exam.get_duration());
//                }
//
//                int distinct = distinctDurations.size() - 1;
//                totalOffenses += distinct < 0 ? 0 : distinct;
//            }
//        }
        return totalOffenses;
    }
}
