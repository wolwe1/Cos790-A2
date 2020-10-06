package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomSwapHeuristic extends PerturbativeHeuristic {


    public RoomSwapHeuristic() {
        super("b");
    }

    @Override
    public void makeChange() {

        List<Period> unsuitablePeriods = new ArrayList<>();
        List<Room> unsuitableRooms = new ArrayList<>();

        while (unsuitablePeriods.size() != schedule.getPeriods().size()){
            Period chosenPeriod = pickPeriodWithAnExamInMultipleRooms(unsuitablePeriods);
            if(chosenPeriod == null) return;
            unsuitablePeriods.add(chosenPeriod);

            Room roomOne = pickRoom(chosenPeriod);
            unsuitableRooms.add(roomOne);

            while (unsuitableRooms.size() != chosenPeriod.getNumberOfRooms()){
                Room roomTwo = pickRoom(chosenPeriod,unsuitableRooms);

                Exam examOne = pickExam(roomOne);
                Exam examTwo = pickExam(roomTwo);

                if(examOne != null && examTwo != null){
                    if(canSwap(roomOne,roomTwo,examOne,examTwo)){
                        roomOne.replace(examOne,examTwo);
                        roomTwo.replace(examTwo,examOne);

                        chosenPeriod.updateRoom(roomOne);
                        chosenPeriod.updateRoom(roomTwo);

                        schedule.updatePeriod(chosenPeriod);
                        return;
                    }
                }
                unsuitableRooms.add(roomTwo);
            }
            unsuitablePeriods.add(chosenPeriod);
            unsuitableRooms.clear();

        }
    }

    private Period pickPeriodWithAnExamInMultipleRooms(List<Period> unsuitablePeriods) {
        List<Period> unusablePeriods = new ArrayList<>(unsuitablePeriods);

        while (unusablePeriods.size() != schedule.getPeriods().size()){

            Period period = pickPeriod(unusablePeriods);

            if(periodHasExamsInDifferentRooms(period))
                return period;
            else
                unusablePeriods.add(period);
        }
        return null;
    }
}
