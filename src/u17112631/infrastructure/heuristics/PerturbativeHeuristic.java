package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class PerturbativeHeuristic {

    protected final String id;
    protected ExamSchedule schedule;
    protected Random numGen;

    protected PerturbativeHeuristic(String id){
        this.id = id;
        this.numGen = new Random(0);
    }

    public void setSeed(long seed){
        this.numGen.setSeed(seed);
    }

    protected Period pickPeriodWithExams(List<Period> periods, List<Integer> unusedPeriodNumbers, int i) {
        int chosenPeriod = i;

        while (periods.get(unusedPeriodNumbers.get(chosenPeriod)).getNumberOfExams() == 0){
            unusedPeriodNumbers.remove(chosenPeriod);

            if(unusedPeriodNumbers.size() == 0)
                return null;

            chosenPeriod = numGen.nextInt(unusedPeriodNumbers.size());
        }

        return periods.get(unusedPeriodNumbers.get(chosenPeriod));
    }

    public void setSchedule(ExamSchedule schedule){
        this.schedule = schedule;
    }

    public abstract void makeChange();

    public ExamSchedule getSchedule(){
        return this.schedule;
    }

    public String getId(){
        return this.id;
    }

    /**
     * Selects a random period from the schedule
     * @return A random period
     */
    protected Period pickPeriod() {

        int numPeriods = schedule.getNumberOfPeriods();
        int chosenPeriod = numGen.nextInt(numPeriods);

        return schedule.getPeriod(chosenPeriod);
    }

    /**
     * Selects a random exam given a period
     * @param chosenPeriod The period to pick from
     * @return A random exam in the given period
     */
    protected Exam pickExam(Period chosenPeriod) {

        int numExamsInPeriod = chosenPeriod.getNumberOfExams();
        if(numExamsInPeriod == 0)
            return null;

        int examOne = numGen.nextInt(numExamsInPeriod);

        return chosenPeriod.getExamByIndex(examOne);
    }

    /**
     * Selects a random exam from a given room
     * @param room The room to pick from
     * @return A random exam from the provided room
     */
    protected Exam pickExam(Room room) {

        int numExams = room.getNumberOfExams();

        if(numExams == 0)
            return null;

        int chosenExam = numGen.nextInt(numExams);

        return room.getExamByIndex(chosenExam);
    }

    /**
     * Selects a random room from a provided period
     * @param chosenPeriod The period to select from
     * @return A random room in the provided period
     */
    protected Room pickRoom(Period chosenPeriod) {

        int numRooms = chosenPeriod.getNumberOfRooms();
        int chosenRoom = numGen.nextInt(numRooms);

        return chosenPeriod.getRoomByIndex(chosenRoom);
    }

    /**
     * Selects a random room from the given period, the room must contain an exam
     * @param period The period to select from
     * @return A room with exams from the provided period
     */
    protected Room pickRoomWithExams(Period period) {
        var rooms = period.getRooms();
        var unusedRoomNumbers = IntStream.range(0,rooms.size()).boxed().collect(Collectors.toList());

        int chosenRoom = numGen.nextInt(unusedRoomNumbers.size());

        while (rooms.get(unusedRoomNumbers.get(chosenRoom)).getNumberOfExams() == 0){
            unusedRoomNumbers.remove(chosenRoom);

            if(unusedRoomNumbers.size() == 0)
                throw new RuntimeException("There are no rooms in period with exams");

            chosenRoom = numGen.nextInt(unusedRoomNumbers.size());
        }

        return rooms.get(unusedRoomNumbers.get(chosenRoom));
    }

    /**
     * Selects a random period from the schedule, the period must have exams
     * @return A random period that has exams
     */
    protected Period pickPeriodWithExams() {

        var periods = schedule.getPeriods();
        var unusedPeriodNumbers = IntStream.range(0,periods.size()).boxed().collect(Collectors.toList());

        return pickPeriodWithExams(periods, unusedPeriodNumbers, numGen.nextInt(unusedPeriodNumbers.size()));

    }

    /**
     * Selects a random exam from the given room, exams in the list provided will not be chosen
     * @param room The room to select from
     * @param unsuitableExams A list of exams to be excluded
     * @return A random exam from the room and not in the ignored list
     */
    protected Exam pickExam(Room room, List<Exam> unsuitableExams) {

        List<Exam> exams = room.getExams();
        int examIndex = numGen.nextInt(exams.size());

        while ( unsuitableExams.contains(exams.get(examIndex))){
            exams.remove(exams.get(examIndex));

            if(exams.size() == 0) {
                return null;
            }

            examIndex = numGen.nextInt(exams.size());
        }

        return exams.get(examIndex);
    }

    /**
     * Selects a period from the schedule that does not exist in the list
     * @param unusablePeriods A list of non-usable periods
     * @return A random period not in the list
     */
    protected Period pickPeriod(List<Period> unusablePeriods) {

        var periods = schedule.getPeriods();
        int chosenPeriod = numGen.nextInt(periods.size());

        while (unusablePeriods.contains(periods.get(chosenPeriod))){
            periods.remove(periods.get(chosenPeriod));

            if(periods.size() == 0) throw new RuntimeException("Unable to pick period");

            chosenPeriod = numGen.nextInt(periods.size());
        }

        return periods.get(chosenPeriod);
    }


    /**
     * Selects a random room from the period but not in the provided list
     * @param period The period to select from
     * @param unsuitableRooms The list of rooms not to include
     * @return A random room from the period,not in the list
     */
    protected Room pickRoom(Period period, List<Room> unsuitableRooms) {

        var rooms = period.getRooms();
        int chosenRoom = numGen.nextInt(rooms.size());

        while (unsuitableRooms.contains(rooms.get(chosenRoom))){
            rooms.remove(rooms.get(chosenRoom));

            if(rooms.size() == 0) throw new RuntimeException("Unable to pick room from period");

            chosenRoom = numGen.nextInt(rooms.size());
        }

        return rooms.get(chosenRoom);
    }

    protected boolean periodHasExamsInDifferentRooms(Period period) {

        if(period.getNumberOfExams() > 1){
            var rooms = period.getRooms();
            boolean foundOne = false;

            for (Room room : rooms) {
                if(room.getNumberOfExams() > 0 && !foundOne)
                    foundOne = true;
                else if(room.getNumberOfExams() > 0 && foundOne)
                    return true;
                else
                    foundOne = false;
            }
        }
        return false;
    }

    protected boolean canSwap(Room roomOne, Room roomTwo, Exam firstExam, Exam secondExam) {

        if(firstExam == null || secondExam == null)
            return false;

        int roomOneCapacityWithoutExam = roomOne.getCapacity() + firstExam.getNumberOfStudents();
        int roomTwoCapacityWithoutExam = roomTwo.getCapacity() + secondExam.getNumberOfStudents();

        return secondExam.getNumberOfStudents() <= roomOneCapacityWithoutExam && firstExam.getNumberOfStudents() <= roomTwoCapacityWithoutExam;
    }

    protected Room pickRoomWithExams(Period period, List<Room> unsuitableRooms) {
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
