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

import javafx.scene.image.Image;
import shared.lists.TileList;
import shared.lists.TileState;

public class MapWriter {
	
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
        	File file = new File(currentPath + "/maps/", mapSave.getMapName() + ".GAMEMAP");
			FileOutputStream fileOut = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOut));
			
			bw.write("# " + mapSave.getMapName() + "\n");
			bw.write("#Dimensions X,Y:\n");
			bw.write(mapSave.getMapWidth() + " " + mapSave.getMapHeight() + "\n");
			bw.write("#Tiles");
			bw.write("#TileID(Any unique char),TYPE, STATE, (value of bounce if soild, otherwise friction.)\n");
			
			HashMap<TileList, Character> tileLetter = new HashMap<>();
			int asciiCode = 65;
			EnumSet.allOf(TileList.class).forEach(tileList -> {
				char letter = Character.valueOf((char) asciiCode);
				tileLetter.put(tileList, letter);
				try {
					double coefficient;
					if(tileList.getTileState() == TileState.SOLID) {
						coefficient = tileList.getBounceCoefficient();
					}
					else {
						coefficient = tileList.getFriction();
					}
					bw.write(letter + " " + tileList.toString() + " " + tileList.getTileState() + " " + coefficient + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			
			/*
			for (int i = 0; i < 10; i++) {
				bw.write("something");
				bw.newLine();
			}
			*/
		 
			bw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
	}

}
