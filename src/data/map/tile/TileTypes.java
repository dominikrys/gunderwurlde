package data.map.tile;

import data.entity.EntityList;

public enum TileTypes {
    GRASS(EntityList.GRASS_TILE),
    WOOD(EntityList.WOOD_TILE);

    EntityList entityListName;

    TileTypes(EntityList entityListName) {
        this.entityListName = entityListName;
    }

    public EntityList getEntityListName() {
        return entityListName;
    }
}
