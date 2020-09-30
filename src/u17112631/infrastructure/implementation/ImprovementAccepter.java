package u17112631.infrastructure.implementation;

import u17112631.dto.primitives.ExamSchedule;
import u17112631.infrastructure.interfaces.IMoveAccepter;

public class ImprovementAccepter implements IMoveAccepter {

    ExamSchedule baseline;
    double baselineFitness;

    ExamFitnessFunction fitnessFunction;

    public ImprovementAccepter(ExamFitnessFunction function){
        fitnessFunction = function;
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
        double newFitness = fitnessFunction.getFitness(schedule);

        if(newFitness < baselineFitness)
            return false;

        setSchedule(schedule,newFitness);

        return true;
    }


}
