package server.game_engine.ai;

import data.Location;
import data.Pose;
import data.map.tile.Tile;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public abstract class EnemyAI {

    private Pose pose;
    private int size;
    private HashSet<Pose> playerPoses;
    private Tile[][] tileMap;
    private int maxDistanceMoved;
    private boolean isProcessing = false;
    private ArrayList<Pair<Integer, Integer>> path = null;
    private LinkedList<Pose> posePath;
    private Pose nextPose;
    private AIAction aiAction = AIAction.WAIT;

    protected EnemyAI() {
    }

    public abstract Attack getAttack();

    protected abstract void generatePath();

    public Pose getNewPose() {
        return nextPose;
    }

    public AIAction getAction() {
        return aiAction;
    }
//    protected void setAIAction(AIAction action){
//        this.aiAction = action;
//    }

    protected Tile[][] getTileMap() {
        return tileMap;
    }

    protected Pose getEnemPose() {
        return pose;
    }

    protected HashSet<Pose> getPlayerPoses() {
        return playerPoses;
    }

    public void setInfo(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved) {
        this.pose = pose;
        this.size = size;
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        this.maxDistanceMoved = maxDistanceMoved;
        isProcessing = true;
        generatePath();
    }

    public boolean isProcessing() {
        return isProcessing;
    }


    protected void generatePosePath(ArrayList<Pair<Integer, Integer>> path) {
        LinkedList<Pose> posePath = new LinkedList<>();
        //Add the current location
        posePath.add(pose);

        int corner;
        int x2;
        int y2;
        Pose nextPose;

        for (int i = 0; i < path.size() - 1; i++) {
            corner = calcTheCorner(path.get(i), path.get(i + 1));
            System.out.println(corner +
                    " 1st - " + path.get(i).getValue() + " " + path.get(i).getKey() +
                    " 2nd - " + path.get(i+1).getValue() + " " + path.get(i+1).getKey());
            try {
                TimeUnit.SECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Pair<Integer, Integer> nextTile = path.get(i + 1);
            Location centreOfNextTile = Tile.tileToLocation(nextTile.getValue(), nextTile.getKey());

            do {
                x2 = (int) (posePath.getFirst().getX() + maxDistanceMoved * Math.cos(corner));
                y2 = (int) (posePath.getFirst().getY() + maxDistanceMoved * Math.cos(corner));

                nextPose = new Pose(x2, y2);
                posePath.add(nextPose);

            } while ((nextPose.getX() != centreOfNextTile.getX()) && (nextPose.getY() != centreOfNextTile.getY()));
        }

        setPosePath(posePath);
    }

    private int calcTheCorner(Pair<Integer, Integer> current, Pair<Integer, Integer> next) {
        if (current.getValue() < next.getValue()) {
            if (current.getKey() < next.getKey()) {
                return 45;
            } else if (current.getKey() == next.getKey()) {
                return 0;
            } else if (current.getKey() > next.getKey()) {
                return 315;
            }
        } else if (current.getValue() == next.getValue()) {
            if (current.getKey() < next.getKey()) {
                return 90;
            } else if (current.getKey() < next.getKey()) {
                return 270;
            }
        } else if (current.getValue() > next.getValue()) {
            if (current.getKey() < next.getKey()) {
                return 135;
            } else if (current.getKey() == next.getKey()) {
                return 180;
            } else if (current.getKey() > next.getKey()) {
                return 225;
            }
        }
        return 5555;
    }

    private synchronized void setPosePath(LinkedList<Pose> posePath) {
        this.posePath = posePath;
        isProcessing = false;
        for (Pose pose : posePath) {
            System.out.println(pose.getX() + " " + pose.getY());
        }
        //aiAction = AIAction.MOVE;
    }

}
