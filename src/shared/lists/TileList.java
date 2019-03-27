package shared.lists;

import java.io.Serializable;

/**
 * TileList enum. Contains all tile types in the game
 */
public enum TileList implements Serializable{
    // Meadow Tiles
    GRASS(EntityList.GRASS_TILE, TileState.PASSABLE, 0.5, 200),
    WOOD(EntityList.WOOD_TILE, TileState.SOLID, 0.7),
    DOOR(EntityList.DOOR_TILE, TileState.SOLID, 0.9),
    RUINS_FLOOR(EntityList.RUINS_FLOOR, TileState.PASSABLE, 0.5, 200),
    RUINS_ORNATE_BLOCK(EntityList.RUINS_ORNATE_BLOCK, TileState.SOLID, 0.5),
    RUINS_SOLID_BLOCK_DARK(EntityList.RUINS_SOLID_BLOCK_DARK, TileState.SOLID, 0.5),
    RUINS_SOLID_BLOCK_LIGHT(EntityList.RUINS_SOLID_BLOCK_LIGHT, TileState.SOLID, 0.5),
    RUINS_WALL_DARK(EntityList.RUINS_WALL_DARK, TileState.SOLID, 0.5),
    RUINS_WALL_MID(EntityList.RUINS_WALL_MID, TileState.SOLID, 0.5),
    RUINS_WALL_LIGHT(EntityList.RUINS_WALL_LIGHT, TileState.SOLID, 0.5),
    SAND(EntityList.SAND, TileState.PASSABLE, 0.502, 1000),
    RED_GROUND(EntityList.RED_GROUND, TileState.PASSABLE, 0.5, 200),
    MARBLE_FLOOR(EntityList.MARBLE_FLOOR, TileState.PASSABLE, 0.49, 200),
    DIRT(EntityList.DIRT, TileState.PASSABLE, 0.5, 200),
    WATER(EntityList.WATER, TileState.PASSABLE, 0.49, 6000),
    VOID(EntityList.VOID, TileState.SOLID, 0),
    CARPET(EntityList.CARPET, TileState.PASSABLE, 0.47, 200),
    WOOD_DARK(EntityList.WOOD_DARK, TileState.SOLID, 0.5, 200),
    FOOTPATH(EntityList.FOOTPATH, TileState.PASSABLE, 0.47, 200);
	
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

    /*
     * Density of the tileList
     */
    double density;

    /**
     * Constructor
     *
     * @param entityListName    EntityList equivalent
     * @param tilestate         State of the tileList
     * @param friction          Friction coefficient of tileList
     * @param bounceCoefficient Bounce coefficient of tileList
     */
    TileList(EntityList entityListName, TileState tilestate, double bounceCoefficient) {
        this.entityListName = entityListName;
        this.tileState = tilestate;
        this.bounceCoefficient = bounceCoefficient;
        this.friction = 0;
        this.density = 0;
    }

    /**
     * Constructor without bounce coefficient
     *
     * @param entityListName EntityList equivalent
     * @param tilestate      State of the tileList
     * @param friction       Friction coefficient of tileList
     */
    TileList(EntityList entityListName, TileState tilestate, double friction, double density) {
        this.entityListName = entityListName;
        this.tileState = tilestate;
        this.bounceCoefficient = 0;
        this.friction = friction;
        this.density = density;
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
     * Get friciton coefficient of this tile
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

    /*
     * Get density of this tile
     * 
     * @return Density
     */
    public double getDensity() {
        return density;
    }
}
