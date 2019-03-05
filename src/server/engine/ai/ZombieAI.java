package server.engine.ai;

import java.util.LinkedList;
import java.util.Random;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Location;
import shared.Pose;
import shared.lists.ActionList;

public class ZombieAI extends EnemyAI {

    long attackDelay;
    long beginAttackTime;
    boolean attacking;
    private boolean turnLeft;
    private int stepsUntilNormPath = 0;
    private Location attackLocation;

    public ZombieAI() {
        super();
        this.beginAttackTime = System.currentTimeMillis();
        this.attackDelay = DEFAULT_DELAY;
        this.attacking = false;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (getDistToPlayer(closestPlayer) >= Constants.TILE_SIZE) {
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) < Constants.TILE_SIZE) {
            this.actionState = ActionList.ATTACKING;
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            attackLocation = closestPlayer;
            return AIAction.ATTACK;
        }
        return AIAction.WAIT;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(attackLocation, 24, 1));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    @Override
    protected Pose generateNextPose() {
        pose = checkIfInSpawn();

        if (outOfSpawn) {
            double angle = getAngle(pose, closestPlayer);
            pose = poseFromAngle(randomizePath(angle), angle, maxDistanceToMove);
        }

        return pose;
    }

    protected Force generateMovementForce(){
        int[] tile = Tile.locationToTile(pose);

        if ((tile[0] == 0 && tile[1] == (mapYDim - 2) / 2)
                || (tile[0] == 1 && tile[1] == (mapYDim - 2) / 2)) {
            return new Force(90, maxDistanceToMove*4);
        }

        if ((tile[0] == mapXDim - 1 && tile[1] == (mapYDim - 2) / 2)
                || (tile[0] == mapXDim - 2 && tile[1] == (mapYDim - 2) / 2)) {
            return new Force(270, maxDistanceToMove*4);

        }

        return new Force(90, 10);
    }

    //Maybe needs some more balancing
    private double randomizePath(double angle) {
        Random rand = new Random();
        //change of moving from direct path
        int r = rand.nextInt(500);

        if (stepsUntilNormPath == 0) {
            if (r == 1) {
                turnLeft = true;
                //How much to move to a side
                stepsUntilNormPath = rand.nextInt(100) + 20;
            } else if (r == 0) {
                turnLeft = false;
                stepsUntilNormPath = rand.nextInt(100) + 20;
            } else {
                return angle;
            }
        } else {
            if (turnLeft) {
                angle -= 50;
            } else {
                angle += 50;
            }
            stepsUntilNormPath--;
        }

        return angle;
    }
}
