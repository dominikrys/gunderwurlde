package server.game_engine.ai;

import java.util.ArrayList;
import java.util.HashSet;

import data.Pose;
import data.map.tile.Tile;
import javafx.util.Pair;

public abstract class EnemyAI {

    private Pose pose;
    private int size;
    private HashSet<Pose> playerPoses;
    private Tile [][] tileMap;
    private int maxDistanceMoved;
    protected boolean isProcessing = true;
    protected ArrayList<Pair<Integer, Integer>> path;

    protected EnemyAI() {
    }

    public abstract AIAction getAction();

    public abstract Attack getAttack();

    public abstract Pose getNewPose();

    protected abstract void getPath();

    protected Tile[][] getTileMap(){
        return tileMap;
    }

    protected Pose getEnemPose(){
        return pose;
    }

    protected HashSet<Pose> getPlayerPoses(){
        return playerPoses;
    }

    public void setInfo(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved){
        System.out.println("setInfo");
        this.pose = pose;
        this.size = size;
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        this.maxDistanceMoved = maxDistanceMoved;
        isProcessing = true;
        getPath();
    }

    public boolean isProcessing(){
        return isProcessing;
    }

    protected void setPath(ArrayList<Pair<Integer, Integer>> path){
        this.path = path;

        for (Pair<Integer, Integer> pair : path) {
            System.out.println(pair);
        }

        this.isProcessing = false;
    }

}
