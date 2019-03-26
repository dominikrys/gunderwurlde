package shared.view;

import java.io.Serializable;

import shared.Location;
import shared.lists.Team;

public class LaserView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Location start;
    protected final Location end;
    protected final double width;
    protected final Team team;

    public LaserView(Location start, Location end, double width, Team team) {
        this.start = start;
        this.end = end;
        this.width = width;
        this.team = team;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public double getWidth() {
        return width;
    }

    public Team getTeam() {
        return team;
    }

}
