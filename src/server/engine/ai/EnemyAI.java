package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
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
    long attackDelay;
    static long SHORT_DELAY = 380;
    static long MEDIUM_DELAY = 500;
    static long LONG_DELAY = 1000;
    protected Pose pose;
//    double maxDistanceToMove;
//    private int enemSize;
    private HashSet<Pose> playerPoses;
    Pose closestPlayer;
    protected Tile[][] tileMap;
//    protected int mapXDim;
//    protected int mapYDim;
    private boolean isProcessing;
    ActionList actionState;
    protected double maxMovementForce;

    protected EnemyAI() {
        this.attackDelay = SHORT_DELAY;
        isProcessing = false;
        actionState = ActionList.NONE;
    }

    public abstract LinkedList<Attack> getAttacks();

    protected abstract Force generateMovementForce();

    public abstract AIAction getAction();

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
            return false;
        }

        return tileNotSolid;
    }

    public void setInfo(Enemy enemy, HashSet<Pose> playerPoses, Tile[][] tileMap) {
        this.enemy = enemy;
        this.pose = this.enemy.getPose();
//        this.enemSize = this.enemy.getSize();
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
//        this.mapXDim = tileMap.length;
//        this.mapYDim = tileMap[0].length;
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

    static int getAngle(Pose enemy, Pose player) {
        int angle = (int) Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public Force getForceFromAttack(double maxMovementForce) {
        return new Force(0, 0);
    }

    public static boolean pathUnobstructed(Pose startPose, Pose endPose, Tile[][] tileMap){
        int[] currentTile;
        int[] endTile = Tile.locationToTile(endPose);
        int directionToTheEndPose = getAngle(startPose, endPose);
        System.out.println("\n\n\ndirection to shoot: "  + directionToTheEndPose);
        Pose currentPose = startPose;
//        System.out.println("startPose:" + currentPose);
        do {
            int angleToPlayer = getAngle(currentPose, endPose);
            System.out.println("angle to player: " + angleToPlayer);
            currentPose = poseInDistance(currentPose, directionToTheEndPose, 15);
            currentTile = Tile.locationToTile(currentPose);

            System.out.println("Current pose: " + currentPose);
            System.out.println("current tile: " + currentTile[0] + " " + currentTile[1]);
            System.out.println("player tile: " + endTile[0] + " " + endTile[1]);
            System.out.println("NotSolid? - " + tileNotSolid(currentTile, tileMap));

            if(!tileNotSolid(currentTile, tileMap)) {
                System.out.println("Return false");
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                return false;
            }

        }while(!Pose.compareLocation(currentPose, endPose, 15));

        System.out.println("Return true");
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        return true;
    }

    private static Pose poseInDistance(Pose startingPose, int angleToPose, int distanceToPose) {
        double vecI = Math.cos(angleToPose) * distanceToPose;
        double vecJ = Math.sin(angleToPose) * distanceToPose;

        return new Pose((int) vecI + startingPose.getX(), (int) vecJ + startingPose.getY());
    }

//    static Pose poseFromAngle(Pose currentPose, double angleToMove, double distToMove) {
//        Pose newPose = null;
//
//        //east
//        if (angleToMove > 337.5 || angleToMove <= 22.5) {
//            newPose = new Pose(currentPose.getX() + distToMove, currentPose.getY());
//
//            //north-east
//        } else if (angleToMove > 22.5 && angleToMove <= 67.5) {
//            newPose = new Pose(currentPose.getX() + distToMove, currentPose.getY() + distToMove);
//
//            //north
//        } else if (angleToMove > 67.5 && angleToMove <= 112.5) {
//            newPose = new Pose(currentPose.getX(), currentPose.getY() + distToMove);
//
//            //north-west
//        } else if (angleToMove > 112.5 && angleToMove <= 157.5) {
//            newPose = new Pose(currentPose.getX() - distToMove, currentPose.getY() + distToMove);
//
//            //west
//        } else if (angleToMove > 157.5 && angleToMove <= 202.5) {
//            newPose = new Pose(currentPose.getX() - distToMove, currentPose.getY());
//
//            //south-west
//        } else if (angleToMove > 202.5 && angleToMove <= 247.5) {
//            newPose = new Pose(currentPose.getX() - distToMove, currentPose.getY() - distToMove);
//
//            //south
//        } else if (angleToMove > 247.5 && angleToMove <= 292.5) {
//            newPose = new Pose(currentPose.getX(), currentPose.getY() - distToMove);
//
//            //south-east
//        } else if (angleToMove > 292.5 && angleToMove <= 337.5) {
//            newPose = new Pose(currentPose.getX() + distToMove, currentPose.getY() - distToMove);
//        }
//
//        return newPose;
//    }


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
//            if (tileNotSolid(Tile.locationToTile(newPose), tileMap)) {
//                return newPose;
//            }
//        }
//
//        return pose;
//
//    }

//    private Pose moveOutOfSpawn(Pose pose) {
//        int[] tile = Tile.locationToTile(pose);
//
//        if ((tile[0] == 0 && tile[1] == (mapYDim - 2) / 2)
//                || (tile[0] == 1 && tile[1] == (mapYDim - 2) / 2)) {
//            return new Pose(pose.getX() + maxDistanceToMove, pose.getY(), 90);
//        }
//
//        if ((tile[0] == mapXDim - 1 && tile[1] == (mapYDim - 2) / 2)
//                || (tile[0] == mapXDim - 2 && tile[1] == (mapYDim - 2) / 2)) {
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
