package u17112631.dto.primitives;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Period {



    private Date date;
    private int duration;
    private int penalty;
    private int periodNumber;
    private List<Room> rooms;

    public Period(String period, int periodNumber) throws ParseException {
        String[] info = period.split(",");

        date = new SimpleDateFormat("dd:MM:yy HH:mm:ss").parse(info[0] + info[1]);
        duration = Integer.parseInt(info[2].strip());
        penalty = Integer.parseInt(info[3].strip());
        this.periodNumber = periodNumber;
        rooms = new ArrayList<>();

    }

    protected Period(Period other){
        this.date = other.date;
        this.duration = other.duration;
        this.penalty = other.penalty;
        this.periodNumber = other.periodNumber;
        this.rooms = other.getRooms();
    }

    public int getPenalty() {
        return penalty;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public int getMaxRoomCapacity() {
        int maxCapacity = 0;

        for (Room room :
                rooms) {
            if(room.getCapacity() > maxCapacity)
                maxCapacity = room.getCapacity();
        }
        return maxCapacity;
    }


    public Date getDate() {
        return date;
    }

    public int getTotalRemainingCapacity() {
        int totalCapacity = 0;

        for (Room room : rooms) {
            totalCapacity += room.getCapacity();
        }

        return totalCapacity;
    }

    public Period getCopy() {
        return new Period(this);
    }

    public int getNumberOfExams() {

        int totalExams = 0;

        for (Room room : rooms) {
            totalExams += room.getNumExams();
        }

        return totalExams;
    }

    public void replaceExam(Exam examOne, Exam examTwo) {

        Room roomOne = findRoomWithExam(examOne);
        Room roomTwo = findRoomWithExam(examTwo);

        if(roomOne.getRoomNumber() == roomTwo.getRoomNumber()){
            roomOne.swap(examOne,examTwo);
        }else{
            roomOne.replace(examOne,examTwo);
            roomTwo.replace(examTwo,examOne);
        }
    }

    private Room findRoomWithExam(Exam exam) {

        for (Room room : rooms) {
            if(room.containsExam(exam))
                return room;
        }

        throw new RuntimeException("Period cannot find room with that exam");
    }


    public Exam getExam(int examIndex) {

        int previousExams = 0;

        for (Room room : rooms) {

            if(examIndex < room.getNumExams() + previousExams)
                return room.getExam(examIndex - previousExams);
            else
                previousExams += room.getNumExams();
        }

        throw new RuntimeException("Period does not contain exam index");
    }

    public int getNumberOfRooms() {
        return this.rooms.size();
    }

    public Room getRoom(int roomIndex) {

        if(roomIndex < 0 || roomIndex >= this.rooms.size()) throw new RuntimeException("Attempted to access room that does not exist in period");

        return this.rooms.get(roomIndex).getCopy();
    }

    public void updateRoom(Room updatedRoom) {

        int roomIndex = getRoomIndex(updatedRoom);

        this.rooms.set(roomIndex,updatedRoom);
    }

    public int getRoomIndex(Room room){

        for (int i = 0; i < this.rooms.size(); i++) {

            if(this.rooms.get(i).getRoomNumber() == room.getRoomNumber())
                return i;
        }

        throw new RuntimeException("Room not found in period");
    }
}
