package server.engine.ai;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import shared.lists.ActionList;

import java.util.LinkedList;

//Not needed currently but if we want to change the attack dmg we could do it here
public class RunnerZombieAI extends ZombieAI {


    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(getClosestPlayer(), 24, 1));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

}
