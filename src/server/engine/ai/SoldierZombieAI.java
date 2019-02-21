package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.AttackType;
import server.engine.state.entity.attack.ProjectileAttack;
import shared.Pose;
import shared.lists.ActionList;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SoldierZombieAI extends EnemyAI{

    private final int RANGE_TO_SHOOT; //In Location metric
    private final int RATE_OF_FIRE;
    private long attackDelay = 500; //Might need to move this to enemyAI
    private boolean attacking = false;
    private boolean moving = false;
    private long beginAttackTime;
    private Random rand = new Random();
    private Pose poseToGo;

    public SoldierZombieAI(int rangeToShoot, int rateOfFire){
        this.RANGE_TO_SHOOT = rangeToShoot;
        this.RATE_OF_FIRE = rateOfFire;
    }

    @Override
    public AIAction getAction() {
        if(outOfSpawn) {                                    //Check if in spawn and if so, move out

            if (attacking) {                                //If attacking, continue to attack
                return AIAction.ATTACK;
            } else if (moving) {                            //If moving, continue to move
                return AIAction.MOVE;
            } else if (getDistToPlayer(getClosestPlayer()) >= RANGE_TO_SHOOT) {
                //1 in 50 change it will decide to move
                if (rand.nextInt(50) == 0) {
                    return AIAction.MOVE;
                } else {
                    return AIAction.WAIT;
                }
            } else if (getDistToPlayer(getClosestPlayer()) < RANGE_TO_SHOOT) {
                int decision = rand.nextInt(100);
                //Will decide whether to attack based on the RATE_OF_FIRE
                if (decision <= RATE_OF_FIRE && decision >= 2) {
                    attacking = true;
                    beginAttackTime = System.currentTimeMillis();
                    return AIAction.ATTACK;
                } else if (decision < 2 && decision > 0) {
                    return AIAction.MOVE;
                } else {
                    return AIAction.WAIT;
                }
            }
            return AIAction.WAIT;
        }else{
            return AIAction.MOVE;
        }
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            //TODO change this projAttack object
            attacks.add(new ProjectileAttack(closestPlayer,4, AttackType.PROJECTILE));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    @Override
    protected synchronized Pose generateNextPose(double maxDistanceToMove, Pose closestPlayer) {
        pose = checkIfInSpawn();
        //if out of spawn
        if(outOfSpawn) {
            //if does not have pose to go
            if (poseToGo == null || poseToGo.compareLocation(pose, 1)) {
                moving = false;
                //if not already generating a new pose to go
                if(!isProcessing()) {
                    setProcessing(true);
                    new RandomPoseGen(this, pose).start();
                }else{
                    //if has a pose generated
                    moving = true;
                    double angle = getAngle(pose, poseToGo);
                    pose = poseFromAngle(angle, angle, maxDistanceToMove);
                }
            } else {
                //if has a pose to go
                moving = true;
                double angle = getAngle(pose, poseToGo);
                pose = poseFromAngle(angle, angle, maxDistanceToMove);
            }
        }

        return pose;
    }


    synchronized void setPoseToGo(Pose pose) {
        poseToGo = pose;
        setProcessing(false);
    }
}
