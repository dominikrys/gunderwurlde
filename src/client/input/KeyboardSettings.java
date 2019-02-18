package client.input;

import java.util.HashMap;
import java.util.Map.Entry;

public class KeyboardSettings {
	private HashMap<String,String> mapping = new HashMap<String,String>();
	
	public KeyboardSettings() {
		this.init();
	}
	
	public void init() {
		mapping.put("up", "W");
		mapping.put("left", "A");
		mapping.put("down", "S");
		mapping.put("right", "D");
		mapping.put("reload", "R");
		mapping.put("drop", "G");
		mapping.put("interact", "E");
		mapping.put("item1", "DIGIT1");
		mapping.put("item2", "DIGIT2");
		mapping.put("item3", "DIGIT3");
		mapping.put("esc", "ESCAPE");
	}
	
	public String getKey(String action) {
		return mapping.get(action);
	}
	
	public String getAction(String key) {
		for(Entry<String, String> entry : mapping.entrySet()) {
			if(entry.getValue().equals(key)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public void changeKey(String action, String newKey) {
		mapping.put(action, newKey);
	}
}
