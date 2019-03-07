package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

import java.util.LinkedList;

public class ShotgunMidgetAI extends ZombieAI{

    private final int KNOCKBACK_AMOUT;
    private int knockback;
    private int knockbackAngle;
    private boolean knockbackState = false;
    private Shotgun shotgun = new Shotgun(3, 10, 1000);

    public ShotgunMidgetAI(int knockbackAmount){
        super();
        this.KNOCKBACK_AMOUT = knockbackAmount;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (getDistToPlayer(closestPlayer) >= Constants.TILE_SIZE * 3 || knockbackState) {
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) < Constants.TILE_SIZE * 3) {
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
            knockbackAngle = (int) getAngle(pose, closestPlayer);
            attacks.add(new ProjectileAttack(shotgun.getShotProjectiles(
                    new Pose(pose, knockbackAngle), Teams.ENEMY)));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    @Override
    public Force getForceFromAttack(double maxMovementForce) {
        return new Force(Pose.normaliseDirection(knockbackAngle + 180), maxMovementForce + KNOCKBACK_AMOUT);
    }

    @Override
    protected Force generateMovementForce(){
        int angleToMove = (int) getAngle(pose, closestPlayer);
        return new Force(angleToMove, maxMovementForce);
    }
}
