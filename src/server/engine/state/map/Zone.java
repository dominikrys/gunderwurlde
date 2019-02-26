package server.engine.state.map;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import shared.Location;

public class Zone {
    private static int nextZoneID = 0;

    protected LinkedHashSet<Location> enemySpawns;
    protected LinkedList<Round> rounds;
    protected LinkedList<int[]> triggers;
    // TODO doors & enemies to kill
    protected int id;

    public Zone(LinkedHashSet<Location> enemySpawns, LinkedList<Round> rounds, LinkedList<int[]> triggers) {
        this.enemySpawns = enemySpawns;
        this.rounds = rounds;
        this.triggers = triggers;
        this.id = nextZoneID++;
    }

    public LinkedHashSet<Location> getEnemySpawns() {
        return enemySpawns;
    }

    public Round getNextRound() {
        return rounds.removeFirst();
    }

    public LinkedList<int[]> getTriggers() {
        return triggers;
    }

    public int getId() {
        return id;
    }

}
