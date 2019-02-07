package server.game_engine.ai;

import data.Pose;

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
    public Pose getNewPose() {
        // TODO Auto-generated method stub
        return null;
    }

}
