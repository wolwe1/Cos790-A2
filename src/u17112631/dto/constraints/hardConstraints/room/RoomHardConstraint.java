package u17112631.dto.constraints.hardConstraints.room;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.interfaces.IRoomHardConstraintRule;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Room;

import java.util.Arrays;
import java.util.List;

public class RoomHardConstraint implements IHardConstraint {

    protected int examConstrained;
    private final IRoomHardConstraintRule _rule;

    public RoomHardConstraint(String input) {
        String[] info = input.split(",");
        examConstrained = Integer.parseInt(info[0]);
        _rule = new RoomExclusiveRule(examConstrained);
    }

    @Override
    public boolean containsViolation(ExamSchedule schedule) {
        return _rule.containsViolation(schedule);
    }

    @Override
    public boolean contains(Exam exam) {
        return exam.getExamNumber() == examConstrained;
    }

    @Override
    public int getOtherExam(Exam nextExamToSchedule) {
        return -1;
    }

    @Override
    public boolean usesRule(String ruleName) {
        return _rule.typeIs(ruleName);
    }

    @Override
    public List<Exam> setPriority(Exam nextExamToSchedule, Exam otherExam) {
        return Arrays.asList(nextExamToSchedule,otherExam);
    }

    public boolean moveWillViolateConstraint(Room room, Exam exam) {
        return _rule.moveWillViolateConstraint(room,exam);
    }
}
