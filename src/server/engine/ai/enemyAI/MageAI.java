package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.ai.newPoseGenerators.PoseAroundPlayerGen;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.item.weapon.gun.FireGun;
import server.engine.state.item.weapon.gun.ProjectileGun;
import server.engine.state.item.weapon.gun.IceGun;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Team;

import java.util.Random;

public class MageAI extends PoseGeneratorUsingEnemy {

    private final long TIME_BETWEEN_TELEPORTS;
    private final int DISTANCE_TO_PLAYER;
    private long lastTeleport = 0;
    private ProjectileGun gun;
    private boolean teleportAway = false;
    long now;

    public MageAI(long timeBetweenTeleports, int distanceToPlayer) {
        super(MEDIUM_DELAY);
        this.DISTANCE_TO_PLAYER = distanceToPlayer;
        this.TIME_BETWEEN_TELEPORTS = timeBetweenTeleports;
        Random rand = new Random();

        if (rand.nextInt(2) == 1) {
            gun = new FireGun();
        } else {
            gun = new IceGun();
        }
    }

    @Override
    public AIAction getAction() {
        if(closestPlayer != null) {
            now = System.currentTimeMillis();
            if (attacking) {
                return AIAction.ATTACK;
            } else if (teleportAway && now - lastTeleport >= TIME_BETWEEN_TELEPORTS) {
                if (poseToGo != null) {
                    teleportAway = false;
                    return AIAction.UPDATE;

                } else if (!isProcessing()) {
                    (new PoseAroundPlayerGen(
                            this, DISTANCE_TO_PLAYER, false, closestPlayer, pose)).start();
                }

            } else if (now - lastTeleport >= TIME_BETWEEN_TELEPORTS) {
                if (poseToGo != null) {
                    this.actionState = ActionList.ATTACKING;
                    attacking = true;
                    beginAttackTime = System.currentTimeMillis();
                    return AIAction.UPDATE;

                } else if (!isProcessing()) {
                    (new PoseAroundPlayerGen(
                            this, DISTANCE_TO_PLAYER, true, closestPlayer, pose)).start();
                }
            }
            return AIAction.MOVE;
        }else {
            return AIAction.WAIT;
        }
    }

    @Override
    public Enemy getUpdatedEnemy() {
        lastTeleport = System.currentTimeMillis();
        int attackAngle = getAngle(pose, closestPlayer);
        enemy.setPose(new Pose(poseToGo, attackAngle));
        poseToGo = null;
        return enemy;
    }

    @Override
    protected Attack getAttackObj() {
        teleportAway = true;
        int attackAngle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getProjectiles(new Pose(pose, attackAngle), Team.ENEMY , 0));
    }

    @Override
    protected Force generateMovementForce() {
        int attackAngle = getAngle(pose, closestPlayer);
        return new Force(attackAngle, 0);
    }
}
