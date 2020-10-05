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
        List<Room> unusableRoomsForSourcing = new ArrayList<>();

        //While there are still periods to source from
        while(unusablePeriodsForSourcing.size() != schedule.getPeriods().size()){

            Period periodToPickFrom = pickPeriodWithExams(unusablePeriodsForSourcing);

            if(periodToPickFrom == null)
                return;

            while(unusableRoomsForSourcing.size() != periodToPickFrom.getNumberOfRooms()){
                Room roomToPickFrom = pickRoomWithExams(periodToPickFrom,unusableRoomsForSourcing);

                if(roomToPickFrom != null){
                    Exam examToMove = pickExam(roomToPickFrom);

                    //Try place exam in another period
                    boolean placedExams = placeExamInAnotherPeriod(periodToPickFrom,roomToPickFrom,examToMove);

                    if(placedExams){
                        //Placement succeeded, remove from source period
                        roomToPickFrom.removeExam(examToMove);
                        periodToPickFrom.updateRoom(roomToPickFrom);
                        schedule.updatePeriod(periodToPickFrom);

                        return;
                    }
                }

                unusableRoomsForSourcing.add(roomToPickFrom);
            }

            //Add period to not be used again
            unusablePeriodsForSourcing.add(periodToPickFrom);
        }

    }

    private Period pickPeriodWithExams(List<Period> unusablePeriods) {

        var periods = schedule.getPeriods();
        int chosenPeriod = numGen.nextInt(periods.size());

        while (unusablePeriods.contains(periods.get(chosenPeriod)) || periods.get(chosenPeriod).getNumberOfExams() == 0){
            periods.remove(periods.get(chosenPeriod));

            if(periods.size() == 0) return null;

            chosenPeriod = numGen.nextInt(periods.size());
        }

        return periods.get(chosenPeriod);
    }

    private boolean placeExamInAnotherPeriod(Period periodBeingMovedFrom, Room roomToPickFrom, Exam examToMove) {
        List<Period> unusablePeriodsForMoving = new ArrayList<>();
        unusablePeriodsForMoving.add(periodBeingMovedFrom);

        while(unusablePeriodsForMoving.size() != schedule.getPeriods().size()) {

            //Pick a differing period
            Period periodToMoveTo = pickPeriod(unusablePeriodsForMoving);
            Room roomToMoveTo = periodToMoveTo.getRoom(roomToPickFrom.getRoomNumber());

            //Can apply move
            if (roomToMoveTo.canFitExam(examToMove)){
                roomToMoveTo.placeExam(examToMove);
                periodToMoveTo.updateRoom(roomToMoveTo);
                schedule.updatePeriod(periodToMoveTo);
                return true;
            }
            unusablePeriodsForMoving.add(periodToMoveTo);

        }
        return false;
    }

    private Room pickRoomWithExams(Period period, List<Room> unsuitableRooms) {
        var rooms = period.getRooms();
        int chosenRoom = numGen.nextInt(rooms.size());

        while (unsuitableRooms.contains(rooms.get(chosenRoom)) || rooms.get(chosenRoom).getNumberOfExams() == 0){
            rooms.remove(rooms.get(chosenRoom));

            if(rooms.size() == 0) return null;

            chosenRoom = numGen.nextInt(rooms.size());
        }

        return rooms.get(chosenRoom);
    }
}
