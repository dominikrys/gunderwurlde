package data.entity.enemy;

import java.util.LinkedHashSet;

import data.HasContactDamage;
import data.entity.EntityList;
import data.item.weapon.gun.Ammo;
import data.item.weapon.gun.AmmoList;
import data.map.tile.Tile;

public class Zombie extends Enemy implements HasContactDamage {
    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_MOVESPEED = 6;
    public static final int DEFAULT_SIZE = Tile.TILE_SIZE;
    public static final int DEFAULT_SCORE_ON_KILL = 10;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 5000, 3, 2));
    }

    protected int contactDamage = 1;

    public Zombie() {
        this(DEFAULT_HEALTH, DEFAULT_MOVESPEED, DEFAULT_SIZE, DEFAULT_DROPS, DEFAULT_SCORE_ON_KILL);
    }

    Zombie(int maxHealth, int moveSpeed, int size, LinkedHashSet<Drop> drops, int scoreOnKill) {
        super(maxHealth, moveSpeed, EntityList.ZOMBIE, size, drops, scoreOnKill);
    }

    @Override
    public int getContactDamage() {
        return contactDamage;
    }

    @Override
    public void setContactDamage(int contactDamage) {
        this.contactDamage = contactDamage;
    }

    @Override
    public Enemy makeCopy() {
        return new Zombie(this.maxHealth, this.moveSpeed, this.size, this.drops, this.scoreOnKill);
    }

}
