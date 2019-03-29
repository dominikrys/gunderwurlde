package server.engine.state.entity;

import java.util.LinkedHashSet;

import server.engine.state.laser.Laser;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

/**
 * Class for all Entities.
 * 
 * @author Richard
 *
 */
public abstract class Entity {
    /**
     * Maximum size an entity can take.
     */
    public static final int MAX_SIZE = (3 * Tile.TILE_SIZE);
    /**
     * Pose of the entity (Location and direction)
     */
    protected Pose pose;
    /**
     * The name, size and appearance to use for the entity.
     */
    protected EntityList entityListName;
    /**
     * The status of the Entity.
     */
    protected EntityStatus status;
    /**
     * Entity size as it's radius.
     */
    protected int size;
    /**
     * Boolean to indicate if the entity is cloaked
     */
    protected boolean cloaked; // "invisible"
    /**
     * The zoneID corresponding to the Zone that spawned the Entity.
     */
    protected int zoneID;

    /**
     * Super Constructor that creates an Entity for the given pose, size &
     * entityListName.
     * 
     * @param pose
     * @param size
     * @param entityListName
     */
    protected Entity(Pose pose, int size, EntityList entityListName) {
        this.pose = pose;
        this.size = size;
        this.entityListName = entityListName;
        this.cloaked = false;
        this.status = EntityStatus.NONE;
        this.zoneID = -1;
    }
    
    /**
     * Super Constructor to create a template Entity for Spawning.
     * 
     * @param size
     * @param entityListName
     */
    protected Entity(int size, EntityList entityListName) {
        this (new Pose(), size, entityListName);
    }

    /**
     * Super Constructor to create an Entity with default direction.
     * 
     * @param location
     * @param size
     * @param entityListName
     */
    protected Entity(Location location, int size, EntityList entityListName) {
        this(new Pose(location), size, entityListName);
    }

    /**
     * Abstract method to make a copy of the Entity.
     * 
     * @return A copy of the Entity.
     */
    public abstract Entity makeCopy();

    // getters and setters.

    /**
     * @return zoneID
     */
    public int getZoneID() {
        return zoneID;
    }

    /**
     * @param zoneID
     */
    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    /**
     * @return true if Cloaked
     */
    public boolean isCloaked() {
        return cloaked;
    }

    /**
     * @param cloaked
     */
    public void setCloaked(boolean cloaked) {
        this.cloaked = cloaked;
    }

    /**
     * @return Satus of the Entity
     */
    public EntityStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    /**
     * @return pose of the Entity
     */
    public Pose getPose() {
        return pose;
    }

    /**
     * @return entityList value for that entity
     */
    public EntityList getEntityListName() {
        return entityListName;
    }

    /**
     * @param pose
     */
    public void setPose(Pose pose) {
        this.pose = pose;
    }

    /**
     * @return Location for the entity
     */
    public Location getLocation() {
        return pose;
    }

    /**
     * @param location
     */
    public void setLocation(Location location) {
        this.pose = new Pose(location, this.pose.getDirection());
    }

    /**
     * @return Size of the Entity (radius)
     */
    public int getSize() {
        return size;
    }

    /**
     * Changes entity size with given amount.
     * 
     * @param amount
     */
    public void changeSize(int amount) {
        if (amount <= -this.size) {
            this.size = 1;
        } else if ((amount + this.size) > MAX_SIZE) {
            this.size = MAX_SIZE;
        } else {
            this.size += amount;
        }
    }

    /**
     * Sets the Entity size to the given value.
     * 
     * @param size
     */
    public void setSize(int size) {
        if (size <= 0) {
            this.size = 1;
        } else if (size > MAX_SIZE) {
            this.size = MAX_SIZE;
        } else {
            this.size = size;
        }
    }
    
    /**
     * Gets the Tiles the Entity is present on.
     * 
     * @return Collection of the TileCords for the Tiles that the Entity is on.
     */
    public LinkedHashSet<int[]> getTilesOn() {
        Location loc = this.getLocation();
        int radius = size;
        double x = loc.getX();
        double y = loc.getY();

        int[] maxLoc = Tile.locationToTile(new Location(x + radius, y + radius));
        int[] minLoc = Tile.locationToTile(new Location(x - radius, y - radius));

        LinkedHashSet<int[]> tilesOn = new LinkedHashSet<>();

        for (int t_x = minLoc[0]; t_x <= maxLoc[0]; t_x++) {
            for (int t_y = minLoc[1]; t_y <= maxLoc[1]; t_y++) {
                tilesOn.add(new int[] { t_x, t_y });
            }
        }

        return tilesOn;
    }

    /**
     * Checks if the Entity has collided with the given entity e2.
     * 
     * @param e2
     *            Other entity to check with.
     * @return true if they've collided.
     */
    public boolean haveCollided(Entity e2) {
        Location e1_loc = this.getLocation();
        int e1_radius = this.size;
        double e1_x = e1_loc.getX();
        double e1_y = e1_loc.getY();

        Location e2_loc = e2.getLocation();
        int e2_radius = e2.getSize();
        double e2_x = e2_loc.getX();
        double e2_y = e2_loc.getY();

        double dist_between_squared = Math.pow(Math.abs(e1_x - e2_x), 2) + Math.pow(Math.abs(e1_y - e2_y), 2);

        return (dist_between_squared <= Math.pow(e1_radius + e2_radius, 2));
    }

    /**
     * Checks if the Entity has collided with the given laser l.
     * 
     * @param l
     *            Laser to check with.
     * @return true if they've collided.
     */
    public boolean haveCollided(Laser l) {
        Location eLoc = this.getLocation();
        int eRadius = this.size;
        double eX = eLoc.getX();
        double eY = eLoc.getY();

        Location start = l.getStart();
        Location end = l.getEnd();
        double size = l.getSize();
        double xDist = end.getX() - start.getX();
        double yDist = end.getY() - start.getY();

        double dot = ((eX - start.getX()) * xDist) + ((eY - start.getY()) * yDist);
        dot /= Math.pow(l.getLength(), 2);

        yDist = start.getY() + (dot * yDist) - eY;
        xDist = start.getX() + (dot * xDist) - eX;

        double laserDist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2)) - size;
        return (eRadius >= laserDist);
    }

}
