package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
import shared.lists.AmmoList;
import shared.lists.ItemList;

public class Shotgun extends Gun {
    public static final ItemList NAME = ItemList.SHOTGUN;
    public static final int DEFAULT_CLIP_SIZE = 8;
    public static final int DEFAULT_RELOAD_TIME = 500;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 5;
    public static final Projectile DEFAULT_PROJECTILE = new SmallBullet();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.SHOTGUN_ROUND;
    public static final int DEFAULT_SPREAD = 20;
    public static final int DEFAULT_COOL_DOWN = 600;
    public static final int DEFAULT_ACCURACY = 6;

    public Shotgun() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }
    public Shotgun(int projectilesPerShot, int spread, int coolDown) {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, spread, coolDown, projectilesPerShot, DEFAULT_ACCURACY);
    }

    @Override
    public int reload(int amountAvailable) {
        int amountTaken = 0;
        if (amountAvailable > 0) {
            amountTaken = super.reload(1);
            if (amountTaken > 0) {
                reloading = true;
                reloadStartTime = System.currentTimeMillis();
            }
        } else {
            reloading = false;
        }
        return amountTaken;
    }

}
