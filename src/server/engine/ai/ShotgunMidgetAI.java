package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
import server.engine.state.item.weapon.gun.Shotgun;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

import java.util.LinkedList;

public class ShotgunMidgetAI extends ZombieAI{

    private final int KNOCKBACK_AMOUT;
    private int knockback;
    private double knockbackAgnle;
    private boolean knockbackState = false;
    private Shotgun shotgun = new Shotgun();

    public ShotgunMidgetAI(int knockbackAmount){
        super();
        this.KNOCKBACK_AMOUT = knockbackAmount;
        this.knockback = KNOCKBACK_AMOUT;
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
            attacks.add(new ProjectileAttack(shotgun.getShotProjectiles(
                    new Pose(pose, (int) getAngle(pose, closestPlayer)), Teams.ENEMY)));
            attacking = false;
            knockbackState = true;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    @Override
    protected Pose generateNextPose() {
        pose = checkIfInSpawn();

        if (outOfSpawn) {
            double angle = getAngle(pose, closestPlayer);
            if(!knockbackState) {
                pose = poseFromAngle(angle, angle, maxDistanceToMove);
            }else{
                knockbackAgnle = angle;
                if(knockback > 0) {
                    pose = poseFromAngle(Pose.normaliseDirection((int) knockbackAgnle + 180), angle, maxDistanceToMove + 1.5);
                    knockback--;
                }else{
                knockback = KNOCKBACK_AMOUT;
                knockbackState = false;
                }
            }
        }

        return pose;
    }

}
