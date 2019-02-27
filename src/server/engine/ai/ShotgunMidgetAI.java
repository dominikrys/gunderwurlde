package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
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
            LinkedList<Projectile> projectiles = new LinkedList<>();
            SmallBullet bulletUsed = new SmallBullet();
            bulletUsed.setSpeed(SmallBullet.DEFAULT_SPEED / 3);
            projectiles.add(bulletUsed.createFor(new Pose(pose, (int) getAngle(pose, closestPlayer)), Teams.ENEMY));
            projectiles.add(bulletUsed.createFor(new Pose(pose, (int) getAngle(pose, closestPlayer) + 15), Teams.ENEMY));
            projectiles.add(bulletUsed.createFor(new Pose(pose, (int) getAngle(pose, closestPlayer) - 15), Teams.ENEMY));
            attacks.add(new ProjectileAttack(projectiles));
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
                    System.out.println("Knocking back");
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
