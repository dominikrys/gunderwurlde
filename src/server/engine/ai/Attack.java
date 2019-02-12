package server.engine.ai;

public class Attack {
    protected int attackDirection;
    protected int attackSize;
    protected AttackType attackType;
    protected long timeToCarryOut;
    protected long startTime;

    public Attack(int attackDirection, int attackSize, AttackType attackType){
        this.attackDirection = attackDirection;
        this.attackSize = attackSize;
        this.attackType = attackType;
    }

    public int getAttackDirection() {
        return attackDirection;
    }

    public int getAttackSize() {
        return attackSize;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTimeToCarryOut() {
        return timeToCarryOut;
    }

}
