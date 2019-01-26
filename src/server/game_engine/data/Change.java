package server.game_engine.data;

public abstract class Change {
    protected ChangeType type;

    Change() {
        this.type = ChangeType.NONE;
    }

    public ChangeType getType() {
        return type;
    }

}
