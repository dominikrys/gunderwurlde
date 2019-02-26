package server.engine.state.entity.enemy;

import server.engine.ai.SoldierZombieAI;
import shared.lists.EntityList;

public class SoldierZombie extends Zombie {

    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_SCORE_ON_KILL = 30;
    private final int RANGE_TO_SHOOT;
    private final int RATE_OF_FIRE;

    public SoldierZombie(int range_to_shoot, int rate_of_fire){
        super(DEFAULT_HEALTH, DEFAULT_MOVESPEED, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL, new SoldierZombieAI(range_to_shoot, rate_of_fire),
                EntityList.SOLDIER);

        this.RANGE_TO_SHOOT = range_to_shoot;
        this.RATE_OF_FIRE = rate_of_fire;
    }

    @Override
    public Enemy makeCopy() {
        return new Zombie(this.maxHealth, this.moveSpeed, this.size, this.drops, this.scoreOnKill, new SoldierZombieAI(RANGE_TO_SHOOT, RATE_OF_FIRE));
    }


}
