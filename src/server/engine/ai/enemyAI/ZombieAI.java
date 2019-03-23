package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.lists.ActionList;

import java.util.Random;

public class ZombieAI extends EnemyAI {

    private boolean turnLeft;
    private int stepsUntilNormPath = 0;
    boolean randomizePath = true;
    private final int DISTANCE_TO_PLAYER_FOR_ATTACK;

    public ZombieAI() {
        super(SHORT_DELAY);
        this.DISTANCE_TO_PLAYER_FOR_ATTACK = Constants.TILE_SIZE;
        this.beginAttackTime = System.currentTimeMillis();
        this.attacking = false;
    }

    public ZombieAI(int distanceToPlayerForAttack) {
        super(SHORT_DELAY);
        this.DISTANCE_TO_PLAYER_FOR_ATTACK = distanceToPlayerForAttack;
        this.beginAttackTime = System.currentTimeMillis();
        this.attacking = false;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (getDistToPlayer(closestPlayer) >= DISTANCE_TO_PLAYER_FOR_ATTACK) {
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) < DISTANCE_TO_PLAYER_FOR_ATTACK) {
            this.actionState = ActionList.ATTACKING;
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return AIAction.ATTACK;
        }
        return AIAction.WAIT;
    }


    protected Force generateMovementForce(){
        int angleToMove = getAngle(pose, closestPlayer);
        if(randomizePath) {
            return new Force((int) randomizePath(angleToMove), maxMovementForce);
        }else{
            return new Force(angleToMove, maxMovementForce);
        }
    }

    //TODO Maybe needs some more balancing

    private double randomizePath(double angle) {
        Random rand = new Random();
        //change of moving from direct path
        int r = rand.nextInt(500);

        if (stepsUntilNormPath == 0) {
            if (r == 1) {
                turnLeft = true;
                //How much to move to a side
                stepsUntilNormPath = rand.nextInt(100) + 20;
            } else if (r == 0) {
                turnLeft = false;
                stepsUntilNormPath = rand.nextInt(100) + 20;
            } else {
                return angle;
            }
        } else {
            if (turnLeft) {
                angle -= 50;
            } else {
                angle += 50;
            }
            stepsUntilNormPath--;
        }

        return angle;
    }

    @Override
    protected Attack getAttackObj() {
        return new AoeAttack(closestPlayer, 24, 1);
    }
    //    @Override
//    protected Pose generateNextPose() {
//        pose = checkIfInSpawn();
//
//        if (outOfSpawn) {
//            double angle = getAngle(pose, closestPlayer);
//            pose = poseFromAngle(randomizePath(angle), angle, maxDistanceToMove);
//        }
//
//        return pose;
//    }
}
