package mapeditor;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class MapWriter {
	
	public static void saveMap(MapEditor mapEditor) {
		 
        try {
 
            FileOutputStream fileOut = new FileOutputStream("file:src/mapeditor/saves/" + mapEditor.getMapName());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(mapEditor);
            objectOut.close();
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
