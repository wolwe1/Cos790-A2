package u17112631.infrastructure.interfaces;

import u17112631.dto.primitives.ExamSchedule;

public interface IMoveAccepter {

    void setSchedule(ExamSchedule bestSchedule);

    boolean acceptsChange(ExamSchedule schedule);
}
