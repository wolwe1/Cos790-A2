/*********************************************
 * OPL 4.2 Model file
 * Author: Andrew Parkes http://www.cs.nott.ac.uk/~ajp/
 * Copyright reserved (AJP and/or University of Nottingham)
 * Creation date: 19/07/2007 13:45
 * Modified: multiple changes up until Aug 2011
 
VERSION: 
this is 'minimal version' for general release & will probably be changed 
or extended.

 *********************************************/

/* NAMING CONVENTIONS:

parameters: (i.e. the values are known) use

  camelNotation or all UPPERCASE (when directly from the .exam file)

variables: (values are to be determined by CPLEX), use

 PolishNotation/Prolog (?) -- i.e. as camel except start with UpperCase

*/

execute{ 
cplex.mipemphasis = 0;
cplex.tilim = 3600;
cplex.MEMORYEMPHASIS=1; // turn on memory conservation
}

string problemName = ... ;

execute{
   writeln("problemName: ",problemName);
}

// ---------- DATA directly from .exam file ------------------------

// --- from Exam section of .exam file ----

int nbExams    = ... ;
range Exam_r = 0..(nbExams-1); 

int nbStudents = ... ;
range Student_r = 0..(nbStudents-1); 

int examSize[Exam_r] = ... ;
int examDuration[Exam_r] = ... ; 

// enrollments[e][s]==1 if exam e has student s enrolled, 0 otherwise 
int enrollments[Exam_r][Student_r] = ...;

int nbDurations = ... ;
range Duration_r = 0..(nbDurations-1);
int   examDurationType[Exam_r][Duration_r] = ... ;

/* the data processsor must also 
provide the following information derive from the enrollments:

the exam conflict arising from student enrollments are given to be given in the 
form of a set of indexed weighted arcs.

an "arc" is a directed edge

numStudents is the number of students enrolled in both e1 and e2
*/
tuple NumberedArc
{
 int ID;
 int e1;
 int e2;  
 int numStudents;
}

// each edge is given as a single arc -- it does not matter which direction it is given
{NumberedArc} numberedEdge_set = ... ;

int nbEdges = ... ;
range Edge_r = 0..(nbEdges-1);

// in numberedEdge_set the IDs should be unique and in the range Edge_r
// (just number them sequentially from zero)

// --- from Period section of .exam file ----

int nbPeriods  = ... ; 
range Period_r = 0..(nbPeriods-1); 
int periodDuration[Period_r] = ... ; 

// sameDay[p1][p2]==1 if p1 and p2 are the same day, 0 otherwise
int sameDay[Period_r][Period_r] = ... ;

int softPeriodPenalties[Period_r] = ... ;

// --- from Rooms section of .exam file ----

int nbRooms    = ... ;
range Room_r = 0..(nbRooms-1); 
int roomCapacity[Room_r] = ... ; 

int softRoomPenalties[Room_r] = ... ;

// --- from HardPeriod section of .exam file ----

/* it happens that the data for these all use ordered pairs of
exams so it is convenient to use a tuple for these */

tuple ExamPair {
 int e1;
 int e2;  
}

{ExamPair} AFTER_set = ... ;
{ExamPair} COINCIDENCE_set = ... ;
{ExamPair} EXCLUSION_set = ... ;

// --- from HardRoom section of .exam file ----

{int} ROOM_EXCLUSIVE_set = ... ;

// --- from Institutional weightings section of .exam file ----

int TWOINAROW    = ... ;
int TWOINADAY    = ... ;
int PERIODSPREAD = ... ;  //  the weight for periodSpread (defaults to 1)
int PERIODSPREAD_SPREAD = ... ; // the spread itself
int NOMIXEDDURATIONS = ... ;
int FRONTLOAD    = ... ;

// these two are not actually used as they are processed into the boolean flags that follow
int paramFrontLoadNumExams   = ... ;
int paramFrontLoadNumPeriods = ... ;

