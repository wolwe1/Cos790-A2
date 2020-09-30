/*
 * This class implements a problem domain and implements ProblemDomain abstract
 * class.
 *
 * N. Pillay
 *
 * 30 August 2016 
 */

package u17112631.link;
//Import statements

import problemdomain.ProblemDomain;
import u17112631.dto.primitives.ExamProblemSet;

public class OurProblemDomain extends ProblemDomain
{
/**Methods that are abstract in ProblemDomain that need to be implemented***/
    
/******************************************************************************/

private ExamProblemSet _problemSet;

 public OurInitialSolution evaluate(String heuristicComb)
 {
   //Implements the abstract method to create a solution using heuristicComb
   //using an instance of the InitialSoln class which is also used to calculate
   //the fitness using the objective value of the created solution.
     
    OurInitialSolution soln = new OurInitialSolution();
    soln.setHeuCom(heuristicComb);
    //soln.setProblemSet(_problemSet);
    soln.createSoln();

    return soln;
 }

 public void setProblemSet(ExamProblemSet problemSet){
     _problemSet = problemSet;
 }
 public int getHeuristicComboLength(){
     return _problemSet.getExams().size() * 2;
 }
/*****************************************************************************/
}
