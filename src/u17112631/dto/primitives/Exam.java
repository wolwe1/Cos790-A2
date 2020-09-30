package u17112631.dto.primitives;

import java.util.*;

public class Exam {

    private final int duration;
    private final Map<Student,Boolean> students;
    private final int examNumber;

    public Exam(String examInfo, int examNumber) {

        String[] info = examInfo.split(",");
        duration = Integer.parseInt(info[0]);

            students = new HashMap<>();

        for (int i = 1; i < info.length; i++) {
            students.put( new Student(Integer.parseInt(info[i].strip())),false);
        }
        this.examNumber = examNumber;
    }

    public Exam(Exam other){
        this.examNumber = other.examNumber;
        this.duration = other.duration;
        this.students = new HashMap<>();

        for (Map.Entry<Student, Boolean> student : other.students.entrySet()) {
            this.students.put(student.getKey(),student.getValue());
        }
    }


    public Exam getCopy() {
        return new Exam(this);
    }

    public int getExamNumber() {
        return this.examNumber;
    }
}
