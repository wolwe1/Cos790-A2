package u17112631.dto.constraints.softConstraint;

import u17112631.dto.primitives.ExamSchedule;

public class FrontLoadSoftConstraint implements ISoftConstraintRule {

    private int examSizeThreshold;
    private int periodDurationThreshold;

    public FrontLoadSoftConstraint(int examThreshold, int periodThreshold) {
        examSizeThreshold = examThreshold;
        periodDurationThreshold = periodThreshold;
    }

    @Override
    public int CountOffenses(ExamSchedule schedule) {
//
//        });
//
//        periodsOrdered.sort( (a,b) -> {
//            int sizeA = a.get_periodNumber();
//            int sizeB = b.get_periodNumber();
//
//            return sizeA > sizeB ? 1 : sizeA < sizeB  ? -1 : 0;
//        });
//
//        var largestExams = examsOrdered.subList(examsOrdered.size() - examSizeThreshold, examsOrdered.size() - 1);
//        var lastPeriods = periodsOrdered.subList(periodsOrdered.size() - periodDurationThreshold, periodsOrdered.size() - 1);
//
//        for (ScheduledExam largestExam : largestExams) {
//            for (Period lastPeriod : lastPeriods) {
//                if(largestExam.Period.get_periodNumber() == lastPeriod.get_periodNumber()){
//                    totalOffenses += 1;
//                    break;
//                }
//            }
//        }
//
//        return totalOffenses;
        return 0;

    }
}
