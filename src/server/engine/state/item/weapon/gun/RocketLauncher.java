package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.Rocket;
import shared.lists.AmmoList;
import shared.lists.ItemList;

/**
 * 
 * @author Richard
 *
 */
public class RocketLauncher extends ProjectileGun {
    public static final ItemList NAME = ItemList.ROCKET_LAUNCHER;
    public static final int DEFAULT_CLIP_SIZE = 1;
    public static final int DEFAULT_RELOAD_TIME = 3000;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 1;
    public static final Projectile DEFAULT_PROJECTILE = new Rocket();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.ROCKET_AMMO;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 0;
    public static final int DEFAULT_ACCURACY = 0;

    public RocketLauncher() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }

}
