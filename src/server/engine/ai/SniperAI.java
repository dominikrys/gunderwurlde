package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.SniperRifle;
import server.engine.state.physics.Force;

import java.util.LinkedList;

public class SniperAI extends EnemyAI {

    private final int RANGE_TO_RUN_AWAY;
    Gun gun = new SniperRifle();

    public SniperAI(int rangeToRunAway){
        super();
        this.RANGE_TO_RUN_AWAY = rangeToRunAway;
    }

    @Override
    public AIAction getAction() {
//        if(){
//
//        }
        return null;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        return null;
    }

    @Override
    protected Force generateMovementForce() {
        return null;
    }
}
