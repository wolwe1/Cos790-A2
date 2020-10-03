package u17112631.dto.primitives;

import java.util.*;

public class Exam {

    private final int duration;
    private final List<Student> students;
    private final int examNumber;

    public Exam(String examInfo, int examNumber) {

        String[] info = examInfo.split(",");
        duration = Integer.parseInt(info[0]);

            students = new ArrayList<>();

        for (int i = 1; i < info.length; i++) {
            students.add( new Student(Integer.parseInt(info[i].strip())));
        }
        this.examNumber = examNumber;
    }

    public Exam(Exam other){
        this.examNumber = other.examNumber;
        this.duration = other.duration;
        this.students = new ArrayList<>();

        this.students.addAll(other.students);
    }

    /**
     * Only use as a wrapper
     * @param examNumber The exam wrapper to search for
     */
    public Exam(int examNumber){
        this.examNumber = examNumber;
        this.duration = -1;
        this.students = null;
    }


    public Exam getCopy() {
        return new Exam(this);
    }

    public int getExamNumber() {
        return this.examNumber;
    }

    public int getNumberOfStudents() {
        return this.students.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return examNumber == exam.examNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(examNumber);
    }

    public Integer getDuration() {
        return this.duration;
    }

    public List<Student> getStudents() {
        return this.students;
    }
}
