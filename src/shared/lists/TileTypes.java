package shared.lists;

public enum TileTypes {
    GRASS(EntityList.GRASS_TILE),
    WOOD(EntityList.WOOD_TILE),
    DOOR(EntityList.WOOD_TILE);

    EntityList entityListName;

    TileTypes(EntityList entityListName) {
        this.entityListName = entityListName;
    }

    public EntityList getEntityListName() {
        return entityListName;
    }
}
