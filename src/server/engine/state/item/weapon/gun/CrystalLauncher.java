package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.CrystalBullet;
import server.engine.state.entity.projectile.Projectile;
import shared.lists.AmmoList;
import shared.lists.ItemList;

public class CrystalLauncher extends Gun {
    public static final ItemList NAME = ItemList.CRYSTAL_LAUNCHER;
    public static final int DEFAULT_CLIP_SIZE = 4;
    public static final int DEFAULT_RELOAD_TIME = 2000;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 1;
    public static final Projectile DEFAULT_PROJECTILE = new CrystalBullet();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.MAGIC_ESSENCE;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 800;
    public static final int DEFAULT_ACCURACY = 0;

    public CrystalLauncher() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }

}
