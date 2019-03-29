package server.engine.state.item.weapon.gun;

import server.engine.state.laser.Laser;
import shared.Location;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

/**
 * 
 * @author Richard
 *
 */
public class HeavyLaserCannon extends LaserGun {
    public static final ItemList NAME = ItemList.LASER_CANNON;
    public static final int DEFAULT_CLIP_SIZE = 120;
    public static final int DEFAULT_RELOAD_TIME = 1500;
    public static final int DEFAULT_AMMO_PER_SHOT = 20;
    public static final int DEFAULT_LASERS_PER_SHOT = 1;
    public static final Laser DEFAULT_LASER = new Laser(new Location(0, 0), new Location(0, 0), 6, 2, 600, Team.NONE);
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.ENERGY;
    public static final int DEFAULT_SPREAD = 0;
    public static final int DEFAULT_COOL_DOWN = 1000; // 1bps
    public static final int DEFAULT_ACCURACY = 0;

    public HeavyLaserCannon() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN,
                DEFAULT_LASERS_PER_SHOT, DEFAULT_ACCURACY, DEFAULT_LASER);
    }

}
