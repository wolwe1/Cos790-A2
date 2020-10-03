package u17112631.dto.primitives;

import java.util.Objects;

public class Student {

    private final int studentNumber;

    public Student(int number) {
        this.studentNumber = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentNumber == student.studentNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }
}
