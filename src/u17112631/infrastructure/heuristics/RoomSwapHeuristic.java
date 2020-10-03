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

        Period chosenPeriod = pickPeriodWithAnExamInMultipleRooms();
        unsuitablePeriods.add(chosenPeriod);

        while (unsuitablePeriods.size() != schedule.getPeriods().size()){
            Room roomOne = pickRoom(chosenPeriod);
            unsuitableRooms.add(roomOne);

            Room roomTwo = pickRoom(chosenPeriod,unsuitableRooms);

            Exam examOne = pickExam(roomOne);
            Exam examTwo = pickExam(roomTwo);

            if(canSwap(roomOne,roomTwo,examOne,examTwo)){
                roomOne.replace(examOne,examTwo);
                roomOne.replace(examTwo,examOne);

                chosenPeriod.updateRoom(roomOne);
                chosenPeriod.updateRoom(roomTwo);

                schedule.updatePeriod(chosenPeriod);
                return;
            }
        }
    }

    private Period pickPeriodWithAnExamInMultipleRooms() {

        List<Period> unusablePeriods = new ArrayList<>();

        while (unusablePeriods.size() != schedule.getPeriods().size()){

            Period period = pickPeriod(unusablePeriods);

            if(periodHasExamsInDifferentRooms(period))
                return period;
            else
                unusablePeriods.add(period);
        }
        throw new RuntimeException("There are no periods with exams in multiple rooms");
    }


}
