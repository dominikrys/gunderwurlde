package server.engine.ai;

import java.util.LinkedList;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import shared.Constants;
import shared.lists.ActionList;

public class BoomerAI extends ZombieAI {

    public BoomerAI(){
        super(Constants.TILE_SIZE * 2);
        randomizePath = false;
        attackDelay = LONG_DELAY;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(pose, 90, 3));
            attacking = false;
            this.actionState = ActionList.NONE;
            enemy.damage(5);
        }
        return attacks;
    }

}
