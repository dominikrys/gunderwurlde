package mapeditor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import server.engine.state.map.tile.Door;
import shared.lists.EntityList;
import shared.lists.Team;
import shared.lists.TileList;
import shared.lists.TileState;

public class MapWriter {
	
	private static int asciiCode = 65;
	
	public static MapSave readSave(File saveFile) {
		MapSave mapSave = null;
		
		try {
			FileInputStream fileIn = new FileInputStream(saveFile);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			
			mapSave = (MapSave) objectIn.readObject();
			
			objectIn.close();
			fileIn.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return mapSave;
	}
	
	public static void saveMap(MapSave mapSave) {
		 
        try {
        	
        	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        	File file = new File(currentPath + "/maps/saves", mapSave.getMapName() + ".gm");
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(mapSave);
            objectOut.close();
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	public static void completeMap(MapSave mapSave) {
		
		try {
			saveMap(mapSave);
			
			String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        	File file = new File(currentPath + "/maps/", mapSave.getMapName() + ".gamemap");
			FileOutputStream fileOut = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOut));
			
			bw.write("# " + mapSave.getMapName() + "\n");
			bw.write("#Dimensions X,Y:\n");
			bw.write(mapSave.getMapWidth() + " " + mapSave.getMapHeight() + "\n");
			bw.write("#Tiles\n");
			bw.write("#TileID(Any unique char),TYPE, STATE, (value of bounce if soild, otherwise friction.)\n");
			
			HashMap<TileList, Character> tileLetter = new HashMap<>();
			
			EnumSet.allOf(TileList.class).forEach(tileList -> {
				char letter = Character.valueOf((char) asciiCode);
				tileLetter.put(tileList, letter);
				try {
					bw.write(letter + " " + tileList.toString() + "\n");
					asciiCode++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			asciiCode = 65;
			
			bw.write("\n#Tile Map\n");
			for(int i = 0 ; i < mapSave.getMapTiles().length ; i++) {
				for(int j = 0 ; j < mapSave.getMapTiles()[0].length ; j++) {
					if(mapSave.getMapTiles()[j][i] != null) {
						bw.write(tileLetter.get(mapSave.getMapTiles()[j][i].getType()));
					}
					else {
						bw.write(tileLetter.get(TileList.VOID));
					}
					if(j + 1 != mapSave.getMapTiles()[0].length) {
						bw.write(" ");
					}
				}
				bw.write("\n");
			}
			
			bw.write("\n#Team Spawns\n");
			bw.write("RED" + " " + mapSave.getTeamSpawns().get(Team.RED)[0] + " " + mapSave.getTeamSpawns().get(Team.RED)[1] + "\n");
			bw.write("BLUE" + " " + mapSave.getTeamSpawns().get(Team.BLUE)[0] + " " + mapSave.getTeamSpawns().get(Team.BLUE)[1] + "\n");
			bw.write("GREEN" + " " + mapSave.getTeamSpawns().get(Team.GREEN)[0] + " " + mapSave.getTeamSpawns().get(Team.GREEN)[1] + "\n");
			bw.write("YELLOW" + " " + mapSave.getTeamSpawns().get(Team.YELLOW)[0] + " " + mapSave.getTeamSpawns().get(Team.YELLOW)[1] + "\n");
			
			bw.write("\n#Zones\n");
			for(Map.Entry<String, ZoneSettings> zone : mapSave.getZoneMap().entrySet()) {
				bw.write("#" + zone.getValue().getZoneName() + "\n");
				bw.write("#EnemySpawns\n");
				for(int i = 0 ; i < zone.getValue().getEnemySpawns().size() ; i++) {
					bw.write(zone.getValue().getEnemySpawns().get(i)[0] + " " + zone.getValue().getEnemySpawns().get(i)[1]);
					if(i + 1 != zone.getValue().getEnemySpawns().size()) {
						 bw.write("\n");
					}
				}
				bw.write("\n\n#Triggers\n");
				for(int i = 0 ; i < zone.getValue().getTriggers().size() ; i++) {
					bw.write(zone.getValue().getTriggers().get(i)[0] + " " + zone.getValue().getTriggers().get(i)[1]);
					if(i + 1 != zone.getValue().getTriggers().size()) {
						bw.write("\n");
					}
				}
				bw.write("\n\n#Optional Doors (Tile to return) (Location params) (Kills Required)");
				for(Map.Entry<int[], Door> door : mapSave.getZoneMap().get(zone.getKey()).getDoors().entrySet()) {
					bw.write("\n" + tileLetter.get(door.getValue().getTileToReturn().getType()) + " " + door.getKey()[0] + " " + door.getKey()[1] + " " + door.getValue().getKillsLeft());
				}
				bw.write("\n\n#Rounds 1st line: (entity type), (int params) 2nd line: (start time), (spawn interval), (amount per spawn), (total to spawn)");
				for(Map.Entry<String, RoundSettings> round : mapSave.getZoneMap().get(zone.getKey()).getRounds().entrySet()) {
					bw.write("\n#" + round.getKey());
					for(Map.Entry<EntityList, WaveSettings> enemy : mapSave.getZoneMap().get(zone.getKey()).getRounds().get(round.getKey()).getWaves().entrySet()) {
						bw.write("\n" + enemy.getKey().toString());
						bw.write("\n" + enemy.getValue().getStartTime() + " " + enemy.getValue().getSpawnInterval() + " " + enemy.getValue().getAmountPerSpawn() + " " + enemy.getValue().getTotal() + "\n");
					}
				}
				bw.write("\n\n");
			}
			bw.write("\n");
		 
			bw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
	}

}
