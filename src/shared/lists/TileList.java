package shared.lists;

import java.io.Serializable;

/**
 * TileList enum. Contains all tile types in the game
 */
public enum TileList implements Serializable{
    // Meadow Tiles
    GRASS(EntityList.GRASS_TILE, TileState.PASSABLE, 0.5),
    WOOD(EntityList.WOOD_TILE, TileState.SOLID, 0.7),
    DOOR(EntityList.DOOR_TILE, TileState.SOLID, 0.9),
    RUINS_FLOOR(EntityList.RUINS_FLOOR, TileState.PASSABLE, 0.5),
    RUINS_DOOR(EntityList.RUINS_DOOR, TileState.SOLID, 0.5),
    RUINS_DOOR_OPEN(EntityList.RUINS_DOOR_OPEN, TileState.PASSABLE, 0.5),
    RUINS_ORNATE_BLOCK(EntityList.RUINS_ORNATE_BLOCK, TileState.SOLID, 0.5),
    RUINS_SOLID_BLOCK_DARK(EntityList.RUINS_SOLID_BLOCK_DARK, TileState.SOLID, 0.5),
    RUINS_SOLID_BLOCK_LIGHT(EntityList.RUINS_SOLID_BLOCK_LIGHT, TileState.SOLID, 0.5),
    RUINS_WALL_DARK(EntityList.RUINS_WALL_DARK, TileState.SOLID, 0.5),
    RUINS_WALL_MID(EntityList.RUINS_WALL_MID, TileState.SOLID, 0.5),
    RUINS_WALL_LIGHT(EntityList.RUINS_WALL_LIGHT, TileState.SOLID, 0.5),
    SAND(EntityList.SAND, TileState.PASSABLE, 0.9),
    RED_GROUND(EntityList.RED_GROUND, TileState.PASSABLE, 0.5),
    MARBLE_FLOOR(EntityList.MARBLE_FLOOR, TileState.PASSABLE, 0.5),
    DIRT(EntityList.DIRT, TileState.PASSABLE, 0.5),
    WATER(EntityList.WATER, TileState.PASSABLE, 0.8);
	
	private static final long serialVersionUID = 1L;

    /**
     * Corresponding name of this tileList in the entity list
     */
    EntityList entityListName;

    /**
     * State of the tileList
     */
    TileState tileState;

    /**
     * Friction coefficient of tileList
     */
    double friction;

    /**
     * Bounce coefficient of tileList
     */
    double bounceCoefficient;

    /**
     * Constructor
     *
     * @param entityListName    EntityList equivalent
     * @param tilestate         State of the tileList
     * @param friction          Friction coefficient of tileList
     * @param bounceCoefficient Bounce coefficient of tileList
     */
    TileList(EntityList entityListName, TileState tilestate, double friction, double bounceCoefficient) {
        this.entityListName = entityListName;
        this.tileState = tilestate;
        this.friction = friction;
        this.bounceCoefficient = bounceCoefficient;
    }

    /**
     * Constructor without bounce coefficient
     *
     * @param entityListName EntityList equivalent
     * @param tilestate      State of the tileList
     * @param friction       Friction coefficient of tileList
     */
    TileList(EntityList entityListName, TileState tilestate, double friction) {
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
     * Get tileList state of this tileList
     *
     * @return TileList state
     */
    public TileState getTileState() {
        return tileState;
    }

    /**
     * Get friciton coefficient of this stile
     *
     * @return Friction coefficient of tileList
     */
    public double getFriction() {
        return friction;
    }

    /**
     * Get boune coefficient of tileList
     *
     * @return Bounce coefficient
     */
    public double getBounceCoefficient() {
        return bounceCoefficient;
    }
}
