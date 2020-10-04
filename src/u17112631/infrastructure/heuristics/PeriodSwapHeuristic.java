package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PeriodSwapHeuristic extends PerturbativeHeuristic {

    public PeriodSwapHeuristic() {
        super("d");
    }

    @Override
    public void makeChange() {

        List<Period> unsuitablePeriods = new ArrayList<>();
        //Get a period with a room that houses exams
        Period firstPeriod = getPeriodWithExams();
        Room roomOne = getRoomWithExams(firstPeriod);

        //Dont use first period for further searching
        unsuitablePeriods.add(firstPeriod);

        Period secondPeriod;
        while (unsuitablePeriods.size() != schedule.getPeriods().size()){

            //Search for a different period, which has the same room that also has exams
            secondPeriod = getPeriodWithExams(unsuitablePeriods);

            Room roomTwo = secondPeriod.getRoom(roomOne.getRoomNumber());

            if(swap(roomOne,roomTwo)){
                this.schedule.updatePeriod(firstPeriod);
                this.schedule.updatePeriod(secondPeriod);

                return;
            }
            else
                unsuitablePeriods.add(secondPeriod);

        }

    }

    private Period getPeriodWithExams(List<Period> unsuitablePeriods) {

        var periods = schedule.getPeriods();
        var unusedPeriodNumbers = IntStream.range(0,periods.size()).boxed().collect(Collectors.toList());

        //Remove periods that cannot be used
        unusedPeriodNumbers.removeIf(
                unusedPeriodNumber -> unsuitablePeriods.contains(periods.get(unusedPeriodNumber)));

        return getPeriodWithExams(periods, unusedPeriodNumbers, numGen.nextInt(unusedPeriodNumbers.size()));
    }

    private boolean swap(Room roomOne, Room roomTwo) {
        Exam firstExam = pickExam(roomOne);
        Exam secondExam = pickExam(roomTwo);

        if(firstExam == null || secondExam == null) return false;
        
        List<Exam> unsuitableExamsForSecondRoom = new ArrayList<>();
        List<Exam> unsuitableExamsForFirstRoom = new ArrayList<>();

        //While there are exams left to try in the first room
        boolean found = false;
        while(unsuitableExamsForFirstRoom.size() != roomOne.getNumberOfExams() && !found){
            
            //While there are options for the second room and no possible swap is found
            while(!canSwap(roomOne,roomTwo,firstExam,secondExam) && unsuitableExamsForSecondRoom.size() != roomTwo.getNumberOfExams()){
                unsuitableExamsForSecondRoom.add(secondExam);
                secondExam = pickExam(roomTwo,unsuitableExamsForSecondRoom);
            }

            //Solution found, we can exit
            if(canSwap(roomOne,roomTwo,firstExam,secondExam))
                found = true;
            else{
                unsuitableExamsForFirstRoom.add(firstExam);
                unsuitableExamsForSecondRoom.clear();
            }
        }

        //There are no possible swaps given these rooms
        if(!found)
            return false;

        roomOne.replace(firstExam,secondExam);
        roomTwo.replace(secondExam,firstExam);

        return true;
    }


}
