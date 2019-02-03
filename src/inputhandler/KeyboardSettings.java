package inputhandler;

public class KeyboardSettings {
	private String up = "W";
	private String down = "S";
	private String left = "A";
	private String right = "D";
	private String reload = "R";
	
	public String getKey(String action) {
		switch(action) {
			case "up" :
				return up;
			case "down" :
				return down;
			case "left" :
				return left;
			case "right" :
				return right;
			case "reload" :
				return reload;
		}
		return null;
	}
	
	public void changeKey(String oldKey, String newKey) {
		switch(oldKey) {
			case "up" :
				up = newKey;
			case "down" :
				down = newKey;
			case "left" :
				left = newKey;
			case "right" :
				right = newKey;
			case "reload" :
				reload = newKey;
		}
	}
}
