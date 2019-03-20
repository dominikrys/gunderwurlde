package server.engine.ai.enemyAI;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import shared.Constants;

public class BoomerAI extends ZombieAI {

    public BoomerAI(){
        super(Constants.TILE_SIZE * 2);
        randomizePath = false;
        attackDelay = LONG_DELAY;
    }

    @Override
    protected Attack getAttackObj() {
        return new AoeAttack(pose, 50, 3);
    }
}
