package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.AttackType;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.map.Meadow;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.ActionList;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class SoldierZombieAI extends EnemyAI{

    private final int RANGE_TO_SHOOT; //In Location metric
    private final int RATE_OF_FIRE;
    private long attackDelay = 500; //Might need to move this to enemyAI
    private Random rand = new Random();
    private boolean attacking;
    private long beginAttackTime;
    private Pose poseToGo;

    public SoldierZombieAI(int rangeToShoot, int rateOfFire){
        this.RANGE_TO_SHOOT = rangeToShoot;
        this.RATE_OF_FIRE = rateOfFire;
    }

    @Override
    public AIAction getAction() {
        if(attacking){
            return AIAction.ATTACK;
        } else if(getDistToPlayer(getClosestPlayer()) >= RANGE_TO_SHOOT){
            //1 in 3 change it will decide to move
            if(rand.nextInt(3) == 0){
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        } else if (getDistToPlayer(getClosestPlayer()) < RANGE_TO_SHOOT){
            int decision = rand.nextInt(9);
            // 1/2 it will attack, 1/4 it with move or wait
            if(decision >= 4){
                attacking = true;
                beginAttackTime = System.currentTimeMillis();
                return AIAction.ATTACK;
            } else if (decision >= 2){
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        }
        return AIAction.WAIT;
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
        Pose nextPose = checkIfInSpawn(pose);

        if(pose == nextPose) {                  //if out of spawn
            if (poseToGo == pose || poseToGo == null) {             //if does not a pose to go
                if(!isProcessing()) {           //if not already generating a new pose to go
                    setProcessing(true);
                    new GenerateSoldierPath(this, pose).start();

                }else{                          //if has a pose generated
                    double angle = getAngle(pose, poseToGo);
                    return poseByAngle(angle, pose, angle, tileMap);
                }
            } else {                            //if has a pose to go
                double angle = getAngle(pose, poseToGo);
                return poseByAngle(angle, pose, angle, tileMap);
            }
        }

        return nextPose;
    }


    synchronized void setPoseToGo(Pose pose) {
        poseToGo = pose;
        setProcessing(false);
    }
}
