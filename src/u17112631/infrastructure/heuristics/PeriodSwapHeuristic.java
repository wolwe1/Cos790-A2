package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;

public class PeriodSwapHeuristic extends PerturbativeHeuristic {

    public PeriodSwapHeuristic() {
        super("p");
    }

    @Override
    public void makeChange() {

        Period firstPeriod = pickPeriod();
        Period secondPeriod = pickPeriod();

        //Ensure differing period
        while(firstPeriod.getPeriodNumber() == secondPeriod.getPeriodNumber()){
            secondPeriod = pickPeriod();
        }

        Exam examOne = pickExam(firstPeriod);
        Exam examTwo = pickExam(secondPeriod);


        firstPeriod.replaceExam(examOne,examTwo);
        this.schedule.updatePeriod(firstPeriod);

        secondPeriod.replaceExam(examTwo,examOne);
        this.schedule.updatePeriod(secondPeriod);

    }

}
