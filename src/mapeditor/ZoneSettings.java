package mapeditor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import server.engine.state.map.Round;
import server.engine.state.map.tile.Door;

public class ZoneSettings {
	
	protected String zoneName;
	protected ArrayList<int[]> enemySpawns;
	protected ArrayList<int[]> triggers;
	protected ArrayList<Round> rounds; 
    protected LinkedHashMap<int[], Door> doors;
    
    public ZoneSettings(String zoneName) {
    	this.zoneName = zoneName;
    	this.enemySpawns = new ArrayList<>();
    	this.triggers = new ArrayList<>();
    	this.rounds = new ArrayList<>();
    	this.doors = new LinkedHashMap<>();
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
    
    public ArrayList<Round> getRounds() {
    	return this.rounds;
    }
    
    public void setRounds(ArrayList<Round> rounds) {
    	this.rounds = rounds;
    }
    
    public LinkedHashMap<int[], Door> getDoors() {
    	return this.doors;
    }
    
    public void setDoors(LinkedHashMap<int[], Door> doors) {
    	this.doors = doors;
    }

}
