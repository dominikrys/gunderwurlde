package server.engine.ai;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Location;
import shared.lists.ActionList;

import java.util.LinkedList;
import java.util.Random;

public class ZombieAI extends EnemyAI {

    long beginAttackTime;
    boolean attacking;
    private boolean turnLeft;
    private int stepsUntilNormPath = 0;
    boolean randomizePath = true;
    int distanceToPlayerForAttack = Constants.TILE_SIZE;

    public ZombieAI() {
        super();
        this.beginAttackTime = System.currentTimeMillis();
        this.attacking = false;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (getDistToPlayer(closestPlayer) >= distanceToPlayerForAttack) {
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) < distanceToPlayerForAttack) {
            this.actionState = ActionList.ATTACKING;
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return AIAction.ATTACK;
        }
        return AIAction.WAIT;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(closestPlayer, 24, 1));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }


    protected Force generateMovementForce(){
        int angleToMove = (int) getAngle(pose, closestPlayer);
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
