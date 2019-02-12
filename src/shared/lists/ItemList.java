package shared.lists;

public enum ItemList {
    PISTOL(EntityList.PISTOL),
    BASIC_AMMO(EntityList.AMMO_CLIP), 
    SHOTGUN(EntityList.PISTOL);

    private String spritePath;
    private EntityList entityListName;

    ItemList(EntityList entityListName) {
        this.entityListName = entityListName;
        this.spritePath = entityListName.getPath();
    }

    public AmmoList toAmmoList() { //only works if you know the item is definitely ammo TODO make this less error prone
        return AmmoList.valueOf(this.toString());
    }

    public String getSpritePath() {
        return spritePath;
    }

    public EntityList getEntityList() {
        return entityListName;
    }
}

