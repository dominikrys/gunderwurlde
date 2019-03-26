package mapeditor;

import java.io.Serializable;
import java.util.HashMap;

import shared.lists.EntityList;

public class RoundSettings implements Serializable{
	
	private static final long serialVersionUID = 1L;
	protected String name;
	protected HashMap<EntityList, WaveSettings> waves;
    protected boolean isBoss;
    
    public RoundSettings() {
    	this.waves = new HashMap<EntityList, WaveSettings>();
    	this.isBoss = false;
    }
    
    public HashMap<EntityList, WaveSettings> getWaves() {
    	return this.waves;
    }
    
    public void setWaves(HashMap<EntityList, WaveSettings> waves) {
    	this.waves = waves;
    }
    
    public boolean isBoss() {
    	return this.isBoss;
    }
    
    public void setIsBoss(boolean isBoss) {
    	this.isBoss = isBoss;
    }

}
