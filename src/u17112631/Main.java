package u17112631;

import genalg.GenAlg;
import initialsoln.InitialSoln;
import problemdomain.ProblemDomain;
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
import u17112631.link.OurProblemDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Single point or multipoint? {1: single, 2: multi}");

        String userInput = myObj.nextLine();

        if(Integer.parseInt(userInput) == 1){
            SinglePointSelectionPerturbativeSearch singlePoint;

            List<RunStatistics> bestPerformersForEachSet = new ArrayList<>();

            System.out.println("Please enter the number of runs per set:");
            userInput = myObj.nextLine();
            int NUMRUNS = Integer.parseInt(userInput);

            for (String set : sets) {
                System.out.println("Set: " + set);
                reader = new ExamFileReader(solutionFileBase + "exam_comp_set"+set+".exam");
                problemSet = reader.CreateProblemSetFromFile();

                //Best performer from run
                RunStatistics best = null;

                for (int i = 0; i < NUMRUNS; i++) {
                    System.out.println("run "+ i);
                    for (String acceptor : acceptors) {
                        for (String selector : selectors) {
                            //System.out.println(acceptor + " - " + selector);
                            singlePoint = singlePointSetup(i,problemSet,acceptor,selector);
                            RunStatistics runStatistics = singlePoint.run();
                            runStatistics.addType(acceptor + " - " + selector);
                            runStatistics.addSeed(i);
                            //runStatistics.print();
                            //System.out.println("=======================================================");

                            if(best == null || runStatistics.getBestFitness() < best.getBestFitness()){
                                best = runStatistics;
                            }
                        }
                    }
                }
                bestPerformersForEachSet.add(best);
            }

            for (RunStatistics setBest : bestPerformersForEachSet) {
                setBest.print();
            }
        }else{
            List<RunStatistics> bestInSets = new ArrayList<>();

            System.out.println("Please enter the number of runs per set:");
            userInput = myObj.nextLine();
            int NUMRUNS = Integer.parseInt(userInput);

            System.out.println("Please enter the population size:");
            userInput = myObj.nextLine();
            int POPSIZE = Integer.parseInt(userInput);

            System.out.println("Please enter the number of generations per run:");
            userInput = myObj.nextLine();
            int NUMGENS = Integer.parseInt(userInput);

            for (String set : sets) {
                System.out.println("\nSet: " + set);
                //Setup
                reader = new ExamFileReader(solutionFileBase + "exam_comp_set"+set+".exam");
                problemSet = reader.CreateProblemSetFromFile();

                HardConstraintCalculator validator = getHardConstraintValidator(problemSet);
                SoftConstraintCalculator fitnessFunction = new SoftConstraintCalculator(problemSet.getSoftConstraints());
                IScheduleCreator creator = new FirstFitCreator(problemSet,validator);
                OurProblemDomain ourProblemDomain = new OurProblemDomain(creator,validator,fitnessFunction);

                double startFitness = fitnessFunction.getFitness(ourProblemDomain.initialSchedule);
                RunStatistics bestForSet = new RunStatistics();
                bestForSet.addBestFitness(Double.POSITIVE_INFINITY);

                //Run
                for (int i = 0; i < NUMRUNS; i++) {
                    GenAlg geneticAlgorithm = new GenAlg(i,"abcd");

                    geneticAlgorithm.setPopulationSize(POPSIZE);
                    geneticAlgorithm.setTournamentSize(2);
                    geneticAlgorithm.setNoOfGenerations(NUMGENS);
                    geneticAlgorithm.setMutationRate(0.2);
                    geneticAlgorithm.setCrossoverRate(0.6);
                    geneticAlgorithm.setInitialMaxLength(15);
                    geneticAlgorithm.setOffspringMaxLength(35);
                    geneticAlgorithm.setMutationLength(2);
                    geneticAlgorithm.setPrint(false);
                    geneticAlgorithm.setProblem(ourProblemDomain);

                    long startTime = System.currentTimeMillis();
                    InitialSoln solution = geneticAlgorithm.evolve();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;

                    RunStatistics runStat = new RunStatistics();
                    runStat.setStartingFitness(startFitness);
                    runStat.addBestPerformer(solution.getHeuCom());
                    runStat.addRunDuration(duration);
                    runStat.addSeed(i);
                    runStat.addBestFitness(solution.getFitness());

                    if(bestForSet.getBestFitness() > runStat.getBestFitness())
                        bestForSet = runStat;

                }
                bestInSets.add(bestForSet);

            }

            for (RunStatistics bestInSet : bestInSets) {
                bestInSet.print();
            }
        }
    }
}
