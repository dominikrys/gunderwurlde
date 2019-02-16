package server.engine.state.map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.lists.MapList;
import shared.lists.Teams;
import shared.lists.TileState;
import shared.lists.TileTypes;

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
            tiles.put(ID, new Tile(type, state));
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

        HashMap<Teams, Location> teamSpawns = new HashMap<>();
        line = file.removeFirst();
        while (!line.isEmpty()) {
            LinkedList<String> teamComp = getComponents(line);
            teamSpawns.put(Teams.valueOf(teamComp.removeFirst()), compToLocation(teamComp));
            line = file.removeFirst();
        }

        LinkedHashSet<Location> enemySpawns = new LinkedHashSet<>();
        line = file.removeFirst();
        while (!line.isEmpty()) {
            enemySpawns.add(compToLocation(getComponents(line)));
            line = file.removeFirst();
        }

        return new GameMap(xDim, yDim, tileMap, teamSpawns, enemySpawns, Meadow.generateRounds(), mapName);
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
