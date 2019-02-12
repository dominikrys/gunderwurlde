package server.engine.state.item.weapon.gun;

import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.lists.ItemList;

public class Shotgun extends Gun {
    public static final ItemList NAME = ItemList.SHOTGUN;
    public static final int DEFAULT_CLIP_SIZE = 20;
    public static final int DEFAULT_RELOAD_TIME = 3500;
    public static final int DEFAULT_AMMO_PER_SHOT = 5;
    public static final EntityList DEFAULT_PROJECTILE_TYPE = EntityList.BASIC_BULLET;
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.SHOTGUN_ROUND;
    public static final int DEFAULT_SPREAD = 20;
    public static final int DEFAULT_COOL_DOWN = 600;

    public Shotgun() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE_TYPE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN);
    }

}
