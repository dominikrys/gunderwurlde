package mapeditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import server.engine.state.map.tile.Door;

public class ZoneSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String zoneName;
	protected ArrayList<int[]> enemySpawns;
	protected ArrayList<int[]> triggers;
	protected HashMap<String, RoundSettings> rounds; 
    protected HashMap<int[], Door> doors;
    
    public ZoneSettings(String zoneName) {
    	this.zoneName = zoneName;
    	this.enemySpawns = new ArrayList<>();
    	this.triggers = new ArrayList<>();
    	this.rounds = new HashMap<String, RoundSettings>();
    	this.doors = new HashMap<int[], Door>();
    }
    
    public String getZoneName() {
    	return this.zoneName;
    }
    
    public void setZoneName(String zoneName) {
    	this.zoneName = zoneName;
    }
    
    public ArrayList<int[]> getEnemySpawns() {
    	return this.enemySpawns;
    }
    
    public void setEnemySpawn(ArrayList<int[]> enemySpawns) {
    	this.enemySpawns = enemySpawns;
    }
    
    public ArrayList<int[]> getTriggers() {
    	return this.triggers;
    }
    
    public void setTriggers(ArrayList<int[]> triggers) {
    	this.triggers = triggers;
    }
    
    public HashMap<String, RoundSettings> getRounds() {
    	return this.rounds;
    }
    
    public void setRounds(HashMap<String, RoundSettings> rounds) {
    	this.rounds = rounds;
    }
    
    public HashMap<int[], Door> getDoors() {
    	return this.doors;
    }
    
    public void setDoors(HashMap<int[], Door> doors) {
    	this.doors = doors;
    }

}
