package server.game_engine.ai;

import java.util.HashSet;

import data.Pose;
import data.map.tile.Tile;

public class ZombieAI extends EnemyAI {

    public ZombieAI(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved) {
        super(pose,size,playerPoses,tileMap,maxDistanceMoved);
        // TODO Auto-generated constructor stub
    }

}
