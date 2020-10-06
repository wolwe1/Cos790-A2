package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomChangeHeuristic extends PerturbativeHeuristic {

    public RoomChangeHeuristic() {
        super("a");
    }

    @Override
    public void makeChange() {

        List<Period> unusablePeriodsForSourcing = new ArrayList<>();
        List<Room> unusableRoomsForSourcing = new ArrayList<>();

        //While there are still periods to source from
        while(unusablePeriodsForSourcing.size() != schedule.getPeriods().size()){

            Period periodToPickFrom = pickPeriodWithExams(unusablePeriodsForSourcing);
            if(periodToPickFrom == null) return;

            //While there are rooms in that period to source from
            while(unusableRoomsForSourcing.size() != periodToPickFrom.getRooms().size()){

                Room roomToPickFrom = pickRoomWithExams(periodToPickFrom,unusableRoomsForSourcing);
                if(attemptToMoveExamFromRoomToAnother(periodToPickFrom,roomToPickFrom)) {
                    return;
                }

                unusableRoomsForSourcing.add(roomToPickFrom);
            }

            unusablePeriodsForSourcing.add(periodToPickFrom);
        }

    }

    private boolean attemptToMoveExamFromRoomToAnother(Period periodToPickFrom, Room roomToPickFrom) {
        List<Exam> unusableExams = new ArrayList<>();

        while (unusableExams.size() != roomToPickFrom.getExams().size()) {
            Exam examToMove = pickExam(roomToPickFrom, unusableExams);
            //While there are other rooms to move an exam to
            if (attemptToMoveExamToOtherRoom(periodToPickFrom, roomToPickFrom, examToMove)) {
                //update the losing room
                roomToPickFrom.removeExam(examToMove);
                periodToPickFrom.updateRoom(roomToPickFrom);
                schedule.updatePeriod(periodToPickFrom);

                return true;
            }
            unusableExams.add(examToMove);
        }
        return false;
    }

    private Period pickPeriodWithExams(List<Period> unusablePeriodsForSourcing) {
        var allPeriods = schedule.getPeriods();
        allPeriods = allPeriods.stream().filter((p)-> !unusablePeriodsForSourcing.contains(p)).collect(Collectors.toList());

        int chosenPeriod = numGen.nextInt(allPeriods.size());

        while (allPeriods.get(chosenPeriod).getNumberOfExams() == 0){
            allPeriods.remove(chosenPeriod);

            if(allPeriods.size() == 0)
                return null;

            chosenPeriod = numGen.nextInt(allPeriods.size());
        }

        return null;
    }

    private boolean attemptToMoveExamToOtherRoom(Period period, Room currentRoom, Exam exam) {
        List<Room> unusableRoomsForMoving = new ArrayList<>();
        unusableRoomsForMoving.add(currentRoom);

        while(unusableRoomsForMoving.size() != period.getRooms().size()){

            Room roomToMoveTo = pickRoom(period,unusableRoomsForMoving);
            //Can apply move
            if(roomToMoveTo.canFitExam(exam)){

                //update the receiving room
                roomToMoveTo.placeExam(exam);
                period.updateRoom(roomToMoveTo);

                return true;
            }
            unusableRoomsForMoving.add(roomToMoveTo);
        }
        return false;
    }
}
