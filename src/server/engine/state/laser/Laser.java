package server.engine.state.laser;

import java.util.LinkedHashSet;

import server.engine.state.map.tile.Tile;
import shared.Line;
import shared.Location;
import shared.Pose;

public class Laser extends Line {
    protected double size;
    protected int damage;

    public Laser(Location start, Location end, double size, int damage) {
        super(start, end);
        this.size = size;
    }

    public static Laser DrawLaser(Pose start, Tile[][] tileMap) {

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

    public LinkedHashSet<int[]> getTilesOn() { // TODO return tilesOn ordered from start to end
        double m = length / Math.abs(end.getX() - start.getX());
        double c = start.getY() - (m * start.getX());
        double maxX = start.getX();
        double maxY = start.getY();
        double minX = end.getX();
        double minY = end.getY();

        if (minX > maxX) {
            maxX = minX;
            minX = start.getX();
        }

        if (minY > maxY) {
            maxY = minY;
            minY = start.getY();
        }

        maxX += size;
        maxY += size;
        minX -= size;
        minY -= size;

        int[] max_loc = Tile.locationToTile(new Location(maxX, maxY));
        int[] min_loc = Tile.locationToTile(new Location(minX, minY));

        LinkedHashSet<int[]> tilesOn = new LinkedHashSet<>();
        double offSet = (Tile.TILE_SIZE / 2) + size;
        for (int t_x = min_loc[0]; t_x <= max_loc[0]; t_x++) {
            for (int t_y = min_loc[1]; t_y <= max_loc[1]; t_y++) {
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
