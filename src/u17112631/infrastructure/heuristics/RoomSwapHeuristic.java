package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

public class RoomSwapHeuristic extends PerturbativeHeuristic {


    public RoomSwapHeuristic() {
        super("r");
    }

    @Override
    public void makeChange() {

        Period chosenPeriod = pickPeriod();

        Room roomOne = pickRoom(chosenPeriod);
        Room roomTwo = pickRoom(chosenPeriod);

        //Ensure room differs
        while(roomTwo.getRoomNumber() == roomOne.getRoomNumber()){
            pickRoom(chosenPeriod);
        }

        Exam examOne = pickExam(roomOne);
        Exam examTwo = pickExam(roomTwo);

        roomOne.replace(examOne,examTwo);
        roomOne.replace(examTwo,examOne);

        chosenPeriod.updateRoom(roomOne);
        chosenPeriod.updateRoom(roomTwo);

        schedule.updatePeriod(chosenPeriod);

    }

}
