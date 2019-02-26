package server.engine.state.map;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import shared.Location;

public class Zone {
    private static int nextZoneID = 0;

    protected LinkedHashSet<Location> entitySpawns;
    protected LinkedList<Round> rounds;
    protected LinkedHashSet<int[]> triggers;
    // TODO doors & enemies to kill
    protected int id;

    public Zone(LinkedHashSet<Location> entitySpawns, LinkedList<Round> rounds, LinkedHashSet<int[]> triggers) {
        this.entitySpawns = entitySpawns;
        this.rounds = rounds;
        this.triggers = triggers;
        this.id = nextZoneID++;
    }

    public LinkedHashSet<Location> getEntitySpawns() {
        return entitySpawns;
    }

    public Round getNextRound() {
        return rounds.removeFirst();
    }

    public LinkedHashSet<int[]> getTriggers() {
        return triggers;
    }

    public int getId() {
        return id;
    }

}
