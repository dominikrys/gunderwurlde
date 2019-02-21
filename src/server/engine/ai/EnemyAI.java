package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.TileState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public abstract class EnemyAI {

    Enemy enemy;
    static long DEFAULT_DELAY = 380;
    protected Pose pose;
    double maxDistanceToMove;
    private int enemSize;
    private HashSet<Pose> playerPoses;
    Pose closestPlayer;
    protected Tile[][] tileMap;
    protected int mapXDim;
    protected int mapYDim;
    private boolean isProcessing;
    ActionList actionState;
    boolean outOfSpawn = false;

    protected EnemyAI() {
        isProcessing = false;
        actionState = ActionList.NONE;
    }

    public abstract LinkedList<Attack> getAttacks();

    protected abstract Pose generateNextPose();

    public abstract AIAction getAction();

    public ActionList getActionState() {
        return actionState;
    }

    public Pose getNewPose(double maxDistanceToMove) {
        this.maxDistanceToMove = maxDistanceToMove;
        return generateNextPose();
    }

    int getDistToPlayer(Pose player) {
        return (int) sqrt(pow(pose.getY() - player.getY(), 2) + pow(pose.getX() - player.getX(), 2));
    }

    synchronized void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    //Static for RandomPoseGenerator
    static boolean tileNotSolid(int[] tile, Tile[][] tileMap) {
        boolean tileNotSolid;
        int mapXDim = tileMap.length;
        int mapYDim = tileMap[0].length;
        try {
            tileNotSolid = (tileMap[tile[0]][tile[1]].getState() != TileState.SOLID) &&
                    !((tile[0] == 0 && tile[1] == (mapYDim - 2) / 2) ||
                            ((tile[0] == mapXDim - 1 && tile[1] == (mapYDim - 2) / 2)));
        } catch (Exception e) {
            System.out.println("enemy wants to go out of map");
            return false;
        }

        return tileNotSolid;
    }

    public void setInfo(Enemy enemy, HashSet<Pose> playerPoses, Tile[][] tileMap) {
        this.enemy = enemy;
        this.pose = this.enemy.getPose();
        this.enemSize = this.enemy.getSize();
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        this.mapXDim = tileMap.length;
        this.mapYDim = tileMap[0].length;
        this.closestPlayer = findClosestPlayer(playerPoses);
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    private Pose findClosestPlayer(HashSet<Pose> playerPoses) {
        Pose closestPlayer = playerPoses.iterator().next();
        int distToClosest = Integer.MAX_VALUE;

        for (Pose playerPose : playerPoses) {
            int distToPlayer = getDistToPlayer(playerPose);
            if (distToPlayer < distToClosest) {
                distToClosest = distToPlayer;
                closestPlayer = playerPose;
            }
        }

        return closestPlayer;
    }

    Pose poseFromAngle(double angleToMove, double angleToFace, double distToMove) {
        Pose newPose = null;

        //east
        if (angleToMove > 337.5 || angleToMove <= 22.5) {
            newPose = new Pose(pose.getX() + distToMove, pose.getY(), (int) angleToFace + 90);

            //north-east
        } else if (angleToMove > 22.5 && angleToMove <= 67.5) {
            newPose = new Pose(pose.getX() + distToMove, pose.getY() + distToMove, (int) angleToFace + 90);

            //north
        } else if (angleToMove > 67.5 && angleToMove <= 112.5) {
            newPose = new Pose(pose.getX(), pose.getY() + distToMove, (int) angleToFace + 90);

            //north-west
        } else if (angleToMove > 112.5 && angleToMove <= 157.5) {
            newPose = new Pose(pose.getX() - distToMove, pose.getY() + distToMove, (int) angleToFace + 90);

            //west
        } else if (angleToMove > 157.5 && angleToMove <= 202.5) {
            newPose = new Pose(pose.getX() - distToMove, pose.getY(), (int) angleToFace + 90);

            //south-west
        } else if (angleToMove > 202.5 && angleToMove <= 247.5) {
            newPose = new Pose(pose.getX() - distToMove, pose.getY() - distToMove, (int) angleToFace + 90);

            //south
        } else if (angleToMove > 247.5 && angleToMove <= 292.5) {
            newPose = new Pose(pose.getX(), pose.getY() - distToMove, (int) angleToFace + 90);

            //south-east
        } else if (angleToMove > 292.5 && angleToMove <= 337.5) {
            newPose = new Pose(pose.getX() + distToMove, pose.getY() - distToMove, (int) angleToFace + 90);
        }

        if (newPose != null) {
            if (tileNotSolid(Tile.locationToTile(newPose), tileMap)) {
                return newPose;
            }
        }

        return pose;
    }


    double getAngle(Pose enemy, Pose player) {
        double angle = Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    private Pose moveOutOfSpawn(Pose pose) {
        int[] tile = Tile.locationToTile(pose);

        if ((tile[0] == 0 && tile[1] == (mapYDim - 2) / 2)
                || (tile[0] == 1 && tile[1] == (mapYDim - 2) / 2)) {
            //TODO make this use maxDistanceToMove instead of just +1
            return new Pose(pose.getX() + maxDistanceToMove, pose.getY(), 90);
        }

        if ((tile[0] == mapXDim - 1 && tile[1] == (mapYDim - 2) / 2)
                || (tile[0] == mapXDim - 2 && tile[1] == (mapYDim - 2) / 2)) {
            return new Pose(pose.getX() - maxDistanceToMove, pose.getY(), 270);

        }

        return pose;
    }

    Pose checkIfInSpawn() {
        Pose nextPose = pose;
        if (!outOfSpawn) {
            //This will return original pose if zombie is out of spawn
            nextPose = moveOutOfSpawn(pose);
            if (nextPose == pose) {
                outOfSpawn = true;
            } else {
                return nextPose;
            }
        }
        return nextPose;
    }

}
