package server.engine.state.item;

import java.util.LinkedList;

import server.engine.state.entity.projectile.Projectile;
import shared.Pose;
import shared.lists.Team;

public interface CreatesProjectiles {
    public LinkedList<Projectile> getProjectiles(Pose origin, Team team, int desiredDistance);
}
