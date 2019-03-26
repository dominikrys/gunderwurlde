package mapeditor;

import java.io.Serializable;

import shared.lists.EntityList;

public class WaveSettings implements Serializable{
	
	private static final long serialVersionUID = 1L;
	protected EntityList enemy;
	protected int startTime;
    protected int spawnInterval;
    protected int amountPerSpawn;
    protected int total;
    protected boolean isReady;
    
    public WaveSettings(EntityList enemy) {
    	this.enemy = enemy;
    	this.isReady = false;
    }
    
    public EntityList getEnemy() {
    	return this.enemy;
    }
    
    public void setEnemy(EntityList enemy) {
    	this.enemy = enemy;
    }
    
    public int getStartTime() {
    	return this.startTime;
    }
    
    public void setStartTime(int startTime) {
    	this.startTime = startTime;
    }
    
    public int getSpawnInterval() {
    	return this.spawnInterval;
    }
    
    public void setSpawnInterval(int spawnInterval) {
    	this.spawnInterval = spawnInterval;
    }
    
    public int getAmountPerSpawn() {
    	return this.amountPerSpawn;
    }
    
    public void setAmountPerSpawn(int amountPerSpawn) {
    	this.amountPerSpawn = amountPerSpawn;
    }
    
    public int getTotal() {
    	return this.total;
    }
    
    public void setTotal(int total) {
    	this.total = total;
    }
    
    public boolean isReady() {
    	return this.isReady;
    }
    
    public void setReady(boolean ready) {
    	this.isReady = ready;
    }
    
}
