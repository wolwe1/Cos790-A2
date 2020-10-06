package u17112631.infrastructure;

import u17112631.helpers.RunStatistics;
import u17112631.infrastructure.interfaces.IMoveAccepter;
import u17112631.infrastructure.heuristics.PerturbativeHeuristic;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;
import u17112631.infrastructure.interfaces.IScheduleCreator;

public class SinglePointSelectionPerturbativeSearch {

    IMoveAccepter moveAccepter;
    IHeuristicSelecter heuristicSelector;
    IScheduleCreator scheduleCreator;

    public SinglePointSelectionPerturbativeSearch(IMoveAccepter accepter,IHeuristicSelecter selector, IScheduleCreator creator){
        this.moveAccepter = accepter;
        this.heuristicSelector = selector;
        this.scheduleCreator = creator;
    }

    public RunStatistics run(){

        StringBuilder heuristicCombination = new StringBuilder();

        //Create initial solution
        ExamSchedule bestSchedule = scheduleCreator.createSchedule();
        //Set the baseline for the move accepter
        moveAccepter.setSchedule(bestSchedule.getCopy());

        //Stats
        double startingFitness = moveAccepter.getScheduleFitness();
        RunStatistics statistics = new RunStatistics();
        statistics.setStartingFitness(startingFitness);
        System.out.println("Initial solution fitness: " + startingFitness);
        long startTime = System.currentTimeMillis();

        while (heuristicSelector.hasNext()) {

            //Select a starting heuristic
            PerturbativeHeuristic heuristic = heuristicSelector.getNextHeuristic();
            heuristic.setSchedule(bestSchedule.getCopy());

            heuristic.makeChange();

            if(moveAccepter.acceptsChange(heuristic.getSchedule())) {

                heuristicCombination.append(heuristic.getId());
                bestSchedule = heuristic.getSchedule();
                //Reset the selector
                heuristicSelector.reset();
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        statistics.addRunDuration(duration);
        statistics.addBestPerformer(heuristicCombination.toString());
        statistics.addBestFitness(moveAccepter.getScheduleFitness());

        System.out.println("Best performing heuristic combination fitness: " + moveAccepter.getScheduleFitness());
        return statistics;
    }
}
