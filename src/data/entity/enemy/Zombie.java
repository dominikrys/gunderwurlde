package data.entity.enemy;

import java.util.LinkedHashSet;

import data.HasContactDamage;
import data.Pose;
import data.entity.item.weapon.gun.Ammo;
import data.entity.item.weapon.gun.AmmoList;
import data.map.tile.Tile;

public class Zombie extends Enemy implements HasContactDamage {
    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_MOVESPEED = 6;
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();
    
    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO),5000,3,2));
    }

    protected int contactDamage = 1;

    public Zombie(Pose pose) {
        this(DEFAULT_HEALTH, DEFAULT_MOVESPEED, pose, DEFAULT_SIZE, DEFAULT_DROPS);
    }

    Zombie(int maxHealth, int moveSpeed, Pose pose, int size, LinkedHashSet<Drop> drops) {
        super(maxHealth, moveSpeed, pose, EnemyList.ZOMBIE, size, drops);
        this.pathToGraphic = "file:assets/img/mobs/zombie.png";
    }

    @Override
    public int getContactDamage() {
        return contactDamage;
    }

    @Override
    public void setContactDamage(int contactDamage) {
        this.contactDamage = contactDamage;
    }

}
