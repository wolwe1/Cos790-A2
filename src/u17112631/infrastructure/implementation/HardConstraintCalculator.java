package u17112631.infrastructure.implementation;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.primitives.ExamSchedule;

import java.util.List;

public class HardConstraintCalculator {

    List<IHardConstraint> constraints;

    public HardConstraintCalculator(List<IHardConstraint> constraints){
        this.constraints = constraints;
    }

    public boolean validatesConstraints(ExamSchedule schedule) {

        for (IHardConstraint constraint : constraints) {
            if(constraint.containsViolation(schedule))
                return true;
        }
        return false;
    }
}
