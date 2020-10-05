package u17112631.infrastructure;

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

    public String run(){

        StringBuilder heuristicCombination = new StringBuilder();

        //Create initial solution
        ExamSchedule bestSchedule = scheduleCreator.createSchedule();
        //Set the baseline for the move accepter
        moveAccepter.setSchedule(bestSchedule.getCopy());
        System.out.println("Initial solution fitness: " + moveAccepter.getScheduleFitness());

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

        System.out.println("Best performing heuristic combination fitness: " + moveAccepter.getScheduleFitness());
        return heuristicCombination.toString();
    }
}
