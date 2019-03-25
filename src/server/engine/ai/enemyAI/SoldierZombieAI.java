package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.ai.newPoseGenerators.RandomPoseGen;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Team;

import java.util.LinkedList;
import java.util.Random;

public class SoldierZombieAI extends EnemyAI {

    private final int RANGE_TO_SHOOT; //In Location metric
    private final int RATE_OF_FIRE;
    private long attackDelay = 600; // Might need to move this to enemyAI
    private boolean attacking = false;
    private boolean moving = false;
    private long beginAttackTime;
    private Random rand = new Random();
    private Pose poseToGo;
    private Pistol pistol = new Pistol();

    public SoldierZombieAI(int rangeToShoot, int rateOfFire) {
        super(SHORT_DELAY);
        this.RANGE_TO_SHOOT = rangeToShoot;
        this.RATE_OF_FIRE = rateOfFire;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {                                //If attacking, continue to attack
            return AIAction.ATTACK;
        } else if (moving) {                            //If moving, continue to move
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) >= RANGE_TO_SHOOT) {
            //1 in 80 change it will decide to move
            if (rand.nextInt(80) == 0) {
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        } else if (getDistToPlayer(closestPlayer) < RANGE_TO_SHOOT) {
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
    }


    //TODO this method could probably live in EnemyAI
    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    public synchronized void setPoseToGo(Pose pose) {
        poseToGo = pose;
        setProcessing(false);
    }

    protected Force generateMovementForce() {
        //if does not have pose to go
        if (poseToGo == null || Pose.compareLocation(pose, poseToGo, 20)) {
            moving = false;
            //if not already generating a new pose to go
            if (!isProcessing()) {
                setProcessing(true);
                new RandomPoseGen(this, pose).start();
            } else {
                //if has a pose generated
                moving = true;
                int angleToMove = getAngle(pose, poseToGo);
//                pose = poseFromAngle(angle, angle, maxDistanceToMove);
                return new Force(angleToMove, maxMovementForce);
            }
        } else {
            //if has a pose to go
            moving = true;
            int angleToMove = getAngle(pose, poseToGo);
//            pose = poseFromAngle(angle, angle, maxDistanceToMove);
            return new Force(angleToMove, maxMovementForce);
        }

        return new Force();
    }

    @Override
    protected Attack getAttackObj() {
        int angle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(pistol.getProjectiles(new Pose(pose, angle), Team.ENEMY));
    }


    //    protected synchronized Pose generateNextPose() {
//        pose = checkIfInSpawn();
//        //if out of spawn
//        if(outOfSpawn) {
//            //if does not have pose to go
//            if (poseToGo == null || poseToGo.compareLocation(pose, 1)) {
//                moving = false;
//                //if not already generating a new pose to go
//                if(!isProcessing()) {
//                    setProcessing(true);
//                    new RandomPoseGen(this, pose).start();
//                }else{
//                    //if has a pose generated
//                    moving = true;
//                    double angle = getAngle(pose, poseToGo);
//                    pose = poseFromAngle(angle, angle, maxDistanceToMove);
//                }
//            } else {
//                //if has a pose to go
//                moving = true;
//                double angle = getAngle(pose, poseToGo);
//                pose = poseFromAngle(angle, angle, maxDistanceToMove);
//            }
//        }
//
//        return pose;
//    }

}
