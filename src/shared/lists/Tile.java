package shared.lists;

/**
 * Tile enum. Contains all tile types in the game
 */
public enum Tile {
    // Meadow Tiles
    GRASS(EntityList.GRASS_TILE, TileState.PASSABLE, 0.5),
    WOOD(EntityList.WOOD_TILE, TileState.SOLID, 0.7),
    DOOR(EntityList.DOOR_TILE, TileState.SOLID, 0.9);

    //Ruins tiles
    /*
    RUINS_FLOOR(EntityList.RUINS_FLOOR),
    RUINS_PATTERN(EntityList.RUINS_PATTERN),
    RUINS_WALL(EntityList.RUINS_WALL),
    RUINS_PILLAR(EntityList.RUINS_PILLAR), //may not be needed
    RUINS_WATER(EntityList.RUINS_WATER),
    RUINS_DOOR(EntityList.RUINS_DOOR);
*/

    /**
     * Corresponding name of this tile in the entity list
     */
    EntityList entityListName;

    /**
     * State of the tile
     */
    TileState tileState;

    /**
     * Friction coefficient of tile
     */
    double friction;

    /**
     * Bounce coefficient of tile
     */
    double bounceCoefficient;

    /**
     * Constructor
     *
     * @param entityListName    EntityList equivalent
     * @param tilestate         State of the tile
     * @param friction          Friction coefficient of tile
     * @param bounceCoefficient Bounce coefficient of tile
     */
    Tile(EntityList entityListName, TileState tilestate, double friction, double bounceCoefficient) {
        this.entityListName = entityListName;
        this.tileState = tilestate;
        this.friction = friction;
        this.bounceCoefficient = bounceCoefficient;
    }

    /**
     * Constructor without bounce coefficient
     *
     * @param entityListName    EntityList equivalent
     * @param tilestate         State of the tile
     * @param friction          Friction coefficient of tile
     */
    Tile(EntityList entityListName, TileState tilestate, double friction) {
        this(entityListName, tilestate, friction, friction);
    }

    /**
     * Get EntityList equivalent
     *
     * @return EntityList equivalent
     */
    public EntityList getEntityListName() {
        return entityListName;
    }

    /**
     * Get tile state of this tile
     *
     * @return Tile state
     */
    public TileState getTileState() {
        return tileState;
    }

    /**
     * Get friciton coefficient of this stile
     *
     * @return Friction coefficient of tile
     */
    public double getFriction() {
        return friction;
    }

    /**
     * Get boune coefficient of tile
     *
     * @return Bounce coefficient
     */
    public double getBounceCoefficient() {
        return bounceCoefficient;
    }
}
