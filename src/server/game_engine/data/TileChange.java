package server.game_engine.data;

public class TileChange extends Change {
    protected boolean nameChange;
    protected boolean typeChange;

    public TileChange() {
        this.nameChange = false;
        this.typeChange = false;
    }

    public TileChange(ChangeType type) {
        this.type = type;
        switch (type) {
        case FULL_CHANGE:
        case NEW:
        case REMOVED:
        case BASIC_CHANGE:
            this.typeChange = true;
            this.nameChange = true;
            break;
        case NONE:
            break;
        }
    }

    public boolean isNameChange() {
        return nameChange;
    }

    public void nameChanged() {
        if (type != ChangeType.FULL_CHANGE)
            type = ChangeType.FULL_CHANGE;
        this.nameChange = true;
    }

    public boolean isTypeChange() {
        return typeChange;
    }

    public void typeChanged() {
        if (type != ChangeType.FULL_CHANGE)
            type = ChangeType.FULL_CHANGE;
        this.typeChange = true;
    }

}
