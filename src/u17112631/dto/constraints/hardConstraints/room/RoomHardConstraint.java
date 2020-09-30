package u17112631.dto.constraints.hardConstraints.room;

public class RoomHardConstraint {

    private final int _room;
    private IRoomHardConstraintRule _rule;

    public RoomHardConstraint(String input) {
        String[] info = input.split(",");
        _room = Integer.parseInt(info[0]);
    }
}
