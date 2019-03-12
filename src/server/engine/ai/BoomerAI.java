package server.engine.ai;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.enemy.Zombie;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.lists.ActionList;

import java.util.LinkedList;

public class BoomerAI extends ZombieAI {

    public BoomerAI(){
        super();
        distanceToPlayerForAttack = Constants.TILE_SIZE * 2;
        randomizePath = false;
        attackDelay = LONG_DELAY;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(pose, 50, 3));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        enemy.damage(5);
        return attacks;
    }

}
