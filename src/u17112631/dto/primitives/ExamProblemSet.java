package u17112631.dto.primitives;

import u17112631.dto.constraints.InstitutionalWeighting;
import u17112631.dto.constraints.hardConstraints.period.PeriodHardConstraint;
import u17112631.dto.constraints.hardConstraints.room.RoomHardConstraint;

import java.util.ArrayList;
import java.util.List;

public class ExamProblemSet{

    List<Exam> exams;
    List<Period> periods;
    List<Room> rooms;
    List<PeriodHardConstraint> periodHardConstraints;
    List<RoomHardConstraint> roomHardConstraints;
    List<InstitutionalWeighting> softConstraints;

    public List<Exam> getExams() {
        List<Exam> examCopies = new ArrayList<>();

        for (Exam exam : exams) {
            examCopies.add( exam.getCopy());
        }
        return examCopies;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    public List<Period> getPeriods() {

        List<Period> periodCopies = new ArrayList<>();

        for (Period period : periods) {
            periodCopies.add( period.getCopy());
        }
        return periodCopies;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void setPeriodHardConstraints(List<PeriodHardConstraint> _periodHardConstrains) {
        this.periodHardConstraints = _periodHardConstrains;
    }

    public List<RoomHardConstraint> getRoomHardConstraints() {
        return roomHardConstraints;
    }

    public void set_roomHardConstraints(List<RoomHardConstraint> _roomHardConstrains) {
        this.roomHardConstraints = _roomHardConstrains;
    }

    public List<InstitutionalWeighting> getSoftConstraints() {
        return softConstraints;
    }

    public void setSoftConstraints(List<InstitutionalWeighting> softConstraints) {
        this.softConstraints = softConstraints;
    }

    //Constraints
    public PeriodHardConstraint ExamConstrained(Exam exam){

        for (PeriodHardConstraint periodHardConstraint : periodHardConstraints) {

            if(periodHardConstraint.contains(exam))
                return periodHardConstraint;
        }
        return null;
    }


    public Room get_room(Integer roomNumber) {
        var rooms = this.rooms;

        for (Room room : rooms) {
            if(room.getRoomNumber() == roomNumber)
                return room.getCopy();
        }

        return null;
    }
}
