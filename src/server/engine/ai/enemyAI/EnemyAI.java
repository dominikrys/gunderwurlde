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

/**
 * A class the all of the enemies must extend. Has the methods and variables that are
 * needed for the AI to function
 */
public abstract class EnemyAI {

    /**
     * The enemy object for the AI
     */
    Enemy enemy;
    /**
     * How long to wait before shooting
     */
    long attackDelay;
    static long SHORT_DELAY = 380;
    static long MEDIUM_DELAY = 500;
    static long LONG_DELAY = 1000;
    /**
     * Current pose of the enemy
     */
    protected Pose pose;
    /**
     *  A boolean to know if any of the threads are in process
     */
    private boolean isProcessing = false;
    /**
     * All of the alive player poses
     */
    private HashSet<Pose> playerPoses;
    /**
     * The pose of the closest player
     */
    Pose closestPlayer;
    /**
     * The map
     */
    static Tile[][] tileMap;
    /**
     * The current state of the enemy
     */
    ActionList actionState;
    /**
     * The maximum amount of force that is possible to use for the force objects
     */
    double maxMovementForce;
    /**
     * The time when the state was set to attack
     */
    long beginAttackTime;
    /**
     * A boolean to know if the enemy is attacking
     */
    boolean attacking = false;

    protected EnemyAI(long attackDelay) {
        this.attackDelay = attackDelay;
        actionState = ActionList.NONE;
    }

    /**
     * A generic getAttack method that checks if
     * the delay has passed for the attack and attacks if so.
     *
     * @return
     */
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

    /**
     * Checks if a tile is not solid
     *
     * @param tile
     * @param tileMap
     * @return
     */
    public static boolean tileNotSolid(int[] tile, Tile[][] tileMap) {
        boolean tileNotSolid;

        try {
            tileNotSolid = (tileMap[tile[1]][tile[0]].getState() != TileState.SOLID);
        } catch (Exception e) {
            return false;
        }

        return tileNotSolid;
    }

    /**
     * A method that hte engine calls every thick to update the ai with the latest information about the game
     * @param enemy
     * @param playerPoses
     */
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

    /**
     * Gets the angle between two poses
     *
     * @param enemy
     * @param player
     * @return
     */
    public static int getAngle(Pose enemy, Pose player) {
        try {
            int angle = (int) Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

            if (angle < 0) {
                angle += 360;
            }

            return angle;
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * The force taken to the enemy after it attacks
     * @param maxMovementForce
     * @return
     */
    public Force getForceFromAttack(double maxMovementForce) {
        return new Force(pose.getDirection(), 0);
    }

    /**
     * Checks if the path between two poses is unobstructed.
     *
     * @param startPose
     * @param endPose
     * @param tileMap
     * @return
     */
    public static boolean pathUnobstructed(Pose startPose, Pose endPose, Tile[][] tileMap){
        int[] currentTile;
        Pose currentPose = startPose;
        do {
            int angleToPlayer = getAngle(currentPose, endPose);
            currentPose = poseInDistance(currentPose, angleToPlayer, 25);
            currentTile = Tile.locationToTile(currentPose);

            if(!tileNotSolid(currentTile, tileMap)) {
                return false;
            }

        }while(!Pose.compareLocation(currentPose, endPose, 15));

        return true;
    }

    /**
     * Generates a new pose from the starting pose
     *
     * @param startingPose pose from where to start calculations
     * @param angleToPose   The angle between the new pose and the starting pose
     * @param distanceToPose    The distance between new and starting poses
     * @return
     */
    // vec = dist * (cos(angle)i + sin(angle)j)
    public static Pose poseInDistance(Pose startingPose, int angleToPose, int distanceToPose) {
        double vecI = Math.cos(Math.toRadians(angleToPose)) * distanceToPose;
        double vecJ = Math.sin(Math.toRadians(angleToPose)) * distanceToPose;

        return new Pose((int) vecI + startingPose.getX(), (int) vecJ + startingPose.getY());
    }

    /**
     * Should only be called when all of the players are dead in the game.
     * @return
     */
    public Enemy getUpdatedEnemy() {
        enemy.setAI(new SoldierZombieAI(0, 0, 150));
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
