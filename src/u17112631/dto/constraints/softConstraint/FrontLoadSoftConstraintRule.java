package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;

import java.util.ArrayList;
import java.util.List;

public class FrontLoadSoftConstraintRule implements ISoftConstraintRule {

    private final int examSizeThreshold;
    private final int periodDurationThreshold;

    public FrontLoadSoftConstraintRule(int examThreshold, int periodThreshold) {
        examSizeThreshold = examThreshold;
        periodDurationThreshold = periodThreshold;
    }

    @Override
    public int CountOffenses(ExamSchedule schedule) {

        var periods = schedule.getPeriods();
        List<Exam> exams = new ArrayList<>();

        for (Period period : periods) {
            exams.addAll(period.getExams());
        }

        exams.sort( (a,b)->{
            int sizeA = a.getNumberOfStudents();
            int sizeB = b.getNumberOfStudents();

            return Integer.compare(sizeB, sizeA);
        });

        var largestExams = exams.subList(exams.size() - examSizeThreshold, exams.size() - 1);
        var lastPeriods = periods.subList(periods.size() - periodDurationThreshold, periods.size() - 1);

        int totalOffenses = 0;
        for (Exam largestExam : largestExams) {

            if(lastPeriods.stream().anyMatch(o -> o.getExams().contains(largestExam)))
                totalOffenses++;
        }

        return totalOffenses;
    }
}