// these boolean flags are 1 if FrontLoad applies, 0 otherwise
int examUseFrontLoad[Exam_r]     = ... ;
int periodUseFrontLoad[Period_r] = ... ;

// ---- print some statistics derived from the .exam data -----

execute {
   // evaluate and print some statistics derivable from the .exam file
   var BOSneeded = 0;
   for ( var e in Exam_r ) 
      for ( var s in Student_r ) 
         if ( enrollments[e][s]==1) BOSneeded++;
         
   writeln("BOSneeded=  ", BOSneeded);
  
   // same again to cross-check the numbers are consistent
   var BOSneeded2 = 0;
   for ( var e2 in Exam_r ) BOSneeded2 += examSize[e2];
         
   writeln("BOSneeded2= ", BOSneeded2);
  
   var BOSavailable = 0;
   for ( var r in Room_r ) BOSavailable += nbPeriods*roomCapacity[r];   
   writeln("BOSavailable= ", BOSavailable);

   writeln("Utilisation = ", 100.0*BOSneeded/BOSavailable, " %");
}

// ---------- END OF DATA directly from .exam file ------------------------

// ----- DATA from the .sln file (if present) ---------

// represents that exam 'ID' is assigned to Period 'time' and room 'room'
tuple examTimeRoom_t {
  int ID;
  int time;
  int room;
}

// the set of assignments
{examTimeRoom_t} examTimeRoom_set = ... ;

/* NOTE: there is no requirement or check that the the set of assignments
in the solution is complete. It can be a partial assignment, e.g. with the intention that 
might try out some large neighbourhood search by simply taking just a 
portion of an existing solution, and optimising the unassigned exams
*/

// ----- END OF DATA from the .sln file (if present) ---------

// --- the real decision variable ----

// dvar boolean ExamTimeRoom[Exam_r ][Period_r][Room_r];
dvar float ExamTimeRoom[Exam_r ][Period_r][Room_r] in 0..1 ;

// Note: It is okay for this to be a float! but do not know which is better

// --------- derived auxiliary variables --------

// ExamTime[e][p] is true iff exam e is assigned to period p
dvar boolean ExamTime[Exam_r ][Period_r];

dvar boolean ExamRoom[Exam_r ][Room_r];

// ========= VARIABLES AND CALCULATIONS FOR THE PENALTIES =====

//    ------- Two in a row  ----------

// dvar int penTwoInARow[Student_r][Period_r];

/* PenTwoInRow_Edge[ <ID,e1,e2,n> ]==1 iff 
   exam e2 is directly before or after exam e1 and is on the sameDay
*/

dvar boolean PenTwoInRow_Edge[Edge_r]; 

// the sum over edges the boolean penalties
//NOTE: not actually needed as the sum will occur directly 
// in the objective -- but useful just for reporting/debugging
dvar int+ RawPen_TwoInARow;

//    ------- Two in a day  ----------

dvar boolean PenTwoInDay_Edge[Edge_r]; 
// dvar float PenTwoInDay_Edge[Edge_r] in 0..1; 

// only needed for reporting separate values
dvar int+ RawPen_TwoInADay;

//    ------- periodspread  ----------

dvar boolean PenPeriodSpread_Edge[Edge_r]; 
// dvar float PenPeriodSpread_Edge[Edge_r] in 0..1; 

// only needed for reporting separate values
dvar int+ RawPen_PeriodSpread;

//    ------- softPeriod  ----------

// only needed for reporting separate values
dvar int+ RawPen_SoftPeriodPenalties;

//    ------- softRoom  ----------

// only needed for reporting separate values
dvar int+ RawPen_SoftRoomPenalties;

//    ------- NonMixedDuration  ----------

// dvar boolean UsedDuration[Duration_r][Period_r][Room_r];
// TODO check this is okay as a float
dvar float UsedDuration[Duration_r][Period_r][Room_r] in 0..1 ;

