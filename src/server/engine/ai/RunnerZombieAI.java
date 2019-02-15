package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import shared.Pose;

import java.util.LinkedList;
//TODO ask about speed
public class RunnerZombieAI extends EnemyAI{
    @Override
    public LinkedList<Attack> getAttacks() {
        return null;
    }

    @Override
    protected Pose generateNextPose(double maxDistanceMoved, Pose closestPlayer) {
        return null;
    }

    @Override
    public AIAction getAction() {
        return null;
    }
}
