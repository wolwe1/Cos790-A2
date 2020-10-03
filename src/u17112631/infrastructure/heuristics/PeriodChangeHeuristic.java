package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.ArrayList;
import java.util.List;

public class PeriodChangeHeuristic extends PerturbativeHeuristic {

    public PeriodChangeHeuristic() {
        super("c");
    }

    @Override
    public void makeChange() {

        List<Period> unusablePeriodsForSourcing = new ArrayList<>();
        List<Period> unusablePeriodsForMoving = new ArrayList<>();
        List<Room> unusableRoomsForSourcing = new ArrayList<>();

        Period periodToPickFrom = pickPeriod();
        Room roomToPickFrom = pickRoom(periodToPickFrom);
        Exam examToMove = pickExam(roomToPickFrom);

        unusablePeriodsForMoving.add(periodToPickFrom);

        //While there are still periods to source from
        while(unusablePeriodsForSourcing.size() != schedule.getPeriods().size()){

            while(unusablePeriodsForMoving.size() != schedule.getPeriods().size() - 1){
                //Pick a differing period
                Period periodToMoveTo = pickPeriod(unusablePeriodsForMoving);

                while(unusableRoomsForSourcing.size() != periodToPickFrom.getRooms().size()){
                    Room roomToMoveTo = periodToMoveTo.getRoom(roomToPickFrom.getRoomNumber());

                    //Can apply move
                    if(roomToMoveTo.canFitExam(examToMove)){

                        //update the receiving entities
                        roomToMoveTo.placeExam(examToMove);
                        periodToMoveTo.updateRoom(roomToMoveTo);
                        schedule.updatePeriod(periodToMoveTo);

                        //update the entities losing items
                        roomToPickFrom.removeExam(examToMove);
                        periodToPickFrom.updateRoom(roomToPickFrom);
                        schedule.updatePeriod(periodToPickFrom);
                        return;
                    }

                    unusableRoomsForSourcing.add(roomToPickFrom);
                    roomToPickFrom = pickRoom(periodToPickFrom,unusableRoomsForSourcing);
                    examToMove = pickExam(roomToPickFrom);
                }
                unusablePeriodsForMoving.add(periodToMoveTo);
                unusableRoomsForSourcing.clear();
            }

            //Add period to not be used again
            unusablePeriodsForSourcing.add(periodToPickFrom);

            //Set next rounds source
            periodToPickFrom = pickPeriod(unusablePeriodsForSourcing);
            roomToPickFrom = pickRoom(periodToPickFrom);
            examToMove = pickExam(roomToPickFrom);

            //Clear the secondary set
            unusablePeriodsForMoving.clear();
        }

    }
}
