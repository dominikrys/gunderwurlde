package server.game_engine.ai;

import data.Pose;

public class ZombieAI extends EnemyAI {

    private boolean newPoseExists = false;

    public ZombieAI() {
        super();
        System.out.println("A zombie is created");
    }

    @Override
    public AIAction getAction() {
        if (newPoseExists) {
            return AIAction.MOVE;
        } else {
            return AIAction.WAIT;
        }
    }

    @Override
    public Attack getAttack() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pose getNewPose() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void generatePath() {
        //System.out.println("Size of playerPoses: " + getPlayerPoses().size());
        new AStar(this, 1, getTileMap(), getEnemPose(), getPlayerPoses().iterator().next()).start();
    }




}
