package u17112631.dto.constraints.hardConstraints.interfaces;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;

import java.util.List;

public interface IHardConstraint {

    boolean containsViolation(ExamSchedule schedule);

    boolean contains(Exam exam);

    int getOtherExam(Exam nextExamToSchedule);

    boolean usesRule(String ruleName);

    List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam);
}
