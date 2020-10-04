package u17112631.infrastructure.implementation;

import u17112631.dto.primitives.ExamSchedule;
import u17112631.infrastructure.interfaces.IMoveAccepter;

public class ImprovementAccepter implements IMoveAccepter {

    ExamSchedule baseline;
    double baselineFitness;

    SoftConstraintCalculator fitnessFunction;
    HardConstraintCalculator validityChecker;

    public ImprovementAccepter(SoftConstraintCalculator function,HardConstraintCalculator validator){
        fitnessFunction = function;
        validityChecker = validator;
        baselineFitness = Double.POSITIVE_INFINITY;
    }

    @Override
    public void setSchedule(ExamSchedule schedule) {
        baseline = schedule;
        baselineFitness = fitnessFunction.getFitness(baseline);
    }

    private void setSchedule(ExamSchedule schedule, double newFitness) {
        baseline = schedule;
        baselineFitness = newFitness;
    }

    @Override
    public boolean acceptsChange(ExamSchedule schedule) {

        if( validityChecker.validatesConstraints(schedule))
            return false;

        double newFitness = fitnessFunction.getFitness(schedule);

        if(newFitness <= baselineFitness)
            return false;

        setSchedule(schedule,newFitness);

        return true;
    }


}
