package server.engine.state.map;

import server.engine.state.entity.Entity;
import server.engine.state.map.tile.Door;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;

import java.util.*;

public class Zone {
    private static int nextZoneID = 0;

    protected LinkedHashSet<Location> entitySpawns;
    protected Iterator<Location> entitySpawnIterator;
    protected Iterator<Round> roundIterator;
    protected LinkedHashSet<int[]> triggers;
    protected LinkedHashSet<Wave> currentWaves;
    protected Round currentRound;
    protected int entityCount;
    protected LinkedHashMap<int[], Door> doors;
    protected int id;

    public Zone(LinkedHashSet<Location> entitySpawns, LinkedList<Round> rounds, LinkedHashSet<int[]> triggers, LinkedHashMap<int[], Door> doors) {
        this.entitySpawns = entitySpawns;
        this.entitySpawnIterator = entitySpawns.iterator();
        this.roundIterator = rounds.iterator();
        this.triggers = triggers;
        this.currentWaves = new LinkedHashSet<>();
        this.currentRound = roundIterator.next();
        this.doors = doors;
        this.entityCount = 0;
        this.id = nextZoneID++;
    }

    public LinkedHashMap<int[], Tile> getTileChanges() {
        LinkedHashMap<int[], Tile> tileChanges = new LinkedHashMap<>();
        for (Map.Entry<int[], Door> door : doors.entrySet()) {
            Door doorChecked = door.getValue();
            if (doorChecked.isOpen()) {
                tileChanges.put(door.getKey(), doorChecked.getTileToReturn());
            }
        }

        // TODO remove if doors dynamically open and close
        for (int[] doorToRemove : tileChanges.keySet()) {
            doors.remove(doorToRemove);
        }

        return tileChanges;
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
                            entityToSpawn.setZoneID(this.id);
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

    public void entityRemoved() {
        entityCount--;
        for (int[] door : doors.keySet()) {
            doors.get(door).entityKilled();
        }
    }

    public LinkedHashSet<int[]> getTriggers() {
        return triggers;
    }

    public int getId() {
        return id;
    }

}
