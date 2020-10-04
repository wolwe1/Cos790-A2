package u17112631.dto.primitives;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Room {

    private int capacity;
    private int penalty;
    private final int roomNumber;
    private final List<Exam> exams;

    public Room(String room, int roomNumber) {

        String[] info = room.split(",");

        capacity = Integer.parseInt(info[0].strip());
        penalty = Integer.parseInt(info[1].strip());
        this.roomNumber = roomNumber;

        exams = new ArrayList<>();
    }

    protected Room(Room other){
        this.capacity = other.capacity;
        this.penalty = other.penalty;
        this.roomNumber = other.roomNumber;

        exams = other.getExams();
    }

    public List<Exam> getExams() {
        List<Exam> exams = new ArrayList<>();

        for (Exam exam : this.exams) {
            exams.add( exam.getCopy());
        }

        return exams;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int getRoomNumber() {
        return roomNumber;
    }


    public Room getCopy() {
        return new Room(this);
    }

    public int getNumberOfExams() {
        return this.exams.size();
    }

    public Exam getExamByIndex(int i) {

        if(i < 0 || i >= this.exams.size()) throw new RuntimeException("Attempted to access exam out of range");

        return this.exams.get(i).getCopy();
    }

    public boolean containsExam(Exam examToLookFor) {

        return containsExam(examToLookFor.getExamNumber());
    }
    public boolean containsExam(int examNumber) {

        for (Exam exam : exams) {

            if(exam.getExamNumber() == examNumber)
                return true;
        }

        return false;
    }

    public void replace(Exam examToReplace, Exam newExam) {

        if(examToReplace.getExamNumber() == newExam.getExamNumber()) return;

        int replaceIndex = getExamIndex(examToReplace);

        exams.set(replaceIndex,newExam);
    }

    public void swap(Exam examOne, Exam examTwo) {

        int firstIndex = getExamIndex(examOne);
        int secondIndex = getExamIndex(examTwo);

        exams.set(firstIndex,examTwo);
        exams.set(secondIndex,examOne);

    }

    private int getExamIndex(Exam exam){

        int replaceIndex = -1;

        for (int i = 0; i < exams.size(); i++) {
            if(exams.get(i).getExamNumber() == exam.getExamNumber())
                replaceIndex = i;
        }

        return replaceIndex;
    }

    public boolean canFitExam(Exam exam) {
        return exam.getNumberOfStudents() <= capacity;
    }

    public void placeExam(Exam exam) {
        exams.add(exam);
        this.capacity -= exam.getNumberOfStudents();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomNumber == room.roomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    public void removeExam(Exam examToRemove) {
        this.exams.remove(examToRemove);
    }

    public List<Student> getStudents() {
        List<Student> studentsWritingInRoom = new ArrayList<>();
        for (Exam exam : exams) {
            studentsWritingInRoom.addAll(exam.getStudents());
        }
        return studentsWritingInRoom;
    }

    public boolean hasNoStudentConflict(Exam exam) {
        var studentsAlreadyInPeriod = getStudents();
        return !exam.hasStudents(studentsAlreadyInPeriod);
    }
}
