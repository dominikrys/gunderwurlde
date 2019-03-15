package server.engine.ai;

import server.engine.state.entity.attack.AoeAttack;
import shared.Constants;

public class BoomerAI extends ZombieAI {

    public BoomerAI(){
        super(Constants.TILE_SIZE * 2);
        randomizePath = false;
        attackDelay = LONG_DELAY;
        attack = new AoeAttack(pose, 50, 3);
    }
}
