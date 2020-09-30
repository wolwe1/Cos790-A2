package u17112631;

import u17112631.infrastructure.heuristics.PeriodSwapHeuristic;
import u17112631.infrastructure.heuristics.RoomSwapHeuristic;
import u17112631.infrastructure.implementation.FirstFitCreator;
import u17112631.infrastructure.implementation.RandomHeuristicSelector;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;
import u17112631.infrastructure.heuristics.PerturbativeHeuristic;
import u17112631.infrastructure.interfaces.IScheduleCreator;
import u17112631.infrastructure.SinglePointSelectionPerturbativeSearch;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static final String solutionFileBase = "exam_comp_set";

    public static void main(String[] args) throws Exception {

        //ExamFileReader reader = new ExamFileReader(solutionFileBase + "exam_comp_set1"+".exam");
        //ExamProblemSet problemset = reader.CreateProblemSetFromFile();

        //IMoveAccepter moveAccepter = new ImprovementAccepter();
        List<PerturbativeHeuristic> heuristicList = new ArrayList<>();
        heuristicList.add( new PeriodSwapHeuristic());
        heuristicList.add( new RoomSwapHeuristic());

        IHeuristicSelecter heuristicSelector = new RandomHeuristicSelector(heuristicList);

        var a = heuristicSelector.hasNext();
        var b = heuristicSelector.getNextHeuristic();
        var c = heuristicSelector.hasNext();
        var d = heuristicSelector.getNextHeuristic();
        var e = heuristicSelector.hasNext();
        var f = heuristicSelector.getNextHeuristic();

        IScheduleCreator scheduleCreator = new FirstFitCreator();

        //SinglePointSelectionPerturbativeSearch singlePoint = new SinglePointSelectionPerturbativeSearch(moveAccepter,heuristicSelector,scheduleCreator);
//        long seed = 2;
//        String possibleHeuristics=new String("lew");
//
//        //Create the genetic algorithm and set parameters
//        CustomGenAlg geneticAlgorithm = new CustomGenAlg(seed,possibleHeuristics);
//        geneticAlgorithm.setPopulationSize(50);
//        geneticAlgorithm.setTournamentSize(3);
//        geneticAlgorithm.setNoOfGenerations(15);
//        geneticAlgorithm.setMutationRate(0.3);
//        geneticAlgorithm.setCrossoverRate(0.3);
//        geneticAlgorithm.setInitialMaxLength(problem.getHeuristicComboLength());
//        geneticAlgorithm.setOffspringMaxLength(problem.getHeuristicComboLength());
//        geneticAlgorithm.setMutationLength(2);
//
//
//        geneticAlgorithm.setProblem(problem);
//
//        InitialSoln solution = geneticAlgorithm.evolve();
//
//        System.out.println("Best Solution");
//        System.out.println("--------------");
//        System.out.println("Fitness: "+solution.getFitness());
//        System.out.println("Heuristic combination: "+solution.getHeuCom());
//        System.out.println("Solution: ");
//        displaySolution(solution.getSoln());
    }
}
