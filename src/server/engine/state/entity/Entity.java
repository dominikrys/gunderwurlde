package server.engine.state.entity;

import java.util.LinkedHashSet;

import server.engine.state.laser.Laser;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

// Class for renderable entities
public abstract class Entity {
    public static final int MAX_SIZE = (3 * Tile.TILE_SIZE);
    
    protected Pose pose;
    protected EntityList entityListName;
    protected EntityStatus status;
    protected int size;
    protected boolean cloaked; // "invisible"
    protected int zoneID;

    protected Entity(Pose pose, int size, EntityList entityListName) {
        this.pose = pose;
        this.size = size;
        this.entityListName = entityListName;
        this.cloaked = false;
        this.status = EntityStatus.NONE;
        this.zoneID = -1;
    }
    
    protected Entity(int size, EntityList entityListName) {
        this (new Pose(), size, entityListName);
    }

    protected Entity(Location location, int size, EntityList entityListName) {
        this(new Pose(location), size, entityListName);
    }

    public abstract Entity makeCopy();

    public int getZoneID() {
        return zoneID;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public boolean isCloaked() {
        return cloaked;
    }

    public void setCloaked(boolean cloaked) {
        this.cloaked = cloaked;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public Pose getPose() {
        return pose;
    }

    public EntityList getEntityListName() {
        return entityListName;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public Location getLocation() {
        return pose;
    }

    public void setLocation(Location location) {
        this.pose = new Pose(location, this.pose.getDirection());
    }

    public int getSize() {
        return size;
    }

    public void changeSize(int amount) {
        if (amount <= -this.size) {
            this.size = 1;
        } else if ((amount + this.size) > MAX_SIZE) {
            this.size = MAX_SIZE;
        } else {
            this.size += amount;
        }
    }

    public void setSize(int size) {
        if (size <= 0) {
            this.size = 1;
        } else if (size > MAX_SIZE) {
            this.size = MAX_SIZE;
        } else {
            this.size = size;
        }
    }
    
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

    public boolean haveCollided(Laser l) {
        Location eLoc = this.getLocation();
        int eRadius = this.size;
        double eX = eLoc.getX();
        double eY = eLoc.getY();

        Location start = l.getStart();
        Location end = l.getEnd();
        double size = l.getSize();
        double m = (end.getY() - start.getY()) / (end.getX() - start.getX());
        double c = start.getY() - (m * start.getX());

        double yDist = (((eX * m) + c) - eY) / 2;
        double xDist = (((eY - c) / m) - eX) / 2;

        double laserDist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2)) - size;
        System.out.println("laserDist: " + laserDist);
        System.out.println("radius: " + eRadius);
        return (eRadius >= laserDist);
    }

}
