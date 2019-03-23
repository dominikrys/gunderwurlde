package shared.lists;

/**
 * ItemList class. Contains all items used in game
 *
 * @author Dominik Rys
 * @author Richard Miller
 */
public enum ItemList {
    // Weapons
    PISTOL(EntityList.PISTOL, null),
    SHOTGUN(EntityList.SHOTGUN, null),
    SMG(EntityList.SMG, null),
    SNIPER_RIFLE(EntityList.SNIPER_RIFLE, null),
    PLASMA_PISTOL(EntityList.PLASMA_PISTOL, null),
    MACHINE_GUN(EntityList.MACHINE_GUN, null),

    // Ammo
    BASIC_AMMO(EntityList.AMMO_CLIP, AmmoList.BASIC_AMMO),
    SHOTGUN_ROUND(EntityList.AMMO_CLIP, AmmoList.SHOTGUN_ROUND),
    ENERGY(EntityList.ENERGY, AmmoList.ENERGY),
    MAGIC_ESSENCE(EntityList.MAGIC_ESSENCE, AmmoList.MAGIC_ESSENCE),
    HEAVY_AMMO(EntityList.HEAVY_AMMO, AmmoList.HEAVY_AMMO),
    ROCKET_AMMO(EntityList.ROCKET_AMMO, AmmoList.ROCKET_AMMO);

    private String spritePath;
    private EntityList entityListName;
    private AmmoList ammoListEquivalent;

    ItemList(EntityList entityListName, AmmoList ammoListEquivalent) {
        this.entityListName = entityListName;
        this.spritePath = entityListName.getPath();
        this.ammoListEquivalent = ammoListEquivalent;
    }

    public AmmoList toAmmoList() { //only works if you know the item is definitely ammo
        return ammoListEquivalent;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public EntityList getEntityList() {
        return entityListName;
    }
}

