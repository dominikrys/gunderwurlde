package server.game_engine.ai;

import java.util.Optional;

import data.entity.projectile.ProjectileList;

public class Attack {
    protected int attackDirection;
    protected int attackSize;
    protected AttackType attackType;
    protected Optional<ProjectileList> projectileType;
}
