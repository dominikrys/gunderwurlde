package server.engine.state.laser;

import java.util.LinkedHashSet;

import server.engine.state.map.tile.Tile;
import shared.Line;
import shared.Location;
import shared.Pose;
import shared.lists.TileState;

public class Laser extends Line {
    protected double size;
    protected int damage;

    public Laser(Location start, Location end, double size, int damage) {
        super(start, end);
        this.size = size;
    }

    public Laser(Line line, double size, int damage) {
        super(line);
        this.size = size;
        this.damage = damage;
    }

    public static Laser DrawLaser(Pose start, Tile[][] tileMap, double size, int damage) {
        int chunkLength = 100;
        boolean endPointFound = false;
        Location endPoint = start;

        while (!endPointFound) {
            Laser testLaser = new Laser(new Line(endPoint, start.getDirection(), chunkLength), size / 2, 0);
            endPoint = testLaser.getEnd();
            LinkedHashSet<int[]> tilesOn = testLaser.getTilesOn();

            for (int[] tileOn : tilesOn) {
                Tile tileBeingChecked = tileMap[tileOn[0]][tileOn[1]];
                if (tileBeingChecked.getState() == TileState.SOLID) {
                    // TODO find precise collision point and set as end
                    endPointFound = true;
                    break;
                }
            }
        }

        return new Laser(start, endPoint, size, damage);
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public LinkedHashSet<int[]> getTilesOn() {
        double m = length / Math.abs(end.getX() - start.getX());
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
        double offSet = (Tile.TILE_SIZE / 2) + size;
        for (int t_x = startX; t_x != endX; t_x += cX) {
            for (int t_y = startY; t_y != endY; t_y += cY) {
                Location tileLoc = Tile.tileToLocation(t_x, t_y);
                minX = tileLoc.getX() - offSet;
                maxX = tileLoc.getX() + offSet;
                minY = tileLoc.getY() - offSet;
                maxY = tileLoc.getY() + offSet;
                double y1 = (minX * m) + c;
                double y2 = (maxX * m) + c;
                double x1 = (minY - c) / m;
                double x2 = (maxY - c) / m;

                if ((y1 <= maxY && y1 >= minY) || (y2 <= maxY && y2 >= minY) || (x1 <= maxX && x1 >= minX) || (x2 <= maxX && x2 >= minX))
                    tilesOn.add(new int[] { t_x, t_y });
            }
        }

        return tilesOn;
    }

}
