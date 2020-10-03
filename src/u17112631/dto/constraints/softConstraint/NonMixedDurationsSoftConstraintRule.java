package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NonMixedDurationsSoftConstraintRule implements ISoftConstraintRule {

    @Override
    public int CountOffenses(ExamSchedule schedule) {

        int totalOffenses = 0;

        for (Period period : schedule.getPeriods()) {

            for (Room room : period.getRooms()) {

                Set<Integer> distinctDurations = new HashSet<>();

                for (Exam scheduledExam : room.getExams()) {
                    distinctDurations.add(scheduledExam.getDuration());
                }

                int distinct = distinctDurations.size() - 1;
                totalOffenses += Math.max(distinct, 0);
            }
        }
        return totalOffenses;
    }
}
