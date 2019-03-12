package server.engine.state.map;

import server.engine.state.entity.Entity;
import server.engine.state.entity.enemy.*;
import server.engine.state.map.tile.Door;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.lists.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MapReader {
    private static String MAP_LOCATION = "maps";

    // test read
    public static void main(String[] args) {
        readMap(MapList.MEADOW);
    }

    public static GameMap readMap(MapList mapName) {
        LinkedList<String> file = readFile(mapName.toString());

        LinkedList<String> dims = getComponents(file.removeFirst());
        int xDim = Integer.valueOf(dims.removeFirst());
        int yDim = Integer.valueOf(dims.removeFirst());

        HashMap<Character, Tile> tiles = new HashMap<>();
        String line = file.removeFirst();
        while (!line.isEmpty()) {
            LinkedList<String> tileComp = getComponents(line);
            char ID = tileComp.removeFirst().charAt(0);
            TileTypes type = TileTypes.valueOf(tileComp.removeFirst());
            TileState state = TileState.valueOf(tileComp.removeFirst());
            double value = Double.valueOf(tileComp.removeFirst());
            tiles.put(ID, new Tile(type, state, value));
            line = file.removeFirst();
        }

        Tile[][] tileMap = new Tile[xDim][yDim];
        int y = 0;
        line = file.removeFirst();
        while (!line.isEmpty()) {
            LinkedList<String> mapComp = getComponents(line);
            for (int x = 0; x < xDim; x++) {
                tileMap[x][y] = tiles.get(mapComp.removeFirst().charAt(0)).getCopy();
            }
            y++;
            line = file.removeFirst();
        }

        EnumMap<Teams, Location> teamSpawns = new EnumMap<>(Teams.class);
        line = file.removeFirst();
        while (!line.isEmpty()) {
            LinkedList<String> teamComp = getComponents(line);
            teamSpawns.put(Teams.valueOf(teamComp.removeFirst()), compToLocation(teamComp));
            line = file.removeFirst();
        }

        LinkedHashMap<Integer, Zone> zones = new LinkedHashMap<>();
        line = file.removeFirst();
        while (!line.isEmpty()) {
            LinkedHashSet<Location> entitySpawns = new LinkedHashSet<>();
            while (!line.isEmpty()) {
                entitySpawns.add(compToLocation(getComponents(line)));
                line = file.removeFirst();
            }
            
            LinkedHashSet<int[]> triggers = new LinkedHashSet<>();
            line = file.removeFirst();
            while (!line.isEmpty()) {
                LinkedList<String> cords = getComponents(line);
                int[] tileCords = { Integer.valueOf(cords.removeFirst()), Integer.valueOf(cords.removeFirst()) };
                triggers.add(tileCords);
                line = file.removeFirst();
            }
            
            LinkedHashMap<int[], Door> doors = new LinkedHashMap<>();
            line = file.removeFirst();
            while (!line.isEmpty()) {
                LinkedList<String> doorParams = getComponents(line);
                Tile tileToReturn = tiles.get(doorParams.removeFirst().charAt(0)).getCopy();
                int[] tileCords = { Integer.valueOf(doorParams.removeFirst()), Integer.valueOf(doorParams.removeFirst()) };
                doors.put(tileCords, new Door(tileToReturn, Integer.valueOf(doorParams.removeFirst())));
                line = file.removeFirst();
            }

            LinkedList<Round> rounds = new LinkedList<>();
            line = file.removeFirst();
            while (!line.isEmpty()) {
                TreeSet<Wave> waves = new TreeSet<>();
                while (!line.isEmpty()) {
                    Entity entityToSpawn = getEntity(getComponents(line));
                    LinkedList<String> waveParams = getComponents(file.removeFirst());
                    waves.add(new Wave(Long.valueOf(waveParams.removeFirst()), Integer.valueOf(waveParams.removeFirst()), entityToSpawn,
                            Integer.valueOf(waveParams.removeFirst()), Integer.valueOf(waveParams.removeFirst())));
                    line = file.removeFirst();
                }
                rounds.add(new Round(waves, false));
                line = file.removeFirst();
            }
            Zone zoneToAdd = new Zone(entitySpawns, rounds, triggers, doors);
            zones.put(zoneToAdd.getId(), zoneToAdd);
            line = file.removeFirst();
        }
        

        return new GameMap(xDim, yDim, tileMap, teamSpawns, zones, mapName);
    }

    // TODO support other enemies & then entities
    private static Entity getEntity(LinkedList<String> entityParams) {
        EntityList entity = EntityList.valueOf(entityParams.removeFirst());
        switch (entity) {
        case ZOMBIE:
            return new Zombie();
        case RUNNER:
            return new RunnerZombie(Integer.valueOf(entityParams.removeFirst()));
        case SOLDIER:
            return new SoldierZombie(Integer.valueOf(entityParams.removeFirst()), Integer.valueOf(entityParams.removeFirst()));
        case MIDGET:
            return new ShotgunMidget(Integer.valueOf(entityParams.removeFirst()), Integer.valueOf(entityParams.removeFirst()));
            case BOOMER:
                return new Boomer();
        default:
            System.out.println("ERROR: Entity not yet supported for spawning: " + entity.toString());
            return new Zombie();
        }
    }

    private static Location compToLocation(LinkedList<String> components) {
        return Tile.tileToLocation(Integer.valueOf(components.removeFirst()), Integer.valueOf(components.removeFirst()));
    }

    private static LinkedList<String> getComponents(String line) {
        return new LinkedList<String>(Arrays.asList(line.split(" ")));
    }

    private static LinkedList<String> readFile(String fileName) {
        String path = MAP_LOCATION + "/" + fileName + ".gamemap";
        LinkedList<String> file = new LinkedList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("#"))
                    file.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " not found!");
        } catch (IOException e) {
            System.out.println("IOError with " + fileName);
        }
        if (file.isEmpty()) {
            System.out.println(fileName + "is empty!");
        }
        return file;
    }
    
}
