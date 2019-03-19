package shared.lists;

/**
 * TileType enum. Contains all tile types in the game
 */
public enum TileType {
    GRASS(EntityList.GRASS_TILE),
    WOOD(EntityList.WOOD_TILE),
    DOOR(EntityList.WOOD_TILE);

    /**
     * Corresponding name of this tile in the entity list
     */
    EntityList entityListName;

    /**
     * Constructor
     *
     * @param entityListName EntityList equivalent
     */
    TileType(EntityList entityListName) {
        this.entityListName = entityListName;
    }

    /**
     * Get EntityList equivalent
     *
     * @return EntityList equivalent
     */
    public EntityList getEntityListName() {
        return entityListName;
    }
}
