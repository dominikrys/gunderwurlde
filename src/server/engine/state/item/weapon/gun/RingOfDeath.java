package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
import shared.lists.AmmoList;
import shared.lists.ItemList;

public class RingOfDeath extends ProjectileGun {
    public static final ItemList NAME = ItemList.RING_OF_DEATH;
    public static final int DEFAULT_CLIP_SIZE = 666;
    public static final int DEFAULT_RELOAD_TIME = 6000;
    public static final int DEFAULT_AMMO_PER_SHOT = 16;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 32;
    public static final Projectile DEFAULT_PROJECTILE = new SmallBullet();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.BASIC_AMMO;
    public static final int DEFAULT_SPREAD = 180;
    public static final int DEFAULT_COOL_DOWN = 200; // 5ps
    public static final int DEFAULT_ACCURACY = 5;

    public RingOfDeath() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }

}
