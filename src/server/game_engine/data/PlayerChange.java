package server.game_engine.data;

public class PlayerChange extends Change {
    protected boolean healthChange;
    protected boolean poseChange;
    protected boolean maxHealthChange;
    protected boolean speedChange;
    protected boolean currentItemChange;
    protected boolean scoreChange;
    protected boolean teamChange;
    protected boolean itemsChange;

    public PlayerChange() {
        this.healthChange = false;
        this.poseChange = false;
        this.maxHealthChange = false;
        this.speedChange = false;
        this.currentItemChange = false;
        this.scoreChange = false;
        this.teamChange = false;
        this.itemsChange = false;
    }

    public PlayerChange(ChangeType type) {
        this.type = type;
        switch (type) {
        case FULL_CHANGE:
            this.maxHealthChange = true;
            this.speedChange = true;
            this.itemsChange = true;
        case NEW:
            this.teamChange = true;
        case BASIC_CHANGE:
            this.poseChange = true;
            this.healthChange = true;
            this.scoreChange = true;
            this.currentItemChange = true;
            break;
        case NONE:
            break;
        case REMOVED:
            break;
        }
    }

    public boolean isHealthChange() {
        return healthChange;
    }

    public void healthChanged() {
        if (type == ChangeType.NONE)
            type = ChangeType.BASIC_CHANGE;
        this.healthChange = true;
    }

    public boolean isPoseChange() {
        return poseChange;
    }

    public void poseChanged() {
        if (type == ChangeType.NONE)
            type = ChangeType.BASIC_CHANGE;
        this.poseChange = true;
    }

    public boolean isMaxHealthChange() {
        return maxHealthChange;
    }

    public void maxHealthChanged() {
        if (type != ChangeType.FULL_CHANGE)
            type = ChangeType.FULL_CHANGE;
        this.maxHealthChange = true;
    }

    public boolean isSpeedChange() {
        return speedChange;
    }

    public void speedChanged() {
        if (type != ChangeType.FULL_CHANGE)
            type = ChangeType.FULL_CHANGE;
        this.speedChange = true;
    }

    public boolean isCurrentItemChange() {
        return currentItemChange;
    }

    public void currentItemChanged() {
        if (type == ChangeType.NONE)
            type = ChangeType.BASIC_CHANGE;
        this.currentItemChange = true;
    }

    public boolean isScoreChange() {
        return scoreChange;
    }

    public void scoreChanged() {
        if (type == ChangeType.NONE)
            type = ChangeType.BASIC_CHANGE;
        this.scoreChange = true;
    }

    public boolean isTeamChange() {
        return teamChange;
    }

    public void teamChanged() {
        if (type == ChangeType.NONE || type == ChangeType.BASIC_CHANGE)
            type = ChangeType.NEW;
        this.teamChange = true;
    }

    public boolean isItemsChange() {
        return itemsChange;
    }

    public void itemsChanged() {
        if (type != ChangeType.FULL_CHANGE)
            type = ChangeType.FULL_CHANGE;
        this.itemsChange = true;
    }

}
