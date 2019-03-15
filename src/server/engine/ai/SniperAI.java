package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.SniperRifle;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

import java.util.LinkedList;

public class SniperAI extends EnemyAI {

    private final int RANGE_TO_RUN_AWAY;
    Gun gun = new SniperRifle();
    private Pose poseToGo;
    private boolean runningAway;
    private boolean inPositionToAttack;
    private boolean attacking;
    private boolean inRangeToRun;
    private boolean playerAiming = false;
    private long beginAttackTime;

    public SniperAI(int rangeToRunAway){
        super();
        this.RANGE_TO_RUN_AWAY = rangeToRunAway;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        }else if(inPositionToAttack){
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return AIAction.ATTACK;
        }else if(runningAway){
            return AIAction.MOVE;
        }else{
            return AIAction.MOVE;
        }
    }

    public void attackController() {
        //Check if still in range or if player is not aiming at us
        if(!(inRangeToRun && playerAiming)){

            //if aiming, run away
        }else if(playerAiming){
            //Run away

            //if in range for a few secs, move away to safer location
        }else{


        }
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        
    }


    @Override
    protected Attack getAttackObj() {
        int angle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getShotProjectiles(new Pose(pose, angle), Teams.ENEMY));
    }

    @Override
    protected Force generateMovementForce() {
        if(runningAway){
            if(poseToGo != null){
                //get there using AStar
            }else{
                //fire up the running away generator
                //cloak, and move somewhere maybe?
            }
        }else{
            if(poseToGo != null){
                // get to the attacking position using AStart
            }else{
                //fire up the attacking position generator
                // cloak? stay still and wait
            }
        }
    }

    synchronized void setPoseToGo(Pose pose) {
        poseToGo = pose;
        setProcessing(false);
    }

}
