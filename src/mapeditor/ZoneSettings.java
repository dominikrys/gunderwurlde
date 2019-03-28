package mapeditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import server.engine.state.map.tile.Door;

/**
 * ZoneSettings class. Contains the settings for a zone.
 *
 * @author Mak Hong Lun Timothy
 */
public class ZoneSettings implements Serializable {
	/**
     * serialVersionUID - serialVersionUID
     */
	private static final long serialVersionUID = 1L;
	/**
     * zoneName - Name of zone
     */
	protected String zoneName;
	/**
     * enemySpawns - List of all enemy spawns
     */
	protected ArrayList<int[]> enemySpawns;
	/**
     * triggers - List of all triggers
     */
	protected ArrayList<int[]> triggers;
	/**
     * rounds - HashMap containing all names and settings of rounds
     */
	protected HashMap<String, RoundSettings> rounds;
	/**
     * doors - HashMap containing all X Y coordinates and settings of doors
     */
    protected HashMap<int[], Door> doors;
    
    /**
     * Constructor
     * 
     * @param zoneName Name of zone
     */
    public ZoneSettings(String zoneName) {
    	this.zoneName = zoneName;
    	this.enemySpawns = new ArrayList<>();
    	this.triggers = new ArrayList<>();
    	this.rounds = new HashMap<String, RoundSettings>();
    	this.doors = new HashMap<int[], Door>();
    }
    
    /**
     * Getter for zoneName
     * 
     * @return zoneName
     */
    public String getZoneName() {
    	return this.zoneName;
    }
    
    /**
     * Setter for zoneName
     * 
     * @param zoneName Name of zone
     */
    public void setZoneName(String zoneName) {
    	this.zoneName = zoneName;
    }
    
    /**
     * Getter for enemySpawns
     * 
     * @return enemySpawns
     */
    public ArrayList<int[]> getEnemySpawns() {
    	return this.enemySpawns;
    }
    
    /**
     * Setter for enemySpawns
     * 
     * @param enemySpawns List of all enemy spawns
     */
    public void setEnemySpawn(ArrayList<int[]> enemySpawns) {
    	this.enemySpawns = enemySpawns;
    }

    /**
     * Getter for triggers
     * 
     * @return triggers
     */
    public ArrayList<int[]> getTriggers() {
    	return this.triggers;
    }
    
    /**
     * Setter for triggers
     * 
     * @param triggers List of all triggers
     */
    public void setTriggers(ArrayList<int[]> triggers) {
    	this.triggers = triggers;
    }
    
    /**
     * Getter for rounds
     * 
     * @return rounds
     */
    public HashMap<String, RoundSettings> getRounds() {
    	return this.rounds;
    }
    
    /**
     * Setter for rounds
     * 
     * @param rounds HashMap containing all names and settings of rounds
     */
    public void setRounds(HashMap<String, RoundSettings> rounds) {
    	this.rounds = rounds;
    }
    
    /**
     * Getter for doors
     * 
     * @return doors
     */
    public HashMap<int[], Door> getDoors() {
    	return this.doors;
    }
    
    /**
     * Setter for doors
     * 
     * @param doors HashMap containing all X Y coordinates and settings of doors
     */
    public void setDoors(HashMap<int[], Door> doors) {
    	this.doors = doors;
    }

}
