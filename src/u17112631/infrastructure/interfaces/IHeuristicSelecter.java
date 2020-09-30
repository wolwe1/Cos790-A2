package u17112631.infrastructure.interfaces;

import u17112631.infrastructure.heuristics.PerturbativeHeuristic;

public interface IHeuristicSelecter {

    PerturbativeHeuristic getNextHeuristic();

    boolean hasNext();

    void reset();
}
