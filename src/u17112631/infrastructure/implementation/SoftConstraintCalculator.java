package u17112631.infrastructure.implementation;

import u17112631.dto.constraints.SoftConstraint;
import u17112631.dto.primitives.ExamSchedule;

import java.util.List;

public class SoftConstraintCalculator {

    private final List<SoftConstraint> softConstraints;

    public SoftConstraintCalculator(List<SoftConstraint> constraints){
        this.softConstraints = constraints;
    }

    public double getFitness(ExamSchedule schedule){

        double totalPenalty = 0;
        for (SoftConstraint constraint : this.softConstraints) {
            totalPenalty += constraint.CalculatePenalty(schedule);
        }
        return totalPenalty;
    }
}
