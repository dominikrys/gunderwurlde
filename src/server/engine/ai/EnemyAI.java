package server.engine.ai;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.map.Meadow;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.TileState;

import static java.lang.Math.*;

public abstract class EnemyAI {

    protected Enemy enemy;
    protected static long DEFAULT_DELAY = 380;
    protected Pose pose;
    private int enemSize;
    private HashSet<Pose> playerPoses;
    protected Pose closestPlayer;
    protected Tile[][] tileMap;
    private boolean isProcessing;
    protected ActionList actionState;

    protected EnemyAI() {
        isProcessing = false;
        actionState = ActionList.NONE;
    }

    public abstract LinkedList<Attack> getAttacks();

    protected abstract Pose generateNextPose(double maxDistanceToMove, Pose closestPlayer);

    public abstract AIAction getAction();



    protected int getEnemSize() { return enemSize; }

    protected HashSet<Pose> getPlayerPoses() {
        return playerPoses;
    }

    public ActionList getActionState() {
        return actionState;
    }

    public Pose getCurrentPose(){
        return pose;
    }

    Pose getClosestPlayer(){ return closestPlayer; }

    public Pose getNewPose(double maxDistanceToMove) {
        return generateNextPose(maxDistanceToMove, closestPlayer);
    }

    int getDistToPlayer(Pose player) {
        return (int) sqrt(pow(pose.getY() - player.getY(), 2) + pow(pose.getX() - player.getX(), 2));
    }

    synchronized void setProcessing(boolean processing){
        isProcessing = processing;
    }

    public void setInfo(Enemy enemy, HashSet<Pose> playerPoses, Tile[][] tileMap) {
        this.enemy = enemy;
        this.pose = this.enemy.getPose();
        this.enemSize = this.enemy.getSize();
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        this.closestPlayer = findClosestPlayer(playerPoses);
    }

    // May not need this
    public boolean isProcessing() {
        return isProcessing;
    }

    private Pose findClosestPlayer(HashSet<Pose> playerPoses) {
        Pose closestPlayer = playerPoses.iterator().next();
        int distToClosest = Integer.MAX_VALUE;

        for (Pose playerPose : playerPoses) {
            int distToPlayer = getDistToPlayer(playerPose);
            if(distToPlayer < distToClosest){
                distToClosest = distToPlayer;
                closestPlayer = playerPose;
            }
        }

        return closestPlayer;
    }

    //TODO I don't really need to pass enemy do I?
    static Pose poseByAngle(double angle, Pose enemy, double angleToFace, Tile [][] tileMap) {
        Pose newPose = null;

        //east
        if (angle > 337.5 || angle <= 22.5) {
            newPose = new Pose(enemy.getX() + 1, enemy.getY(), (int) angleToFace + 90);

            //north-east
        } else if (angle > 22.5 && angle <= 67.5) {
            newPose = new Pose(enemy.getX() + 1, enemy.getY() + 1, (int) angleToFace + 90);

            //north
        } else if (angle > 67.5 && angle <= 112.5) {
            newPose = new Pose(enemy.getX(), enemy.getY() + 1, (int) angleToFace + 90);

            //north-west
        } else if (angle > 112.5 && angle <= 157.5) {
            newPose = new Pose(enemy.getX() - 1, enemy.getY() + 1, (int) angleToFace + 90);

            //west
        } else if (angle > 157.5 && angle <= 202.5) {
            newPose = new Pose(enemy.getX() - 1, enemy.getY(), (int) angleToFace + 90);

            //south-west
        } else if (angle > 202.5 && angle <= 247.5) {
            newPose = new Pose(enemy.getX() - 1, enemy.getY() - 1, (int) angleToFace + 90);

            //south
        } else if (angle > 247.5 && angle <= 292.5) {
            newPose = new Pose(enemy.getX(), enemy.getY() - 1, (int) angleToFace + 90);

            //south-east
        } else if (angle > 292.5 && angle <= 337.5) {
            newPose = new Pose(enemy.getX() + 1, enemy.getY() - 1, (int) angleToFace + 90);
        }

        if (newPose != null) {
            if (tileNotSolid(Tile.locationToTile(newPose), tileMap))
                return newPose;
        }

        return enemy;
    }

    static boolean tileNotSolid(int[] tile, Tile [][] tileMap) {
        boolean tileNotSolid;
        try {
            tileNotSolid = (tileMap[tile[0]][tile[1]].getState() != TileState.SOLID) &&
                    !((tile[0] == 0 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2) ||
                            ((tile[0] == Meadow.DEFAULT_X_DIM - 1 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2)));
        }catch (Exception e){
            System.out.println("enemy wants to go out of map");
            return false;
        }

        return  tileNotSolid;
    }

    static double getAngle(Pose enemy, Pose player) {
        double angle = Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    static Pose checkIfInSpawn(Pose pose){
        int[] tile = Tile.locationToTile(pose);

        if ((tile[0] == 0 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2)
        || (tile[0] == 1 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2)) {
            return new Pose(pose.getX() + 1, pose.getY(), 90);
        }

        if ((tile[0] == Meadow.DEFAULT_X_DIM - 1 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2)
        || (tile[0] == Meadow.DEFAULT_X_DIM - 2 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2)) {
            return new Pose(pose.getX() - 1, pose.getY(), 270);
        }

        return pose;
    }

}
