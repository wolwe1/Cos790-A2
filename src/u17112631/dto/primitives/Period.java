package u17112631.dto.primitives;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Period {



    private final Date date;
    private final int duration;
    private final int penalty;
    private final int periodNumber;
    private List<Room> rooms;
    private int numberOfExams;
    private Set<Student> studentsInPeriod;

    public Period(String period, int periodNumber) throws ParseException {
        String[] info = period.split(",");

        this.date = new SimpleDateFormat("dd:MM:yy HH:mm:ss").parse(info[0] + info[1]);
        this.duration = Integer.parseInt(info[2].strip());
        this.penalty = Integer.parseInt(info[3].strip());
        this.periodNumber = periodNumber;
        this.rooms = new ArrayList<>();
        this.numberOfExams = 0;
        this.studentsInPeriod = new HashSet<>();
    }

    protected Period(Period other){
        this.date = other.date;
        this.duration = other.duration;
        this.penalty = other.penalty;
        this.periodNumber = other.periodNumber;
        this.rooms = other.getRooms();
        numberOfExams = 0;
        this.studentsInPeriod = new HashSet<>(other.studentsInPeriod);
    }

    public int getPenalty() {
        return penalty;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public List<Room> getRooms() {
        List<Room> roomCopies = new ArrayList<>();

        for (Room room : rooms) {
            roomCopies.add(room.getCopy());
        }
        return roomCopies;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;

        int numExams = 0;
        for (Room room : rooms) {
            numExams += room.getNumberOfExams();
        }
        numberOfExams = numExams;
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
            totalExams += room.getNumberOfExams();
        }

        return totalExams;
    }

    private Room findRoomWithExam(Exam exam) {

        for (Room room : rooms) {
            if(room.containsExam(exam))
                return room;
        }

        throw new RuntimeException("Period cannot find room with that exam");
    }


    public Exam getExamByIndex(int examIndex) {

        int previousExams = 0;

        for (Room room : rooms) {

            if(examIndex < room.getNumberOfExams() + previousExams)
                return room.getExamByIndex(examIndex - previousExams);
            else
                previousExams += room.getNumberOfExams();
        }

        throw new RuntimeException("Period does not contain exam index");
    }

    public Exam getExam(int examNumber){
        for (Room room : rooms) {
            if(room.containsExam(examNumber))
                return room.getExams(examNumber);
        }

        throw new RuntimeException("Exam does not exist in period");
    }

    public int getNumberOfRooms() {
        return this.rooms.size();
    }

    public Room getRoomByIndex(int roomIndex) {

        if(roomIndex < 0 || roomIndex >= this.rooms.size()) throw new RuntimeException("Attempted to access room that does not exist in period");

        return this.rooms.get(roomIndex).getCopy();
    }

    public void updateRoom(Room updatedRoom) {

        int roomIndex = getRoomIndex(updatedRoom);

        this.rooms.set(roomIndex,updatedRoom);

        int numExams = 0;
        for (Room room : rooms) {
            numExams += room.getNumberOfExams();
        }
        numberOfExams = numExams;
    }

    public int getRoomIndex(Room room){

        for (int i = 0; i < this.rooms.size(); i++) {

            if(this.rooms.get(i).getRoomNumber() == room.getRoomNumber())
                return i;
        }

        throw new RuntimeException("Room not found in period");
    }

    public boolean canFitExam(Exam exam) {

        for (Room room : rooms) {
            if(room.canFitExam(exam))
                return true;
        }
        return false;
    }

    public boolean containsExam(int examOne) {
        for (Room room : rooms) {
            if(room.containsExam(examOne))
                return true;
        }

        return false;
    }

    public Room findRoomWithExam(int examNumber){
        return findRoomWithExam(new Exam(examNumber));
    }

    public Room getRoom(int roomNumber) {

        for (Room room : rooms) {
            if(room.getRoomNumber() == roomNumber)
                return room.getCopy();
        }

        throw new RuntimeException("Room not found in period");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return periodNumber == period.periodNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodNumber);
    }

    public List<Exam> getExams() {

        List<Exam> examsInPeriod = new ArrayList<>();
        for (Room room : rooms) {
            examsInPeriod.addAll(room.getExams());
        }
        return examsInPeriod;
    }

    public int GetNumberOfSharedStudents(Period other) {

        var studentsInThisPeriod = getStudents();
        var studentsInOtherPeriod = other.getStudents();

        var sharedStudents = new ArrayList<>(studentsInThisPeriod);
        sharedStudents.retainAll(studentsInOtherPeriod);

        return sharedStudents.size();
    }

    public List<Student> getStudents(){

        List<Student> studentsInPeriod = new ArrayList<>();
        for (Room room : rooms) {
            studentsInPeriod.addAll(room.getStudents());
        }
        return studentsInPeriod;
    }

    public boolean hasNoStudentConflict(Exam exam) {
        var studentsAlreadyInPeriod = getStudents();
        return !exam.hasStudents(studentsAlreadyInPeriod);
    }

    public boolean containsExam(Exam exam) {
        return containsExam(exam.getExamNumber());
    }
}
