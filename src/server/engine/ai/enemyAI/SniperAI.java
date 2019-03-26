package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.ai.aStar.AStar;
import server.engine.ai.newPoseGenerators.PoseAroundPlayerGen;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.ProjectileGun;
import server.engine.state.item.weapon.gun.SniperRifle;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.Team;

import java.util.LinkedList;

public class SniperAI extends AStarUsingEnemy {

    private final int RANGE_TO_RUN_AWAY;
    ProjectileGun gun = new SniperRifle();
    private boolean inPositionToAttack = false;

    public SniperAI(int rangeToRunAway) {
        super(LONG_DELAY * 2);
        this.RANGE_TO_RUN_AWAY = rangeToRunAway;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (stillInPositionToAttack() && inPositionToAttack) {
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
            inPositionToAttack = false;
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
        if (posePath == null) {
            if (!getAStarProcessing()) {
                if(poseToGo != null) {
//                    Pose posetogoo = new Pose(Tile.tileToLocation(30, 2));
                    new AStar(this, 1, tileMap, pose, poseToGo).start();
                    poseToGo = null;
                }else if (!isProcessing()){
                    (new PoseAroundPlayerGen(this, RANGE_TO_RUN_AWAY, true,closestPlayer, pose)).start();
                }
            }
        } else {
            Force angle = getForceFromPath();
            if (angle != null) return angle;
        }
        return new Force(pose.getDirection(), 0);
    }

    private Force getForceFromPath() {
        if (!Pose.compareLocation(pose, posePath.peekLast(), 5)) {
            int angle = getAngle(pose, posePath.peekLast());
            return new Force(angle, maxMovementForce);
        } else {
            posePath.pollLast();
            if (posePath.size() == 0) {
                posePath = null;
                inPositionToAttack = true;
            }
        }
        return null;
    }

}
