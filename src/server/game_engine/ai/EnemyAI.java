package server.game_engine.ai;

import java.util.HashSet;

import data.Pose;
import data.map.tile.Tile;

public abstract class EnemyAI {

    private Pose pose;
    private int size;
    private HashSet<Pose> playerPoses;
    private Tile [][] tileMap;
    private int maxDistanceMoved;
    private boolean isProcessing = false;

    protected EnemyAI() {
//        this.pose = pose;
//        this.size = size;
//        this.playerPoses = playerPoses;
//        this.tileMap = tileMap;
//        this.maxDistanceMoved = maxDistanceMoved;
    }

    public void setInfo(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved){
        this.pose = pose;
        this.size = size;
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        this.maxDistanceMoved = maxDistanceMoved;
    }

    public boolean isProcessing(){
        return isProcessing;
    }

    public AIAction getAction() {
        return AIAction.WAIT;
    }

    public Attack getAttack() {
        // TODO Auto-generated method stub
        return null;
    }

    public Pose getNewPose() {
        // TODO Auto-generated method stub
        return null;
    } 

}
