package server.engine.ai;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.map.Meadow;
import server.engine.state.map.tile.Tile;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.TileState;

import java.util.LinkedList;
import java.util.Random;

public class ZombieAI extends EnemyAI {

    long attackDelay;
    long beginAttackTime;
    boolean attacking;
    private boolean turnLeft;
    private int stepsUntilNormPath = 0;

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
            return AIAction.ATTACK;
        }
        return AIAction.WAIT;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(getClosestPlayer(), 24, 1));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    @Override
    protected Pose generateNextPose(double maxDistanceToMove, Pose closestPlayer) {
        int[] tile = Tile.locationToTile(pose);

        if (tile[0] == 0 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2) {
            return new Pose(pose.getX() + 0.1, pose.getY(), 90);
        }

        if (tile[0] == Meadow.DEFAULT_X_DIM - 1 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2) {
            return new Pose(pose.getX() - 0.1, pose.getY(), 270);
        }

        for (double i = 0.1; i < maxDistanceToMove; i += 0.1) {
            pose = poseByAngle(getAngle(pose, closestPlayer), pose);
        }

        return pose;
    }

    private Pose poseByAngle(double angle, Pose enemy) {
        Pose newPose = null;
        double realAngle = angle;
        angle = randomizePath(angle);

            //east
        if (angle > 337.5 || angle <= 22.5) {
            newPose = new Pose(enemy.getX() + 0.1, enemy.getY(), (int) realAngle + 90);

            //north-east
        } else if (angle > 22.5 && angle <= 67.5) {
            newPose = new Pose(enemy.getX() + 0.1, enemy.getY() + 0.1, (int) realAngle + 90);

            //north
        } else if (angle > 67.5 && angle <= 112.5) {
            newPose = new Pose(enemy.getX(), enemy.getY() + 0.1, (int) realAngle + 90);

            //north-west
        } else if (angle > 112.5 && angle <= 157.5) {
            newPose = new Pose(enemy.getX() - 0.1, enemy.getY() + 0.1, (int) realAngle + 90);

            //west
        } else if (angle > 157.5 && angle <= 202.5) {
            newPose = new Pose(enemy.getX() - 0.1, enemy.getY(), (int) realAngle + 90);

            //south-west
        } else if (angle > 202.5 && angle <= 247.5) {
            newPose = new Pose(enemy.getX() - 0.1, enemy.getY() - 0.1, (int) realAngle + 90);

            //south
        } else if (angle > 247.5 && angle <= 292.5) {
            newPose = new Pose(enemy.getX(), enemy.getY() - 0.1, (int) realAngle + 90);

            //south-east
        } else if (angle > 292.5 && angle <= 337.5) {
            newPose = new Pose(enemy.getX() + 0.1, enemy.getY() - 0.1, (int) realAngle + 90);
        }

        if (newPose != null) {
            if (tileNotSolid(Tile.locationToTile(newPose)))
                return newPose;
        }

        return enemy;
    }

    //Maybe needs some more balancing
    private double randomizePath(double angle) {
        Random rand = new Random();
        //change of moving from direct path
        int r = rand.nextInt(1000);

        if(stepsUntilNormPath == 0) {
            if (r == 1) {
                turnLeft = true;
                //How much to move to a side
                stepsUntilNormPath = rand.nextInt(200) + 50;
            } else if (r == 0) {
                turnLeft = false;
                stepsUntilNormPath = rand.nextInt(200) + 50;
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

    private boolean tileNotSolid(int[] tile) {
        return tileMap[tile[0]][tile[1]].getState() != TileState.SOLID;
    }

    private static double getAngle(Pose enemy, Pose player) {
        double angle = Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }


}
