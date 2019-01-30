package server.game_engine.ai;

import java.util.HashSet;

import data.Location;
import data.Pose;
import data.map.tile.Tile;
import server.game_engine.ai.Attack;

public abstract class EnemyAI {

    public EnemyAI(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved) {
        // TODO Auto-generated constructor stub
    }

    public AIAction getAction() {
        // TODO Auto-generated method stub
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
