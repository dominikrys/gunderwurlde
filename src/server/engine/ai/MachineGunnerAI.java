package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

import java.util.LinkedList;

public class MachineGunnerAI extends ZombieAI {

    //Probably needs a better name
    //The +- of degrees the enemy shoots
    private final int ATTACK_WIDTH = 120;
    private int attackAngleOffset = 0;
    private int startOfAttackAngle;
    private boolean delayPast;
    private Pistol pistol = new Pistol();

    public MachineGunnerAI(){
        super();
        distanceToPlayerForAttack = Constants.TILE_SIZE * 10;
        attackDelay = LONG_DELAY;
    }

//    @Override
//    public AIAction getAction() {
//        if (attacking) {
//            return AIAction.ATTACK;
//        } else if (getDistToPlayer(closestPlayer) >= distanceToPlayerForAttack) {
//            return AIAction.MOVE;
//        } else if (getDistToPlayer(closestPlayer) < distanceToPlayerForAttack) {
//            this.actionState = ActionList.ATTACKING;
//            attacking = true;
//            beginAttackTime = System.currentTimeMillis();
//            return AIAction.ATTACK;
//        }
//        return AIAction.WAIT;
//    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();
        delayPast = (now - beginAttackTime) >= attackDelay;

        if (delayPast) {
            if(attackAngleOffset != ATTACK_WIDTH) {
                attacks.add(new ProjectileAttack(pistol.getShotProjectiles(
                        new Pose(pose, startOfAttackAngle + attackAngleOffset), Teams.ENEMY)));
                attackAngleOffset++;
            }else{
                this.actionState = ActionList.NONE;
                attacking = false;
                attackAngleOffset = 0;
            }
        }else{
            startOfAttackAngle = Pose.normaliseDirection(getAngle(pose, closestPlayer) - ATTACK_WIDTH / 2);
        }

        return attacks;
    }

    @Override
    public Force getForceFromAttack(double maxMovementForce) {
        if(delayPast) {
            return new Force(attackAngleOffset, 0);
        }else{
            return new Force(0, 0);
        }
    }
}
