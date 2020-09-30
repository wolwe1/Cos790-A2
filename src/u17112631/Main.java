package u17112631;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.constraints.hardConstraints.period.PeriodHardConstraint;
import u17112631.dto.primitives.ExamProblemSet;
import u17112631.helpers.ExamFileReader;
import u17112631.infrastructure.SinglePointSelectionPerturbativeSearch;
import u17112631.infrastructure.heuristics.PeriodSwapHeuristic;
import u17112631.infrastructure.heuristics.RoomSwapHeuristic;
import u17112631.infrastructure.implementation.*;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;
import u17112631.infrastructure.heuristics.PerturbativeHeuristic;
import u17112631.infrastructure.interfaces.IMoveAccepter;
import u17112631.infrastructure.interfaces.IScheduleCreator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static final String solutionFileBase = "exam_comp_set";

    public static void main(String[] args) throws Exception {

        ExamFileReader reader = new ExamFileReader(solutionFileBase + "exam_comp_set1"+".exam");
        ExamProblemSet problemset = reader.CreateProblemSetFromFile();

        List<IHardConstraint> constraints = new ArrayList<>();
        constraints.add(new PeriodHardConstraint("1,EXAM_COINCIDENCE,2"));
        constraints.add(new PeriodHardConstraint("3,EXCLUSION,4"));
        constraints.add(new PeriodHardConstraint("5,AFTER,6"));

        HardConstraintCalculator validator = new HardConstraintCalculator(constraints);

        SoftConstraintCalculator fitnessFunction = new SoftConstraintCalculator();
        IMoveAccepter moveAccepter = new ImprovementAccepter(fitnessFunction,validator);

        List<PerturbativeHeuristic> heuristicList = new ArrayList<>();
        heuristicList.add( new PeriodSwapHeuristic());
        heuristicList.add( new RoomSwapHeuristic());

        IHeuristicSelecter heuristicSelector = new RandomHeuristicSelector(heuristicList);

        IScheduleCreator scheduleCreator = new FirstFitCreator(problemset);

        SinglePointSelectionPerturbativeSearch singlePoint = new SinglePointSelectionPerturbativeSearch(moveAccepter,heuristicSelector,scheduleCreator);
        singlePoint.run();
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
