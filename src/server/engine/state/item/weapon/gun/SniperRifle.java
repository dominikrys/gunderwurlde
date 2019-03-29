package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
import server.engine.state.map.tile.Tile;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.lists.ItemList;

/**
 * 
 * @author Tomas
 *
 */
public class SniperRifle extends ProjectileGun {

    public static final ItemList NAME = ItemList.SNIPER_RIFLE;
    public static final int DEFAULT_CLIP_SIZE = 6;
    public static final int DEFAULT_RELOAD_TIME = 4000;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 1;
    public static final Projectile DEFAULT_PROJECTILE = new SmallBullet(Tile.TILE_SIZE * 22, 5, EntityList.BASIC_BULLET.getSize() / 2, 0);
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.HEAVY_AMMO;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 3000; //2bps
    public static final int DEFAULT_ACCURACY = 1;

    public SniperRifle() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }

}
