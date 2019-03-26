package server.engine.state.laser;

import java.util.HashMap;
import java.util.LinkedHashSet;

import server.engine.state.map.tile.Tile;
import shared.Line;
import shared.Location;
import shared.Pose;
import shared.lists.Team;
import shared.lists.TileState;

public class Laser extends Line {
    public static final long DAMAGE_TIMEOUT = 800;

    protected double originalSize;
    protected double size;
    protected int damage;
    protected long duration;
    protected long creationTime;
    protected Team team;
    protected HashMap<Integer, Long> lastEntityDamageTime;

    public Laser(Location start, Location end, double size, int damage, long duration, Team team) {
        super(start, end);
        this.size = size;
        this.originalSize = size;
        this.damage = damage;
        this.duration = duration;
        this.team = team;
        this.creationTime = System.currentTimeMillis();
        this.lastEntityDamageTime = new HashMap<>();
    }

    public Laser(Line line, double size, int damage, long duration, Team team) {
        super(line);
        this.size = size;
        this.originalSize = size;
        this.damage = damage;
        this.duration = duration;
        this.team = team;
        this.creationTime = System.currentTimeMillis();
        this.lastEntityDamageTime = new HashMap<>();
    }

    public static Laser DrawLaser(Pose start, Tile[][] tileMap, Laser templateLaser, Team team) {
        int chunkLength = 200;
        boolean endPointFound = false;
        double offSet = (Tile.TILE_SIZE / 2) + (templateLaser.size / 2);
        Laser testLaser = new Laser(new Line(start, start.getDirection(), chunkLength), templateLaser.size / 2, 0, 0, Team.NONE);
        Location endPoint = testLaser.getEnd();
        double m = (testLaser.getEnd().getY() - testLaser.getStart().getY()) / (testLaser.getEnd().getX() - testLaser.getStart().getX());
        double c = testLaser.getStart().getY() - (m * testLaser.getStart().getX());

        while (!endPointFound) {
            LinkedHashSet<int[]> tilesOn = testLaser.getTilesOn();

            for (int[] tileOn : tilesOn) {
                Tile tileBeingChecked = tileMap[tileOn[0]][tileOn[1]];
                if (tileBeingChecked.getState() == TileState.SOLID) {
                    Location tileLoc = Tile.tileToLocation(tileOn[0], tileOn[1]);
                    double minX = tileLoc.getX() - offSet;
                    double maxX = tileLoc.getX() + offSet;
                    double minY = tileLoc.getY() - offSet;
                    double maxY = tileLoc.getY() + offSet;
                    double y1 = (minX * m) + c;
                    double y2 = (maxX * m) + c;
                    double x1 = (minY - c) / m;
                    double x2 = (maxY - c) / m;

                    if (y1 <= maxY && y1 >= minY) {
                        endPoint = new Location(minX, y1);
                    } else if (y2 <= maxY && y2 >= minY) {
                        endPoint = new Location(maxX, y2);
                    } else if (x1 <= maxX && x1 >= minX) {
                        endPoint = new Location(x1, minY);
                    } else {
                        endPoint = new Location(x2, maxY);
                    }

                    endPoint = tileLoc;

                    endPointFound = true;
                    break;
                }
            }

            if (!endPointFound) {
                testLaser = new Laser(new Line(endPoint, start.getDirection(), chunkLength), templateLaser.size / 2, 0, 0, Team.NONE);
                endPoint = testLaser.getEnd();
            }
        }

        System.out.println("laser made.");
        System.out.println("start: " + start.getX() + " " + start.getY() + " " + start.getDirection());
        System.out.println("end: " + endPoint.getX() + " " + endPoint.getY());

        return new Laser(start, endPoint, templateLaser.size, templateLaser.damage, templateLaser.duration, team);
    }

    public boolean canDamage(Integer ID) {
        if (!lastEntityDamageTime.containsKey(ID))
            return true;
        else
            return (lastEntityDamageTime.get(ID) + DAMAGE_TIMEOUT <= System.currentTimeMillis());
    }

    public boolean isRemoved() {
        double portionLeft = (System.currentTimeMillis() - creationTime) / duration;
        if (portionLeft >= 1) {
            return true;
        } else {
            size = originalSize * (1 - portionLeft);
            return false;
        }
    }

    public Team getTeam() {
        return team;
    }

    public double getSize() {
        return size;
    }

    public void setOriginalSize(double newSize) {
        this.originalSize = newSize;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void Damaged(Integer entityID) {
        this.lastEntityDamageTime.put(entityID, System.currentTimeMillis());
    }

    public LinkedHashSet<int[]> getTilesOn() {
        double m = (end.getY() - start.getY()) / (end.getX() - start.getX());
        double c = start.getY() - (m * start.getX());
        double maxX = start.getX();
        double maxY = start.getY();
        double minX = end.getX();
        double minY = end.getY();
        boolean startMaxX = true;
        boolean startMaxY = true;

        if (minX > maxX) {
            maxX = minX;
            minX = start.getX();
            startMaxX = false;
        }

        if (minY > maxY) {
            maxY = minY;
            minY = start.getY();
            startMaxY = false;
        }

        maxX += size;
        maxY += size;
        minX -= size;
        minY -= size;

        int[] max_loc = Tile.locationToTile(new Location(maxX, maxY));
        int[] min_loc = Tile.locationToTile(new Location(minX, minY));

        int startX;
        int endX;
        int cX;
        int startY;
        int endY;
        int cY;

        if (startMaxX) {
            startX = max_loc[0];
            endX = min_loc[0] - 1;
            cX = -1;
        } else {
            startX = min_loc[0];
            endX = max_loc[0] + 1;
            cX = 1;
        }

        if (startMaxY) {
            startY = max_loc[1];
            endY = min_loc[1] - 1;
            cY = -1;
        } else {
            startY = min_loc[1];
            endY = max_loc[1] + 1;
            cY = 1;
        }

        LinkedHashSet<int[]> tilesOn = new LinkedHashSet<>();

        double offset = Tile.TILE_SIZE / 2;
        for (int t_x = startX; t_x != endX; t_x += cX) {
            for (int t_y = startY; t_y != endY; t_y += cY) {
                Location tileLoc = Tile.tileToLocation(t_x, t_y);
                minX = tileLoc.getX() - offset;
                maxX = tileLoc.getX() + offset;
                minY = tileLoc.getY() - offset;
                maxY = tileLoc.getY() + offset;
                double y1 = (minX * m) + c;
                double y2 = (maxX * m) + c;
                double x1 = (minY - c) / m;
                double x2 = (maxY - c) / m;
                minX -= size;
                maxX += size;
                minY -= size;
                maxY += size;

                if ((y1 <= maxY && y1 >= minY) || (y2 <= maxY && y2 >= minY) || (x1 <= maxX && x1 >= minX) || (x2 <= maxX && x2 >= minX))
                    tilesOn.add(new int[] { t_x, t_y });
            }
        }

        return tilesOn;
    }

}
