package server.engine.ai.enemyAI;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.HashSet;
import java.util.LinkedList;

import server.engine.ai.AIAction;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.TileState;

public abstract class EnemyAI {

    Enemy enemy;
    long attackDelay;
    static long SHORT_DELAY = 380;
    static long MEDIUM_DELAY = 500;
    static long LONG_DELAY = 1000;
    protected Pose pose;
    private boolean isProcessing = false;
    //    double maxDistanceToMove;
//    private int enemSize;
    private HashSet<Pose> playerPoses;
    Pose closestPlayer;
    static Tile[][] tileMap;
    //    protected int mapXDim;
//    protected int mapYDim;
    ActionList actionState;
    protected double maxMovementForce;
    long beginAttackTime;
    boolean attacking = false;

    protected EnemyAI(long attackDelay) {
        this.attackDelay = attackDelay;
        actionState = ActionList.NONE;
    }

    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(getAttackObj());
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    protected abstract Attack getAttackObj();

    protected abstract Force generateMovementForce();

    public abstract AIAction getAction();

    public Tile[][] getTileMap(){
        return tileMap;
    }

    public ActionList getActionState() {
        return actionState;
    }

    public Force getMovementForce(double maxMovementForce) {
        this.maxMovementForce = maxMovementForce;
        return generateMovementForce();
    }

    int getDistToPlayer(Pose player) {
        return (int) sqrt(pow(pose.getY() - player.getY(), 2) + pow(pose.getX() - player.getX(), 2));
    }

    //Static for RandomPoseGenerator
    public static boolean tileNotSolid(int[] tile, Tile[][] tileMap) {
        boolean tileNotSolid;
//        int mapXDim = tileMap.length;
//        int mapYDim = tileMap[0].length;

        try {
            tileNotSolid = (tileMap[tile[1]][tile[0]].getState() != TileState.SOLID);
//                    !((tileList[0] == 0 && tileList[1] == (mapYDim - 2) / 2) ||
//                            ((tileList[0] == mapXDim - 1 && tileList[1] == (mapYDim - 2) / 2)));
        } catch (Exception e) {
            return false;
        }

        return tileNotSolid;
    }

    public void setInfo(Enemy enemy, HashSet<Pose> playerPoses) {
        this.enemy = enemy;
        this.pose = this.enemy.getPose();
//        this.enemSize = this.enemy.getSize();
        this.playerPoses = playerPoses;
        //        this.tileMap = tileMap;
        this.closestPlayer = findClosestPlayer(playerPoses);
    }

    static Tile[][] transposeMatrix(Tile[][] m) {
        Tile[][] temp = new Tile[m[0].length][m.length];

        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];

        return temp;
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

    public static int getAngle(Pose enemy, Pose player) {
        int angle = (int) Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public Force getForceFromAttack(double maxMovementForce) {
        return new Force(pose.getDirection(), 0);
    }

    //TODO might be able to make this a bit more efficient with less steps to the enemy
    // and wider acceptance range. But this would make it less precise. But maybe that's ok.

    // Left the prints for debugging if it's ever needed
    public static boolean pathUnobstructed(Pose startPose, Pose endPose, Tile[][] tileMap){
        int[] currentTile;
        Pose currentPose = startPose;
//        System.out.println("\n\n\n\n\nstartPose:" + currentPose);
//        System.out.println("endPose:" + endPose);
        do {
            int angleToPlayer = getAngle(currentPose, endPose);
//            System.out.println("angle to player: " + angleToPlayer);
            currentPose = poseInDistance(currentPose, angleToPlayer, 25);
            currentTile = Tile.locationToTile(currentPose);

//            System.out.println("Current pose: " + currentPose);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            if(!tileNotSolid(currentTile, tileMap)) {
                return false;
            }

        }while(!Pose.compareLocation(currentPose, endPose, 15));

        return true;
    }

