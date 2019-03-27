package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.Plasma;
import server.engine.state.entity.projectile.Projectile;
import shared.lists.AmmoList;
import shared.lists.ItemList;

public class PlasmaPistol extends ProjectileGun {
    public static final ItemList NAME = ItemList.PLASMA_PISTOL;
    public static final int DEFAULT_CLIP_SIZE = 100;
    public static final int DEFAULT_RELOAD_TIME = 500;
    public static final int DEFAULT_AMMO_PER_SHOT = 10;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 1;
    public static final Projectile DEFAULT_PROJECTILE = new Plasma();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.ENERGY;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 400;
    public static final int DEFAULT_ACCURACY = 2;


    public PlasmaPistol() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
        this.reloading = true;
        reloadStartTime = System.currentTimeMillis();
    }

    @Override
    public boolean shoot(int a) {
        boolean result = super.shoot(a);
        reloading = true;
        return result;
    }

    @Override
    public boolean attemptReload(int a) {
        return false;
    }

    @Override
    public int reload(int a) {
        int taken = super.reload(1);
        if (taken == 1) {
            reloading = true;
            reloadStartTime = System.currentTimeMillis();
        }
        return 0;
    }

    @Override
    public void cancelReload() {

    }

}
