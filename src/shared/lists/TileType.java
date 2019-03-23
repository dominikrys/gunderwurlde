package shared.lists;

/**
 * TileType enum. Contains all tile types in the game
 */
public enum TileType {
    // Meadow Tiles
    GRASS(EntityList.GRASS_TILE),
    WOOD(EntityList.WOOD_TILE),
    DOOR(EntityList.WOOD_TILE),
    
    //Ruins tiles
    RUINS_FLOOR(EntityList.RUINS_FLOOR),
    RUINS_PATTERN(EntityList.RUINS_PATTERN),
    RUINS_WALL(EntityList.RUINS_WALL),
    RUINS_PILLAR(EntityList.RUINS_PILLAR), //may not be needed
    RUINS_WATER(EntityList.RUINS_WATER),
    RUINS_DOOR(EntityList.RUINS_DOOR);
    

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
