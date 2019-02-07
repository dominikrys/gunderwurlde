package server.game_engine.ai;

import data.entity.EntityList;

import java.util.Optional;

public class Attack {
    protected int attackDirection;
    protected int attackSize;
    protected AttackType attackType;
    protected Optional<EntityList> projectileType;
}
