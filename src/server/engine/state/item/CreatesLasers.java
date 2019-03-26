package server.engine.state.item;

import java.util.LinkedList;

import server.engine.state.laser.Laser;
import shared.Pose;
import shared.lists.Team;

public interface CreatesLasers {
    public LinkedList<Laser> getLasers(Pose origin, Team team);
}
