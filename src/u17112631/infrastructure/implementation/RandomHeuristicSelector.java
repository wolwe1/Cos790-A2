package u17112631.infrastructure.implementation;

import u17112631.infrastructure.interfaces.IHeuristicSelecter;
import u17112631.infrastructure.heuristics.PerturbativeHeuristic;

import java.util.List;
import java.util.ListIterator;

public class RandomHeuristicSelector implements IHeuristicSelecter {

    List<PerturbativeHeuristic> heuristics;
    ListIterator<PerturbativeHeuristic> iterator;
    
    public RandomHeuristicSelector(List<PerturbativeHeuristic> heuristics){
        this.heuristics = heuristics;
        iterator = this.heuristics.listIterator();
    }

    @Override
    public PerturbativeHeuristic getNextHeuristic() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public void reset() {
        iterator = this.heuristics.listIterator();
    }
}
