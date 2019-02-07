package data.entity.enemy;

import data.HasContactDamage;
import data.entity.EntityList;
import data.item.weapon.gun.Ammo;
import data.item.weapon.gun.AmmoList;

import java.util.LinkedHashSet;

public class Zombie extends Enemy implements HasContactDamage {
    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_MOVESPEED = 6;
    public static final LinkedHashSet<Drop> DEFAULT_DROPS = new LinkedHashSet<>();

    static {
        DEFAULT_DROPS.add(new Drop(new Ammo(AmmoList.BASIC_AMMO), 5000, 3, 2));
    }

    protected int contactDamage = 1;

    public Zombie() {
        this(DEFAULT_HEALTH, DEFAULT_MOVESPEED, 1, DEFAULT_DROPS);
    }

    Zombie(int maxHealth, int moveSpeed, int size, LinkedHashSet<Drop> drops) {
        super(maxHealth, moveSpeed, EntityList.ZOMBIE, size, drops);
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
        return new Zombie(this.maxHealth, this.moveSpeed, this.sizeScaleFactor, this.drops);
    }

}
