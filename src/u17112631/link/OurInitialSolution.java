/*
 * This class implements the InitialSoln abstract class and is used to store
 * details of the initial solution.
 *
 * Nelishia Pillay
 *
 * 30 August 2016
 */
package u17112631.link;

//Import statements

import initialsoln.InitialSoln;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.infrastructure.heuristics.*;
import u17112631.infrastructure.implementation.HardConstraintCalculator;
import u17112631.infrastructure.implementation.SoftConstraintCalculator;

import java.util.ArrayList;
import java.util.List;

//For selective constructive, the solution must create a complete timetable
//No hard constraints must be violated and the fitness will be soft contraints met
public class OurInitialSolution extends InitialSoln
{

    /******************************************************************************/
 //Data elements
    
 //Stores the heuristic combination that will be used to create an initial
 //solution.    
 private String _heuristicComb;
 
 //Stores the fitness value to be used for the initial solution created.
 private double _fitness;
 
 //Stores the initial solution created using the heuristic. In this problem
 //this is stored as an array of strings just as an example. However, the 
 //solution can be of any type, e.g. for the travelling salesman problem it 
 //could be a string representing the tour.
 String _solutionName;
 
 //It may be necessary to store other values that are specific to problem being
 //solved that is different from the fitness or needed to calculate the fitness.
 //For example, for the examination timetabling problem the hard and soft
 //constraint cost also needs to be stored.
    //ExamTimeTablingSolution _solution;
    //ExamProblemSet _problemSet;

    //Problem Specific items
    List<PerturbativeHeuristic> thisSolutionsHeuristics;
    HardConstraintCalculator validator;
    SoftConstraintCalculator fitnessFunction;
    ExamSchedule schedule;

    public OurInitialSolution(HardConstraintCalculator validator, SoftConstraintCalculator fitnessFunction, ExamSchedule schedule){
        this.validator = validator;
        this.fitnessFunction = fitnessFunction;
        this.schedule = schedule;
        this.thisSolutionsHeuristics = new ArrayList<>();
    }
/*****************************************************************************/

/**Implementation of abstract methods needed to extend InitialSoln***/
 
/******************************************************************************/
 public double getFitness()
 {
   return _fitness;
 }
/*****************************************************************************/
 
/******************************************************************************/
 public void setHeuCom(String heuristicComb)
 {
   //Implements the abstract method to store the heuristic combination used to
   //create an initial solution.
     
   this._heuristicComb =heuristicComb;
   this.thisSolutionsHeuristics.clear();

     for (int i = 0; i < _heuristicComb.length(); i++){
         char c = _heuristicComb.charAt(i);
         String heuristicId =Character.toString(c);

         //Process char
         switch (heuristicId){
             case "a" :
                 this.thisSolutionsHeuristics.add(new RoomChangeHeuristic());
                 break;
             case "b" :
                 this.thisSolutionsHeuristics.add(new RoomSwapHeuristic());
                 break;
             case "c" :
                 this.thisSolutionsHeuristics.add(new PeriodChangeHeuristic());
                 break;
             case "d" :
                 this.thisSolutionsHeuristics.add(new PeriodSwapHeuristic());
                 break;
         }
     }
 }
/*****************************************************************************/

 /******************************************************************************/
public String getHeuCom()
{
 //Implements the abstract method to return the heuristic combination used to
 //create the solution.
    
  return _heuristicComb;
}
/*****************************************************************************/

/******************************************************************************/
public String getSoln()
{
  //Implements the abstract method to return a solution.
    
  return _solutionName;
}
/*****************************************************************************/

/******************************************************************************/
 public int fitter(InitialSoln other)
 {
   //This method is used to compare two initial solutions to determine which of
   //the two is fitter. 

     return Double.compare(other.getFitness(), _fitness);
 }
/*****************************************************************************/

/**Methods in addition to the abstract methods that need to be implemented.***/

/******************************************************************************/ 
 public void createSoln()
 {
   /*This method creates a solution using the heuristic combination.*/
   
   //Construct a solution to the problem using the heuristic combination.

   _solutionName =  _heuristicComb;

     for (PerturbativeHeuristic heuristic : this.thisSolutionsHeuristics) {

         //Select a starting heuristic
         heuristic.setSchedule(schedule);

         heuristic.makeChange();

     }

     if(validator.validatesConstraints(schedule))
         _fitness = Double.POSITIVE_INFINITY;
     else{
         _fitness = fitnessFunction.getFitness(schedule);
     }
 }
}
