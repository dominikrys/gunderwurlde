package server.game_engine.data;

public abstract class EnemyChange extends Change {
    protected boolean healthChange;
    protected boolean poseChange;
    protected boolean maxHealthChange;
    protected boolean speedChange;
    protected boolean typeChange;

    public EnemyChange() {
        this.healthChange = false;
        this.poseChange = false;
        this.maxHealthChange = false;
        this.speedChange = false;
        this.typeChange = false;
    }

    public EnemyChange(ChangeType type) {
        this.type = type;
        switch (type) {
        case FULL_CHANGE:
            this.maxHealthChange = true;
            this.speedChange = true;
        case NEW:
            this.typeChange = true;
        case BASIC_CHANGE:
            this.poseChange = true;
            this.healthChange = true;
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

    public boolean isTypeChange() {
        return typeChange;
    }

    public void typeChanged() {
        if (type == ChangeType.NONE || type == ChangeType.BASIC_CHANGE)
            type = ChangeType.NEW;
        this.typeChange = true;
    }

}
