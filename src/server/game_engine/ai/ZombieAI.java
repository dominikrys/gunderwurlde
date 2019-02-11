package server.game_engine.ai;

import data.Pose;

public class ZombieAI extends EnemyAI {

    private boolean newPoseExists = false;

    public ZombieAI() {
        super();
        System.out.println("A zombie is created");
    }

    @Override
    public Attack getAttack() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void generatePath() {
        //System.out.println("Size of playerPoses: " + getPlayerPoses().size());
        System.out.println(" genPath called");
        new AStar(this, 0.8, getTileMap(), getEnemPose(), getPlayerPoses().iterator().next()).run();
    }




}
