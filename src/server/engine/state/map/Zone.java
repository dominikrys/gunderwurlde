package server.engine.state.map;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import server.engine.state.entity.Entity;
import shared.Location;
import shared.Pose;

public class Zone {
    private static int nextZoneID = 0;

    protected LinkedHashSet<Location> entitySpawns;
    protected Iterator<Location> entitySpawnIterator;
    protected Iterator<Round> roundIterator;
    protected LinkedHashSet<int[]> triggers;
    protected LinkedHashSet<Wave> currentWaves;
    protected Round currentRound;
    protected int entityCount;
    // TODO doors & enemies to kill
    protected int id;

    public Zone(LinkedHashSet<Location> entitySpawns, LinkedList<Round> rounds, LinkedHashSet<int[]> triggers) {
        this.entitySpawns = entitySpawns;
        this.entitySpawnIterator = entitySpawns.iterator();
        this.roundIterator = rounds.iterator();
        this.triggers = triggers;
        this.currentWaves = new LinkedHashSet<>();
        this.currentRound = roundIterator.next();
        this.entityCount = 0;
        this.id = nextZoneID++;
    }

    public void activate() {
        currentRound.start();
    }

    public LinkedHashSet<Entity> getEntitysToSpawn() {
        LinkedHashSet<Entity> entitysToSpawn = new LinkedHashSet<>();
        LinkedHashSet<Wave> newWaves = new LinkedHashSet<>();

        if (currentRound.hasWavesLeft()) {
            while (currentRound.isWaveReady()) {
                currentWaves.add(currentRound.getNextWave());
            }
        }

        if (!currentWaves.isEmpty()) {
            for (Wave wave : currentWaves) {
                if (!wave.isDone()) {
                    Wave currentWave = wave;
                    if (currentWave.readyToSpawn()) {
                        Entity templateEntityToSpawn = currentWave.getEntityToSpawn();
                        int amountToSpawn = currentWave.getSpawn();

                        for (int i = 0; i < amountToSpawn; i++) {
                            if (!entitySpawnIterator.hasNext())
                                entitySpawnIterator = entitySpawns.iterator();
                            Entity entityToSpawn = templateEntityToSpawn.makeCopy();
                            entityToSpawn.setPose(new Pose(entitySpawnIterator.next()));
                            // TODO spawning status
                            entitysToSpawn.add(entityToSpawn);
                            entityCount++;
                        }
                    }
                    newWaves.add(currentWave);
                }
            }
        } else if (entityCount == 0) {
            if (roundIterator.hasNext()) {
                currentRound = roundIterator.next();
                currentRound.start();
            } else {
                // TODO no more rounds left deactivate zone
            }
        }
        currentWaves = newWaves;
        return entitysToSpawn;
    }

    public boolean entityRemoved() { // TODO return true to open doors
        entityCount--;
        return false;
    }

    public LinkedHashSet<int[]> getTriggers() {
        return triggers;
    }

    public int getId() {
        return id;
    }

}