// dvar int+ PenNoMixedDuration[Period_r][Room_r];
dvar float+ PenNoMixedDuration[Period_r][Room_r]; // check is okay

// only needed for reporting separate values
dvar int+ RawPen_NonMixedDuration;

dvar int+ RawPen_FrontLoadPenalties;

/* 
// this is only used if the ExamTime/Room variables are changed into 
// parameters for the sake of validating very large instances
execute {
   writeln("Processing examTimeRoom_set");
   // relies on the default setting being 0
   for( var etr in examTimeRoom_set ) {
      // writeln(" ID= ",etr.ID);
      ExamTimeRoom[etr.ID][etr.time][etr.room] = 1;
      ExamTime[etr.ID][etr.time] = 1;
      ExamRoom[etr.ID][etr.room] = 1;
   }
   writeln("Processing examTimeRoom_set (done)");
}
*/

// =============== THE OBJECTIVE FUNCTION ==========================
/* have the choice whether to use the variable, or the equivalent expression.
Generally it seems to be better to use the expression, and the variable is 
only really used for reporting. */

minimize 

//       TWOINAROW * RawPen_TwoInARow + 
       TWOINAROW * sum(edge in numberedEdge_set) edge.numStudents * PenTwoInRow_Edge[edge.ID] +

//       TWOINADAY * RawPen_TwoInADay +
       TWOINADAY * sum(edge in numberedEdge_set) edge.numStudents * PenTwoInDay_Edge[edge.ID] +
       
//    PERIODSPREAD * RawPen_PeriodSpread +
    PERIODSPREAD * sum(edge in numberedEdge_set) edge.numStudents * PenPeriodSpread_Edge[edge.ID] +

//               1 * RawPen_SoftPeriodPenalties +
                 1 * (sum(p in Period_r) softPeriodPenalties[p] * sum(e in Exam_r) ExamTime[e][p]) +

//               1 * RawPen_SoftRoomPenalties +
               1 * ( sum(r in Room_r) softRoomPenalties[r] * sum(e in Exam_r) ExamRoom[e][r] ) +

// NOMIXEDDURATIONS * RawPen_NonMixedDuration +
NOMIXEDDURATIONS * sum(p in Period_r, r in Room_r) PenNoMixedDuration[p][r] +

//       FRONTLOAD * RawPen_FrontLoadPenalties +
       FRONTLOAD * sum(e in Exam_r: examUseFrontLoad[e] == 1 )
                     sum(p in Period_r : periodUseFrontLoad[p] == 1 )
                      ExamTime[e][p]
                      
;  // end of objective function

// ----  THE CONSTRAINTS ---------------

