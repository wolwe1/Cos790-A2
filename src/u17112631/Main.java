package u17112631;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.primitives.ExamProblemSet;
import u17112631.helpers.ExamFileReader;
import u17112631.infrastructure.SinglePointSelectionPerturbativeSearch;
import u17112631.infrastructure.heuristics.*;
import u17112631.infrastructure.implementation.*;
import u17112631.infrastructure.implementation.selectors.GreedyHeuristicSelector;
import u17112631.infrastructure.implementation.selectors.IterativeHeuristicSelector;
import u17112631.infrastructure.implementation.selectors.RandomHeuristicSelector;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;
import u17112631.infrastructure.interfaces.IMoveAccepter;
import u17112631.infrastructure.interfaces.IScheduleCreator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    //2
    //static final String solutionFileBase = "data\\TTC07tools\\";
    static final String solutionFileBase = "data\\";
    static SinglePointSelectionPerturbativeSearch setup(long seed, ExamProblemSet set){
        List<IHardConstraint> constraints = new ArrayList<>();

        constraints.addAll(set.getRoomHardConstraints());
        constraints.addAll(set.getPeriodHardConstraints());

        HardConstraintCalculator validator = new HardConstraintCalculator(constraints);

        SoftConstraintCalculator fitnessFunction = new SoftConstraintCalculator(set.getSoftConstraints());
        IMoveAccepter moveAccepter = new ImprovementAccepter(fitnessFunction,validator);

        List<PerturbativeHeuristic> heuristicList = new ArrayList<>();
        heuristicList.add( new PeriodSwapHeuristic());
        heuristicList.add( new RoomSwapHeuristic());
        heuristicList.add( new RoomChangeHeuristic());
        heuristicList.add( new PeriodChangeHeuristic());

        for (PerturbativeHeuristic perturbativeHeuristic : heuristicList) {
            perturbativeHeuristic.setSeed(seed);
        }

        IHeuristicSelecter heuristicSelector = new GreedyHeuristicSelector(heuristicList);

        IScheduleCreator scheduleCreator = new FirstFitCreator(set,validator);

        return new SinglePointSelectionPerturbativeSearch(moveAccepter,heuristicSelector,scheduleCreator);
    }

    public static void main(String[] args) throws Exception {

        ExamFileReader reader = new ExamFileReader(solutionFileBase + "exam_comp_set4"+".exam");
        ExamProblemSet problemSet = reader.CreateProblemSetFromFile();



        SinglePointSelectionPerturbativeSearch singlePoint = setup(1,problemSet);
        String bestCombination = singlePoint.run();
        System.out.println("Combination: " + bestCombination);
//        long seed = 2;
//        String possibleHeuristics=new String("lew");
//
//        //Create the genetic algorithm and set parameters
//        GenAlg geneticAlgorithm = new CustomGenAlg(seed,possibleHeuristics);
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
