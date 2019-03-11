package server.engine.ai;

import java.util.LinkedList;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

public class ShotgunMidgetAI extends ZombieAI{

    private final int KNOCKBACK_AMOUT;
//    private int knockback;
    private int knockbackAngle;
    private boolean knockbackState = false;
    private Shotgun shotgun = new Shotgun(3, 10, 1000);

    public ShotgunMidgetAI(int knockbackAmount){
        super();
        this.KNOCKBACK_AMOUT = knockbackAmount;
        this.timeBetweenAttacks = 1500;
    }

    @Override
    public AIAction getAction() {
        long now = System.currentTimeMillis();
        if (attacking) {
            if ((now - beginAttackTime) >= attackDelay) {
                return AIAction.ATTACK;  
            }
        } else if (getDistToPlayer(closestPlayer) >= Constants.TILE_SIZE * 3 || knockbackState) {
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) < Constants.TILE_SIZE * 3 && (now - beginAttackTime) >= (timeBetweenAttacks + attackDelay)) {
            this.actionState = ActionList.ATTACKING;
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return AIAction.WAIT;
        }
        return AIAction.WAIT;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        knockbackAngle = (int) getAngle(pose, closestPlayer);
        attacks.add(new ProjectileAttack(shotgun.getShotProjectiles(new Pose(pose, knockbackAngle), Teams.ENEMY)));
        attacking = false;
        this.actionState = ActionList.NONE;
        return attacks;
    }

    @Override
    public Force getForceFromAttack(double maxMovementForce) {
        knockbackAngle = (int) getAngle(pose, closestPlayer);
        return new Force(Pose.normaliseDirection(knockbackAngle + 180), maxMovementForce + KNOCKBACK_AMOUT);
    }

    @Override
    protected Force generateMovementForce(){
        int angleToMove = (int) getAngle(pose, closestPlayer);
        return new Force(angleToMove, maxMovementForce);
    }
}
