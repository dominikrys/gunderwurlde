package shared.lists;

public enum MusicList {
	
	MUSIC1("file:assets/sound/gun/gun_cock.wav");

private String soundPath;
	
	MusicList(String soundPath) {
        this.soundPath = soundPath;
    }

    public String getPath() {
        return soundPath;
    }
	
}
