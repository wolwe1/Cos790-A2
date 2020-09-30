package u17112631.infrastructure.heuristics;

import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamSchedule;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.util.Random;

public abstract class PerturbativeHeuristic {

    protected final String id;
    protected ExamSchedule schedule;
    protected Random numGen;

    protected PerturbativeHeuristic(String id){
        this.id = id;
        this.numGen = new Random(0);
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

    protected Period pickPeriod() {

        int numPeriods = schedule.getNumberOfPeriods();
        int chosenPeriod = numGen.nextInt(numPeriods);

        return schedule.getPeriod(chosenPeriod);
    }

    protected Exam pickExam(Period chosenPeriod) {

        int numExamsInPeriod = chosenPeriod.getNumberOfExams();
        int examOne = numGen.nextInt(numExamsInPeriod);

        return chosenPeriod.getExam(examOne);
    }

    protected Exam pickExam(Room room) {

        int numExams = room.getNumExams();
        int chosenExam = numGen.nextInt(numExams);

        return room.getExam(chosenExam);
    }

    protected Room pickRoom(Period chosenPeriod) {

        int numRooms = chosenPeriod.getNumberOfRooms();
        int chosenRoom = numGen.nextInt(numRooms);

        return chosenPeriod.getRoom(chosenRoom);
    }
}
