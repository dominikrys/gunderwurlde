package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Team;

public class ShotgunMidgetAI extends ZombieAI{

    private final int KNOCKBACK_AMOUNT;
    private Shotgun gun = new Shotgun(3, 10, 1000);
    private long lastAttack = 0;
    private long now = 0;

    public ShotgunMidgetAI(int knockbackAmount, int distanceToPlayerForAttack){
        super(Constants.TILE_SIZE * distanceToPlayerForAttack, MEDIUM_DELAY);
        randomizePath = false;
        this.KNOCKBACK_AMOUNT = knockbackAmount;
    }

    @Override
    public AIAction getAction() {
        if(closestPlayer != null) {
            now = System.currentTimeMillis();

            if (attacking) {
                return AIAction.ATTACK;
            } else if (getDistToPlayer(closestPlayer) >= DISTANCE_TO_PLAYER_FOR_ATTACK) {
                return AIAction.MOVE;
            } else if (getDistToPlayer(closestPlayer) < DISTANCE_TO_PLAYER_FOR_ATTACK
                    && (now - lastAttack) > MEDIUM_DELAY
                    && pathUnobstructed(pose, closestPlayer, tileMap)) {
                this.actionState = ActionList.ATTACKING;
                attacking = true;
                beginAttackTime = System.currentTimeMillis();
                attackLocation = closestPlayer;
                return AIAction.ATTACK;
            }
            return AIAction.WAIT;
        }else{
            return AIAction.UPDATE;
        }
    }

    @Override
    public Force getForceFromAttack(double maxMovementForce) {
        if (!attacking) {
            lastAttack = System.currentTimeMillis();
            int knockbackAngle = Pose.normaliseDirection(getAngle(pose, closestPlayer) + 180);
            return new Force(knockbackAngle, maxMovementForce + KNOCKBACK_AMOUNT);
        }else{
            return new Force(pose.getDirection(), 0);
        }
    }

    @Override
    protected Attack getAttackObj() {
        int attackAngle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getProjectiles(new Pose(pose, attackAngle), Team.ENEMY, 0));
    }
}