subject to {

// ============= (PARTIAL) SOLUTION =================

/* UNCOMMENT TO ENFORCE THE (PARTIAL) SOLUTION

// enforce the (partial) solution (if any)
// without the solution being enforced then it can be far too slow!!
// use these for enforcing all or part of a (partial) solution given examTimeRoom_set
forall( < ID , time, room > in examTimeRoom_set )
   ExamTime[ID][time] == 1;
forall( < ID , time, room > in examTimeRoom_set )
   ExamRoom[ID][room] == 1;
forall( < ID , time, room > in examTimeRoom_set )
   ExamTimeRoom[ID][time][room] == 1;

*/

// ============= (PARTIAL) SOLUTION (DONE) =================

   // every exam is in precisely one Period and Room
   forall(e in Exam_r)
    sum(r in Room_r, p in Period_r) ExamTimeRoom[e][p][r] == 1;
 
   // every exam is in precisely one Period
   forall(e in Exam_r)
    sum(p in Period_r) ExamTime[e][p] == 1;

   // every exam is in precisely one Room
   forall(e in Exam_r)
    sum(r in Room_r) ExamRoom[e][r] == 1;
 
    // --- constraints linking ExamTimeRoom to ExamTime and ExamRoom

   forall(e in Exam_r)
    forall(p in Period_r)
       sum(r in Room_r) ExamTimeRoom[e][p][r] == ExamTime[e][p];
 
   forall(e in Exam_r)
    forall(r in Room_r)
      sum(p in Period_r) ExamTimeRoom[e][p][r] == ExamRoom[e][r];
 
    // --- conflict matrix ----------

   // for every period every student is in at most one exam

   forall(p in Period_r)
     forall(s in Student_r) 
      sum(e in Exam_r : enrollments[e][s] == 1 ) ExamTime[e][p] <= 1;

    // ---- enforce room capacities ---------------

   // for every room-slot (room&period) the total examsize is not too big

   forall(p in Period_r, r in Room_r)
        sum(e in Exam_r) examSize[e] * ExamTimeRoom[e][p][r] 
         <= roomCapacity[r];

   // this not needed though is also valid - not sure if it helps
   // for every room-slot (room&period) the total examsize is not too big
   forall(p in Period_r)
        sum(e in Exam_r) examSize[e] * ExamTime[e][p] <= sum(r in Room_r) roomCapacity[r] ;

   // this not needed though is also valid - not sure if it helps
   forall(r in Room_r)
        sum(e in Exam_r) examSize[e] * ExamRoom[e][r] <= nbPeriods * roomCapacity[r] ;

   // ----- enforce period durations ---------

   // for every exam the assigned period is long enough
   forall(e in Exam_r, p in Period_r)
            examDuration[e] * ExamTime[e][p] <= periodDuration[p];
   
// ------------ HARD PERIOD: "AFTER" -----------

forall( < e1 , e2 > in AFTER_set )
   forall(p1 in Period_r, p2 in Period_r : p1 <= p2 ) 
       ExamTime[e1][p1] + ExamTime[e2][p2] <= 1; // NOT( (e1 in p1)  & (e2 in p2) )

// ------------ HARD PERIOD: "COINCIDENCE" -----------

// e1 e2 must occur in same period
forall( < e1 , e2 > in COINCIDENCE_set )
   forall(p in Period_r ) 
       ExamTime[e1][p] == ExamTime[e2][p];

// ------------ HARD PERIOD: "EXCLUSION" -----------

// e1 e2 must NOT occur in same period
forall( < e1 , e2 > in EXCLUSION_set )
   forall(p in Period_r ) 
       ExamTime[e1][p] + ExamTime[e2][p] <= 1;

// ------------ HARD ROOM: "ROOM_EXCLUSIVE" -----------

forall( e in ROOM_EXCLUSIVE_set, e2 in Exam_r : e2 != e )
  forall(p in Period_r, r in Room_r)
       ExamTime[e][p] + ExamRoom[e][r] + ExamTime[e2][p] + ExamRoom[e2][r] <= 3;

    // -------------- handle two-in-a-row --------------

    forall(p1 in Period_r, p2 in Period_r : 
       p2 == p1 + 1 && sameDay[p1][p2] == 1 
       )
      forall(edge in numberedEdge_set)
         ExamTime[edge.e1][p1] + ExamTime[edge.e2][p2] <= 
             1 + PenTwoInRow_Edge[edge.ID];    

    // same for reversed arc
    forall(p1 in Period_r, p2 in Period_r : 
       p2 == p1 + 1 && sameDay[p1][p2] == 1 
       )
      forall(edge in numberedEdge_set)
         ExamTime[edge.e2][p1] + ExamTime[edge.e1][p2] <= 
             1 + PenTwoInRow_Edge[edge.ID];    

/* NOTE we don't need two separate booleans for each direction of the edge,
as only one direction can be forced true, and it doesn't matter which one. */

   RawPen_TwoInARow == 
      sum(edge in numberedEdge_set) edge.numStudents * PenTwoInRow_Edge[edge.ID];

    // -------------- handle two-in-a-day --------------

    forall(p1 in Period_r, p2 in Period_r : 
       p2 > p1 + 1 && sameDay[p1][p2] == 1 
       )
      forall(edge in numberedEdge_set)
         ExamTime[edge.e1][p1] + ExamTime[edge.e2][p2] <= 
             1 + PenTwoInDay_Edge[edge.ID];    

    forall(p1 in Period_r, p2 in Period_r : 
       p2 > p1 + 1 && sameDay[p1][p2] == 1 
       )
      forall(edge in numberedEdge_set)
         ExamTime[edge.e2][p1] + ExamTime[edge.e1][p2] <= 
             1 + PenTwoInDay_Edge[edge.ID];    

   RawPen_TwoInADay == 
      sum(edge in numberedEdge_set) 
         edge.numStudents * PenTwoInDay_Edge[edge.ID];

    // -------------- handle period spread --------------

    forall(p1 in Period_r, p2 in Period_r : 
       p2 > p1 && p2 <= p1 + PERIODSPREAD_SPREAD
       )
      forall(edge in numberedEdge_set)
         ExamTime[edge.e1][p1] + ExamTime[edge.e2][p2] <= 
             1 + PenPeriodSpread_Edge[edge.ID];    

    // same for reversed edge
    forall(p1 in Period_r, p2 in Period_r : 
       p2 > p1 && p2 <= p1 + PERIODSPREAD_SPREAD
       )
      forall(edge in numberedEdge_set)
         ExamTime[edge.e2][p1] + ExamTime[edge.e1][p2] <= 
             1 + PenPeriodSpread_Edge[edge.ID];    

   RawPen_PeriodSpread == 
      sum(edge in numberedEdge_set) 
         edge.numStudents * PenPeriodSpread_Edge[edge.ID];

    // -------------- soft period penalties --------------

    // only used for reporting
    RawPen_SoftPeriodPenalties == 
      sum(p in Period_r) softPeriodPenalties[p] * sum(e in Exam_r) ExamTime[e][p];

    // -------------- soft room penalties --------------

     // only needed for reporting
     RawPen_SoftRoomPenalties == 
     ( sum(r in Room_r) softRoomPenalties[r] * sum(e in Exam_r) ExamRoom[e][r] );
      

   // ----- handle the NoMixedDuration Penalties -----------

   /* It is not so easy in IP to count the number of different durations.
      The following explcitly handles the different durations, 
      counts number of occcurences of each duration type.
      Converts the count to a boolean whether or not the duration is used
      Then puts a lower bound on penalty of "num_durations_used" - 1
   */
   
  forall(p in Period_r, r in Room_r)
      forall(dt in Duration_r)
         forall(e in Exam_r : examDurationType[e][dt] == 1 ) 
           UsedDuration[dt][p][r] >= ExamTime[e][p] + ExamRoom[e][r] - 1 ;

  forall(p in Period_r, r in Room_r)
         1 + PenNoMixedDuration[p][r] >= sum(dt in Duration_r) UsedDuration[dt][p][r];

   // just for reporting
   RawPen_NonMixedDuration ==   sum(p in Period_r, r in Room_r) PenNoMixedDuration[p][r];

    // -------------- front load penalties --------------

   // only used for reporting
    RawPen_FrontLoadPenalties == 
      sum(e in Exam_r, p in Period_r)  (
       examUseFrontLoad[e] * 
       periodUseFrontLoad[p] *
       ExamTime[e][p] );


} // end of 'subject to'


// TODO: also write out the penalties and check they match the other validators

execute {
   // write out the solution in format suitable for .sln file
  for(e in Exam_r)
  for(t in Period_r)
  for(r in Room_r)
    if ( ExamTimeRoom[e][t][r] > 0.99 ) // have to account for floating point errors
     // writeln("e= ",e," t= ",t," r= ",r);
     writeln(t,", ",r);
}
