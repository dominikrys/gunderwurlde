package data.entity.item.weapon;

import data.entity.item.ItemList;
import data.entity.projectile.ProjectileType;

public class Pistol extends Gun {
    public static final ItemList ITEM_ID = ItemList.PISTOL;
    public static final int DEFAULT_AMMO_AMOUNT = Integer.MAX_VALUE;
    public static final int DEFAULT_CLIP_SIZE = 12;
    public static final int DEFAULT_RELOAD_TIME = 5;
    public static final int DEFAULT_AMMO_PER_SHOT = 1;
    public static final ProjectileType DEFAULT_AMMO_TYPE = ProjectileType.SMALLBULLET;


    public Pistol() {
        super(ITEM_ID, DEFAULT_AMMO_AMOUNT, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT, DEFAULT_AMMO_TYPE);
    }

}
