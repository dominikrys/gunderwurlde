package data.entity.item.weapon.gun;

import data.entity.projectile.ProjectileList;

public class Pistol extends Gun {
    public static final GunList NAME = GunList.PISTOL;
    public static final int DEFAULT_CLIP_SIZE = 12;
    public static final int DEFAULT_RELOAD_TIME = 3500;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final ProjectileList DEFAULT_PROJECTILE_TYPE = ProjectileList.SMALLBULLET;
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.BASIC_AMMO;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 500; //2bps

    public Pistol() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE_TYPE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN);
    }

}
