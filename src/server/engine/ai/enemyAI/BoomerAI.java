package server.engine.ai.enemyAI;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import shared.Constants;
import shared.lists.Team;

public class BoomerAI extends ZombieAI {

    public BoomerAI(){
        super(Constants.TILE_SIZE * 2, LONG_DELAY);
        randomizePath = false;
    }

    @Override
    protected Attack getAttackObj() {
        return new AoeAttack(pose, 50, 3, Team.NONE);
    }
}
