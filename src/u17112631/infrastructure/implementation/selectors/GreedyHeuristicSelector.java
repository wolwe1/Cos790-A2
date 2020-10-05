package u17112631.infrastructure.implementation.selectors;

import u17112631.infrastructure.heuristics.PerturbativeHeuristic;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;

import java.util.*;
import java.util.stream.Collectors;

public class GreedyHeuristicSelector implements IHeuristicSelecter {

    private final List<HeuristicEntry> heuristics;
    private ListIterator<HeuristicEntry> heuristicIterator;

    public GreedyHeuristicSelector(List<PerturbativeHeuristic> heuristicList){

        heuristics = new ArrayList<>();

        for (PerturbativeHeuristic heuristic : heuristicList) {
            heuristics.add(new HeuristicEntry(heuristic));
        }

        heuristicIterator = heuristics.listIterator();
    }

    @Override
    public PerturbativeHeuristic getNextHeuristic() {
        HeuristicEntry entry = heuristicIterator.next();

        return entry.heuristic;
    }

    @Override
    public boolean hasNext() {
        return heuristicIterator.hasNext();
    }

    @Override
    public void reset() {
        HeuristicEntry lastUsed = heuristicIterator.previous();
        lastUsed.value++;
        heuristics.sort( (a,b)->{
            int aValue = a.value;
            int bValue = b.value;

            return Integer.compare(bValue, aValue);
        });

        heuristicIterator = heuristics.listIterator();
    }

    private PerturbativeHeuristic getHighestValueHeuristic(){
        return heuristics.stream().max(Comparator.comparing(HeuristicEntry::getValue)).get().heuristic;
    }

    private List<HeuristicEntry> getUnusedHeuristics(){
        return heuristics.stream().filter( (h) -> !h.used).collect(Collectors.toList());
    }
}

    class HeuristicEntry{
        public PerturbativeHeuristic heuristic;
        public int value;
        public boolean used;

        public HeuristicEntry(PerturbativeHeuristic heuristic){
            this.heuristic = heuristic;
            value = 0;
            used = false;
        }

        public int getValue() {
            return value;
        }
    }

