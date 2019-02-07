package server.game_engine.ai;

import java.util.HashSet;

import data.Location;
import data.Pose;
import data.map.tile.Tile;

public class ZombieAI extends EnemyAI {

    public ZombieAI() {
        super();
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
    public Pose getNewLocation() {
        // TODO Auto-generated method stub
        return null;
    }

}
