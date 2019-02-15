package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import shared.Pose;

import java.util.LinkedList;
import java.util.Random;

public class SoldierZombieAI extends EnemyAI{

    private final int RANGE_TO_SHOOT;
    private final int RATE_OF_FIRE;
    private Random rand = new Random();

    public SoldierZombieAI(int rangeToShoot, int rateOfFire){
        this.RANGE_TO_SHOOT = rangeToShoot;
        this.RATE_OF_FIRE = rateOfFire;
    }

    @Override
    public AIAction getAction() {
        if(getDistToPlayer(getClosestPlayer()) > RANGE_TO_SHOOT){
            //1 in 3 change it will decide to move
            if(rand.nextInt(3) == 0){
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        } else {
            int decision = rand.nextInt(9);
            // 1/2 it will attack, 1/4 it with move or wait
            if(decision >= 4){
                return AIAction.ATTACK;
            } else if (decision >= 2){
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        }
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
