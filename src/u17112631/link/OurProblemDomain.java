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
import u17112631.dto.primitives.ExamSchedule;
import u17112631.infrastructure.implementation.HardConstraintCalculator;
import u17112631.infrastructure.implementation.SoftConstraintCalculator;
import u17112631.infrastructure.interfaces.IScheduleCreator;

public class OurProblemDomain extends ProblemDomain
{
    public final HardConstraintCalculator validator;
    public final SoftConstraintCalculator fitnessFunction;
/**Methods that are abstract in ProblemDomain that need to be implemented***/
    
/******************************************************************************/
public ExamSchedule initialSchedule;
private ExamProblemSet _problemSet;

    public OurProblemDomain(IScheduleCreator creator, HardConstraintCalculator validator, SoftConstraintCalculator fitnessFunction){
        initialSchedule = creator.createSchedule();
        this.validator = validator;
        this.fitnessFunction = fitnessFunction;
    }

 public OurInitialSolution evaluate(String heuristicComb)
 {
   //Implements the abstract method to create a solution using heuristicComb
   //using an instance of the InitialSoln class which is also used to calculate
   //the fitness using the objective value of the created solution.
     
    OurInitialSolution soln = new OurInitialSolution(validator,fitnessFunction,initialSchedule);
    soln.setHeuCom(heuristicComb);
    //soln.setProblemSet(_problemSet);
    soln.createSoln();

    return soln;
 }

/*****************************************************************************/
}
