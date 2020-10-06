package u17112631;

import u17112631.dto.constraints.hardConstraints.interfaces.IHardConstraint;
import u17112631.dto.primitives.ExamProblemSet;
import u17112631.helpers.ExamFileReader;
import u17112631.helpers.RunStatistics;
import u17112631.infrastructure.SinglePointSelectionPerturbativeSearch;
import u17112631.infrastructure.heuristics.*;
import u17112631.infrastructure.implementation.*;
import u17112631.infrastructure.implementation.acceptors.EqualOrBetterAccepter;
import u17112631.infrastructure.implementation.acceptors.ImprovementAccepter;
import u17112631.infrastructure.implementation.selectors.GreedyHeuristicSelector;
import u17112631.infrastructure.implementation.selectors.RandomHeuristicSelector;
import u17112631.infrastructure.interfaces.IHeuristicSelecter;
import u17112631.infrastructure.interfaces.IMoveAccepter;
import u17112631.infrastructure.interfaces.IScheduleCreator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static String[] sets = new String[]{"1","2","5","7","9"};
    static String[] acceptors = new String[]{"Improvement","Equal"};
    static String[] selectors = new String[]{"Greedy","Random"};
    //1,2,5,7,9
    //No goes 3,4,6,10,11,12
    //static final String solutionFileBase = "data\\TTC07tools\\";
    static final String solutionFileBase = "data\\";

    static HardConstraintCalculator getHardConstraintValidator(ExamProblemSet set){
        List<IHardConstraint> constraints = new ArrayList<>();

        constraints.addAll(set.getRoomHardConstraints());
        constraints.addAll(set.getPeriodHardConstraints());

         return new HardConstraintCalculator(constraints);
    }

    static IMoveAccepter getAcceptor(ExamProblemSet set,HardConstraintCalculator validator,String type){
        SoftConstraintCalculator fitnessFunction = new SoftConstraintCalculator(set.getSoftConstraints());

        if(type.equals("Improvement"))
            return new ImprovementAccepter(fitnessFunction,validator);
        else if(type.equals("Equal"))
            return new EqualOrBetterAccepter(fitnessFunction,validator);

        throw new RuntimeException("Invalid Acceptor selected");
    }

    static IHeuristicSelecter getSelector(long seed,String type) {

        List<PerturbativeHeuristic> heuristicList = new ArrayList<>();
        heuristicList.add( new PeriodSwapHeuristic());
        heuristicList.add( new RoomSwapHeuristic());
        heuristicList.add( new RoomChangeHeuristic());
        heuristicList.add( new PeriodChangeHeuristic());

        for (PerturbativeHeuristic perturbativeHeuristic : heuristicList) {
            perturbativeHeuristic.setSeed(seed);
        }

        if(type.equals("Greedy"))
            return new GreedyHeuristicSelector(heuristicList);
        else if(type.equals("Random"))
            return new RandomHeuristicSelector(heuristicList,seed);

        throw new RuntimeException("Invalid type supplied");
    }

    static SinglePointSelectionPerturbativeSearch singlePointSetup(long seed, ExamProblemSet set,String acceptor,String selector){

        HardConstraintCalculator validator = getHardConstraintValidator(set);
        IMoveAccepter moveAccepter = getAcceptor(set,validator,acceptor);
        IHeuristicSelecter heuristicSelector = getSelector(seed,selector);

        IScheduleCreator scheduleCreator = new FirstFitCreator(set,validator);

        return new SinglePointSelectionPerturbativeSearch(moveAccepter,heuristicSelector,scheduleCreator);
    }



    public static void main(String[] args) throws Exception {
        ExamFileReader reader;
        ExamProblemSet problemSet;
        SinglePointSelectionPerturbativeSearch singlePoint;

        for (String set : sets) {
            System.out.println("Set: " + set);
            reader = new ExamFileReader(solutionFileBase + "exam_comp_set"+set+".exam");
            problemSet = reader.CreateProblemSetFromFile();

            for (String acceptor : acceptors) {
                for (String selector : selectors) {
                    System.out.println(acceptor + " - " + selector);
                    singlePoint = singlePointSetup(Integer.parseInt(set),problemSet,acceptor,selector);
                    RunStatistics runStatistics = singlePoint.run();
                    runStatistics.print();
                    System.out.println("=======================================================");
                }
            }

        }

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
