package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import shared.Pose;

import java.util.LinkedList;

public class SoldierZombieAI extends EnemyAI{

    private final int RANGE_TO_SHOOT;
    private final int RATE_OF_FIRE;

    public SoldierZombieAI(int rangeToShoot, int rateOfFire){
        this.RANGE_TO_SHOOT = rangeToShoot;
        this.RATE_OF_FIRE = rateOfFire;
    }

    @Override
    public AIAction getAction() {
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        return null;
    }

    @Override
    protected Pose generateNextPose(double maxDistanceMoved, Pose closestPlayer) {
        return null;
    }
}