    // vec = dist * (cos(angle)i + sin(angle)j)
    public static Pose poseInDistance(Pose startingPose, int angleToPose, int distanceToPose) {
        double vecI = Math.cos(Math.toRadians(angleToPose)) * distanceToPose;
        double vecJ = Math.sin(Math.toRadians(angleToPose)) * distanceToPose;

//        System.out.println("VecI : " + vecI + " " + Math.cos(Math.toRadians(angleToPose)) + " vecJ : " + vecJ + " " + Math.sin(Math.toRadians(angleToPose)));

        return new Pose((int) vecI + startingPose.getX(), (int) vecJ + startingPose.getY());
    }

    public Enemy getUpdatedEnemy() {
        return enemy;
    }

    public synchronized boolean isProcessing() {
        return isProcessing;
    }

    public synchronized void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public static void setTileMap(Tile[][] tm) {
        tileMap = transposeMatrix(tm);
        int[] tile = { 8, 10 };
        tileNotSolid(tile, tileMap);
    }

//    protected abstract Pose generateNextPose();

//    public Pose getNewPose(double maxDistanceToMove) {
//        this.maxDistanceToMove = maxDistanceToMove;
//        return generateNextPose();

//    }


//    Pose poseFromAngle(double angleToMove, double angleToFace, double distToMove) {
//        Pose newPose = null;
//
//        //east
//        if (angleToMove > 337.5 || angleToMove <= 22.5) {
//            newPose = new Pose(pose.getX() + distToMove, pose.getY(), (int) angleToFace + 90);
//
//            //north-east
//        } else if (angleToMove > 22.5 && angleToMove <= 67.5) {
//            newPose = new Pose(pose.getX() + distToMove, pose.getY() + distToMove, (int) angleToFace + 90);
//
//            //north
//        } else if (angleToMove > 67.5 && angleToMove <= 112.5) {
//            newPose = new Pose(pose.getX(), pose.getY() + distToMove, (int) angleToFace + 90);
//
//            //north-west
//        } else if (angleToMove > 112.5 && angleToMove <= 157.5) {
//            newPose = new Pose(pose.getX() - distToMove, pose.getY() + distToMove, (int) angleToFace + 90);
//
//            //west
//        } else if (angleToMove > 157.5 && angleToMove <= 202.5) {
//            newPose = new Pose(pose.getX() - distToMove, pose.getY(), (int) angleToFace + 90);
//
//            //south-west
//        } else if (angleToMove > 202.5 && angleToMove <= 247.5) {
//            newPose = new Pose(pose.getX() - distToMove, pose.getY() - distToMove, (int) angleToFace + 90);
//
//            //south
//        } else if (angleToMove > 247.5 && angleToMove <= 292.5) {
//            newPose = new Pose(pose.getX(), pose.getY() - distToMove, (int) angleToFace + 90);
//
//            //south-east
//        } else if (angleToMove > 292.5 && angleToMove <= 337.5) {
//            newPose = new Pose(pose.getX() + distToMove, pose.getY() - distToMove, (int) angleToFace + 90);
//        }
//
//        if (newPose != null) {
//            if (tileNotSolid(TileList.locationToTile(newPose), tileMap)) {
//                return newPose;
//            }
//        }
//
//        return pose;
//
//    }

//    private Pose moveOutOfSpawn(Pose pose) {
//        int[] tileList = TileList.locationToTile(pose);
//
//        if ((tileList[0] == 0 && tileList[1] == (mapYDim - 2) / 2)
//                || (tileList[0] == 1 && tileList[1] == (mapYDim - 2) / 2)) {
//            return new Pose(pose.getX() + maxDistanceToMove, pose.getY(), 90);
//        }
//
//        if ((tileList[0] == mapXDim - 1 && tileList[1] == (mapYDim - 2) / 2)
//                || (tileList[0] == mapXDim - 2 && tileList[1] == (mapYDim - 2) / 2)) {
//            return new Pose(pose.getX() - maxDistanceToMove, pose.getY(), 270);
//
//        }
//
//        return pose;

//    }
//    Pose checkIfInSpawn() {
//        Pose nextPose = pose;
//        if (!outOfSpawn) {
//            //This will return original pose if zombie is out of spawn
//            nextPose = moveOutOfSpawn(pose);
//            if (nextPose == pose) {
//                outOfSpawn = true;
//            } else {
//                return nextPose;
//            }
//        }
//        return nextPose;

//    }

}
