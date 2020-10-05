package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomChangeHeuristic extends PerturbativeHeuristic {

    public RoomChangeHeuristic() {
        super("a");
    }

    @Override
    public void makeChange() {

        List<Period> unusablePeriodsForSourcing = new ArrayList<>();
        List<Room> unusableRoomsForMoving = new ArrayList<>();
        List<Room> unusableRoomsForSourcing = new ArrayList<>();
        List<Exam> unusableExams= new ArrayList<>();

        Period periodToPickFrom = pickPeriodWithExams();
        Room roomToPickFrom = pickRoomWithExams(periodToPickFrom);
        Exam examToMove = pickExam(roomToPickFrom);

        unusableRoomsForMoving.add(roomToPickFrom);

        //While there are still periods to source from
        while(unusablePeriodsForSourcing.size() != schedule.getPeriods().size()){

            //While there are rooms in that period to source from
            while(unusableRoomsForSourcing.size() != periodToPickFrom.getRooms().size()){

                //While there are exams left in the room
                while(unusableExams.size() != roomToPickFrom.getExams().size()){

                    //While there are other rooms to move an exam to
                    while(unusableRoomsForMoving.size() != periodToPickFrom.getRooms().size()){

                        Room roomToMoveTo = pickRoom(periodToPickFrom,unusableRoomsForMoving);
                        //Can apply move
                        if(roomToMoveTo.canFitExam(examToMove)){

                            //update the receiving room
                            roomToMoveTo.placeExam(examToMove);
                            periodToPickFrom.updateRoom(roomToMoveTo);

                            //update the losing room
                            roomToPickFrom.removeExam(examToMove);
                            periodToPickFrom.updateRoom(roomToPickFrom);
                            schedule.updatePeriod(periodToPickFrom);
                            return;
                        }
                        unusableRoomsForMoving.add(roomToMoveTo);
                    }

                    //Add exam to no usage list, select next one and reset unusable rooms
                    unusableExams.add(examToMove);
                    examToMove = pickExam(roomToPickFrom,unusableExams);
                    unusableRoomsForMoving.clear();
                    unusableRoomsForMoving.add(roomToPickFrom);
                }
                //Add room to no usage list, get next one and reset inner variables
                unusableRoomsForSourcing.add(roomToPickFrom);
                roomToPickFrom = pickRoom(periodToPickFrom,unusableRoomsForSourcing);
                unusableExams.clear();
                unusableRoomsForMoving.clear();
                unusableRoomsForMoving.add(roomToPickFrom);
            }

            unusablePeriodsForSourcing.add(periodToPickFrom);
            periodToPickFrom = pickPeriod(unusablePeriodsForSourcing);

            unusableRoomsForSourcing.clear();
            unusableRoomsForMoving.clear();
            unusableExams.clear();

            //Set next rounds source
            periodToPickFrom = pickPeriod(unusablePeriodsForSourcing);
            roomToPickFrom = pickRoom(periodToPickFrom);
            examToMove = pickExam(roomToPickFrom);

            //Clear the secondary set
            unusableRoomsForMoving.clear();
        }

    }
}
