package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.ai.aStar.AStar;
import server.engine.ai.newPoseGenerators.PoseAroundPlayerGen;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.SniperRifle;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.Team;

import java.util.LinkedList;

public class SniperAI extends AStarUsingEnemy {

    private final int RANGE_TO_RUN_AWAY;
    Gun gun = new SniperRifle();

    public SniperAI(int rangeToRunAway) {
        super(LONG_DELAY * 2);
        this.RANGE_TO_RUN_AWAY = rangeToRunAway;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (stillInPositionToAttack() && movementFinished) {
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return AIAction.ATTACK;
        } else {
            return AIAction.MOVE;
        }
    }

    private boolean stillInPositionToAttack() {
        double distanceBetween = getDistToPlayer(closestPlayer);
        boolean stillInPosition = distanceBetween > RANGE_TO_RUN_AWAY - RANGE_TO_RUN_AWAY * 0.05
                && distanceBetween <= RANGE_TO_RUN_AWAY + RANGE_TO_RUN_AWAY * 0.2;

        if(stillInPosition){
            return true;
        }else{
            movementFinished = false;
            return false;
        }
    }

    @Override
    protected Attack getAttackObj() {
        int angle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getProjectiles(new Pose(pose, angle), Team.ENEMY));
    }

    @Override
    protected Force generateMovementForce() {
        return generateMovementForce(new PoseAroundPlayerGen(this, RANGE_TO_RUN_AWAY, true,closestPlayer, pose));
    }


}
