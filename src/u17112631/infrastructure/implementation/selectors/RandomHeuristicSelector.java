package u17112631.infrastructure.implementation.selectors;

import u17112631.infrastructure.heuristics.PerturbativeHeuristic;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;

import java.util.*;

public class RandomHeuristicSelector implements IHeuristicSelecter {

    private final List<PerturbativeHeuristic> heuristics;
    private final List<PerturbativeHeuristic> usedHeuristics;
    private final Random numgen;

    public RandomHeuristicSelector(List<PerturbativeHeuristic> heuristics, long seed){
        numgen = new Random(seed);
        this.heuristics = heuristics;
        usedHeuristics = new ArrayList<>();
    }

    @Override
    public PerturbativeHeuristic getNextHeuristic() {
        int chosenHeuristic = numgen.nextInt(heuristics.size());

        PerturbativeHeuristic heuristic = heuristics.get(chosenHeuristic);

        usedHeuristics.add(heuristic);
        heuristics.remove(heuristic);

        return heuristic;
    }

    @Override
    public boolean hasNext() {
        return heuristics.size() > 0;
    }

    @Override
    public void reset() {
        heuristics.addAll(usedHeuristics);
        usedHeuristics.clear();
    }
}
