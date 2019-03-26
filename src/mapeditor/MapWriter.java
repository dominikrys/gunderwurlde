package mapeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;

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

}
