package server.game_engine.ai;

import java.util.HashSet;

import data.Location;
import data.Pose;
import data.map.tile.Tile;
import server.game_engine.ai.Attack;

public abstract class EnemyAI {

    private Pose pose;
    private int size;
    private HashSet<Pose> playerPoses;
    private Tile [][] tileMap;
    private int maxDistanceMoved;

    protected EnemyAI(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved) {
        this.pose = pose;
        this.size = size;
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
        this.maxDistanceMoved = maxDistanceMoved;
    }

    public AIAction getAction() {
        return AIAction.WAIT;
    }

    public Attack getAttack() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getDirection() { //direction the enemy should face
        // TODO Auto-generated method stub
        return 0;
    }

    public Location getNewLocation() {
        // TODO Auto-generated method stub
        return null;
    } 

}
