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
     
   if(other.getFitness() < _fitness)
    return -1;
   else if (other.getFitness() > _fitness)
    return 1;
   else
    return 0;
 }
/*****************************************************************************/

/**Methods in addition to the abstract methods that need to be implemented.***/

/******************************************************************************/ 
 public void createSoln()
 {
   /*This method creates a solution using the heuristic combination.*/
   
   //Construct a solution to the problem using the heuristic combination.


   _solutionName ="Solution created using heuristic combo : " + _heuristicComb;


//   _solution = new ExamTimeTablingSolution(_problemSet,_heuristicComb);
//     try {
//         _solution.SolveProblem();
//     } catch (Exception e) {
//         e.printStackTrace();
//         System.out.println(e.getMessage());
//     }
//     _fitness = _solution.GetFitness();
 }

    //public void setProblemSet(IProblemSet problemSet) {
    // _problemSet = (ExamProblemSet) problemSet;
    //}

    //public void print() {
    // _solution.PrintSolution();
    //}

    //public int getProblemSetSize(){
    // return _problemSet.get_exams().size();
    //}
}
