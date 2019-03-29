package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.HeavyBullet;
import server.engine.state.entity.projectile.Projectile;
import shared.lists.AmmoList;
import shared.lists.ItemList;

/**
 * 
 * @author Richard
 *
 */
public class HeavyPistol extends ProjectileGun {
    public static final ItemList NAME = ItemList.HEAVY_PISTOL;
    public static final int DEFAULT_CLIP_SIZE = 12;
    public static final int DEFAULT_RELOAD_TIME = 3000;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 1;
    public static final Projectile DEFAULT_PROJECTILE = new HeavyBullet();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.HEAVY_AMMO;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 600;
    public static final int DEFAULT_ACCURACY = 3;

    public HeavyPistol() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }

}
