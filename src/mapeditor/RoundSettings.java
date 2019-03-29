package mapeditor;

import java.io.Serializable;
import java.util.HashMap;

import shared.lists.EntityList;

/**
 * RoundSettings class. Contains the settings for a round.
 *
 * @author Mak Hong Lun Timothy
 */
public class RoundSettings implements Serializable{
	/**
     * serialVersionUID - serialVersionUID
     */
	private static final long serialVersionUID = 1L;
	/**
     * name - Name of this round
     */
	protected String name;
	/**
     * waves - HashMap containing enemy types and their settings for this round
     */
	protected HashMap<EntityList, WaveSettings> waves;
    
	/**
     * Constructor
     */
    public RoundSettings() {
    	this.waves = new HashMap<EntityList, WaveSettings>();
    }
    
    /**
     * Getter for waves
     * 
     * @return waves
     */
    public HashMap<EntityList, WaveSettings> getWaves() {
    	return this.waves;
    }
    
    /**
     * Setter for waves
     */
    public void setWaves(HashMap<EntityList, WaveSettings> waves) {
    	this.waves = waves;
    }
    
}
