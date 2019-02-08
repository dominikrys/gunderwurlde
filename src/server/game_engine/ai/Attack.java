package server.game_engine.ai;

import java.util.Optional;

import data.entity.EntityList;

public class Attack {
    protected int attackDirection;
    protected int attackSize;
    protected AttackType attackType;
    protected Optional<EntityList> projectileType;
}
