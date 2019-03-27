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
    private HashSet<Pose> playerPoses;
    Pose closestPlayer;
    static Tile[][] tileMap;
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

    public HashSet<Pose> getPlayerPoses(){
        return playerPoses;
    }

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

        try {
            tileNotSolid = (tileMap[tile[1]][tile[0]].getState() != TileState.SOLID);
        } catch (Exception e) {
            return false;
        }

        return tileNotSolid;
    }

    public void setInfo(Enemy enemy, HashSet<Pose> playerPoses) {
        this.enemy = enemy;
        this.pose = this.enemy.getPose();
        this.playerPoses = playerPoses;
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
        Pose closestPlayer;
        try {
            closestPlayer = playerPoses.iterator().next();
            int distToClosest = Integer.MAX_VALUE;

            for (Pose playerPose : playerPoses) {
                int distToPlayer = getDistToPlayer(playerPose);
                if (distToPlayer < distToClosest) {
                    distToClosest = distToPlayer;
                    closestPlayer = playerPose;
                }
            }
        }catch (Exception e){
            return null;
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
    }

}
