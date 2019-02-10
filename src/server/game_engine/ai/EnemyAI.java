package server.game_engine.ai;

import data.Constants;
import data.Pose;
import data.map.tile.Tile;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public abstract class EnemyAI {

    private Pose pose;
    private int size;
    private HashSet<Pose> playerPoses;
    private Tile[][] tileMap;
    private boolean isProcessing = false;
    //private ArrayList<Pair<Integer, Integer>> path = null;
    private LinkedHashSet<Pose> posePath;
    //private Pose nextPose;
    private AIAction aiAction = AIAction.WAIT;

    protected EnemyAI() {
    }

    public abstract Attack getAttack();

    protected abstract void generatePath();

    public synchronized Pose getNewPose(int maxDistanceMoved) {
        Pose next = pose;
        Iterator<Pose> i = posePath.iterator();

        for (int j = 0; j < maxDistanceMoved; j++) {
            if((i.hasNext()) && (posePath.size() > Constants.TILE_SIZE)) {
                next = i.next();
                next.setDirection(calcDirection(pose, next));
                i.remove();
            }
        }

        this.pose = next;
        return next;
    }


    public synchronized AIAction getAction() {
        return aiAction;
    }

    protected Tile[][] getTileMap() {
        return tileMap;
    }

    protected Pose getEnemPose() {
        return pose;
    }

    protected HashSet<Pose> getPlayerPoses() {
        return playerPoses;
    }

    public void setInfo(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap) {
        this.pose = pose;
        this.size = size;
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        isProcessing = true;
        generatePath();
    }

    public synchronized boolean isProcessing() {
        return isProcessing;
    }

    private int calcDirection(Pose current, Pose next) {
        if (current.getY() < next.getY()) {
            if (current.getX() < next.getX()) {
                return 135;
            } else if (current.getX() == next.getX()) {
                return 180;
            } else if (current.getX() > next.getX()) {
                return 225;
            }
        } else if (current.getY() == next.getY()) {
            if (current.getX() < next.getX()) {
                return 90;
            } else if (current.getX() > next.getX()) {
                return 270;
            }
        } else if (current.getY() > next.getY()) {
            if (current.getX() < next.getX()) {
                return 45;
            } else if (current.getX() == next.getX()) {
                return 0;
            } else if (current.getX() > next.getX()) {
                return 315;
            }
        }
        return 5555;
    }

    protected synchronized void setPosePath(LinkedHashSet<Pose> posePath) {
        this.posePath = posePath;
        isProcessing = false;
//        for (Pose pose : posePath) {
//            System.out.println(pose.getX() + " " + pose.getY());
//        }
        aiAction = AIAction.MOVE;
    }

}
