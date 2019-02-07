package server.game_engine.ai;

import java.util.HashSet;

import data.Location;
import data.Pose;
import data.map.tile.Tile;

public class ZombieAI extends EnemyAI {

    public ZombieAI(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap, int maxDistanceMoved) {
        super(pose,size,playerPoses,tileMap,maxDistanceMoved);
        System.out.println("A Zombie is created");
    }

    @Override
    public AIAction getAction() {

        return AIAction.WAIT;
    }

    @Override
    public Attack getAttack() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getDirection() { //direction the enemy should face
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Location getNewLocation() {
        // TODO Auto-generated method stub
        return null;
    }

}
