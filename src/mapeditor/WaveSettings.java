package mapeditor;

import java.io.Serializable;

import shared.lists.EntityList;

/**
 * WaveSettings class. Contains the settings for an enemy.
 *
 * @author Mak Hong Lun Timothy
 */
public class WaveSettings implements Serializable{
	/**
     * serialVersionUID - serialVersionUID
     */
	private static final long serialVersionUID = 1L;
	/**
     * enemy - Type of enemy
     */
	protected EntityList enemy;
	/**
     * startTime - Start time of enemy
     */
	protected int startTime;
	/**
     * spawnInterval - Spawn interval of enemy
     */
    protected int spawnInterval;
    /**
     * amountPerSpawn - Amount per spawn of enemy
     */
    protected int amountPerSpawn;
    /**
     * total - Total amount to spawn of enemy
     */
    protected int total;
    /**
     * isReady - Boolean whether this enemy is fully set
     */
    protected boolean isReady;
    
    /**
     * Constructor
     * 
     * @param enemy Type of enemy
     */
    public WaveSettings(EntityList enemy) {
    	this.enemy = enemy;
    	this.isReady = false;
    }
    
    /**
     * Getter for enemy
     * 
     * @return enemy
     */
    public EntityList getEnemy() {
    	return this.enemy;
    }
    
    /**
     * Setter for enemy
     * 
     * @param enemy Type of enemy
     */
    public void setEnemy(EntityList enemy) {
    	this.enemy = enemy;
    }
    
    /**
     * Getter for startTime
     * 
     * @return startTime
     */
    public int getStartTime() {
    	return this.startTime;
    }
    
    /**
     * Setter for startTime
     * 
     * @param startTime Start time of enemy
     */
    public void setStartTime(int startTime) {
    	this.startTime = startTime;
    }
    
    /**
     * Getter for spawnInterval
     * 
     * @return spawnInterval
     */
    public int getSpawnInterval() {
    	return this.spawnInterval;
    }
    
    /**
     * Setter for spawnInterval
     * 
     * @param spawnInterval Spawn interval of enemy
     */
    public void setSpawnInterval(int spawnInterval) {
    	this.spawnInterval = spawnInterval;
    }
    
    /**
     * Getter for amountPerSpawn
     * 
     * @return amountPerSpawn
     */
    public int getAmountPerSpawn() {
    	return this.amountPerSpawn;
    }
    
    /**
     * Setter for amountPerSpawn
     * 
     * @param amountPerSpawn Amount per spawn of enemy
     */
    public void setAmountPerSpawn(int amountPerSpawn) {
    	this.amountPerSpawn = amountPerSpawn;
    }
    
    /**
     * Getter for total
     * 
     * @return total
     */
    public int getTotal() {
    	return this.total;
    }
    
    /**
     * Setter for total
     * 
     * @param total Total amount to spawn of enemy
     */
    public void setTotal(int total) {
    	this.total = total;
    }
    
    /**
     * Getter for isReady
     * 
     * @return Boolean whether this enemy is fully set
     */
    public boolean isReady() {
    	return this.isReady;
    }
    
    /**
     * Setter for isReady
     * 
     * @param ready True if ready, false otherwise
     */
    public void setReady(boolean ready) {
    	this.isReady = ready;
    }
    
}
