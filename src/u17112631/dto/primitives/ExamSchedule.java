package u17112631.dto.primitives;

import java.util.ArrayList;
import java.util.List;

public class ExamSchedule {

    List<Period> periods;
    int numberOfExams;

    public ExamSchedule(List<Period> periods){
        this.periods = periods;
        numberOfExams = 0;
    }

    public ExamSchedule(ExamSchedule other) {
        this.periods = other.getPeriods();
        this.numberOfExams = other.numberOfExams;
    }

    public List<Period> getPeriods() {

        List<Period> periods = new ArrayList<>();

        for (Period period : this.periods) {
            periods.add(period.getCopy());
        }

        return periods;
    }

    public int getNumberOfPeriods() {
        return this.periods.size();
    }

    public Period getPeriod(int chosenPeriod) {

        if(chosenPeriod < 0 || chosenPeriod >= this.periods.size()) throw new RuntimeException();

        return this.periods.get(chosenPeriod);
    }

    public void updatePeriod(Period updatedPeriod) {

        int periodIndex = -1;

        for (int i = 0; i < this.periods.size(); i++) {
            if(this.periods.get(i).getPeriodNumber() == updatedPeriod.getPeriodNumber())
                periodIndex = i;
        }

        this.periods.set(periodIndex,updatedPeriod);

        //Update exam count
        int numExams = 0;
        for (Period period : periods) {
            numExams += period.getNumberOfExams();
        }
        this.numberOfExams = numExams;
    }

    public ExamSchedule getCopy() {
        return new ExamSchedule(this);
    }
}
