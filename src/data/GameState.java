package data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import data.ai.Enemy;
import data.item.Item;
import data.map.GameMap;
import data.player.Player;
import data.projectile.Projectile;

public class GameState {
	protected GameMap currentMap;
	protected LinkedHashSet<Enemy> enemies;
	protected LinkedHashSet<Projectile> projectiles;
	protected LinkedHashSet<Player> players;
	protected LinkedHashSet<Item> items; //each must have location

	public GameState(GameMap currentMap, LinkedHashSet<Player> players) {
		this.players = players;
		setCurrentMap(currentMap);
		this.enemies = new LinkedHashSet<Enemy>();
		this.projectiles = new LinkedHashSet<Projectile>();
		this.items = new LinkedHashSet<Item>();
	}

	public GameMap getCurrentMap() {
		return currentMap;
	}

	public LinkedHashSet<Item> getItems() {
		return items;
	}
	
	public void addItem(Item item) {
		this.items.add(item);
	}

	public void setItems(LinkedHashSet<Item> items) {
		this.items = items;
	}

	public void setCurrentMap(GameMap currentMap) {
		this.currentMap = currentMap;
		this.enemies = new LinkedHashSet<Enemy>();
		this.projectiles = new LinkedHashSet<Projectile>();
		this.items = new LinkedHashSet<Item>();
		
		ArrayList<Location> playerSpawns = currentMap.getPlayerSpawns();
		Iterator<Location> spawnIterator = playerSpawns.iterator();
		
		for (Player p: players) {
			if (!spawnIterator.hasNext()) spawnIterator = playerSpawns.iterator();
			p.setPose(new Pose(spawnIterator.next()));
		}
	}

	public LinkedHashSet<Enemy> getEnemies() {
		return enemies;
	}
	
	public void setEnemies(LinkedHashSet<Enemy> enemies) {
		this.enemies = enemies;
	}

	public void addEnemy(Enemy enemy) {
		this.enemies.add(enemy);
	}

	public LinkedHashSet<Projectile> getProjectiles() {
		return projectiles;
	}

	public void setProjectiles(LinkedHashSet<Projectile> projectiles) {
		this.projectiles = projectiles;
	}
	
	public void addProjectile(Projectile projectile) {
		this.projectiles.add(projectile);
	}

	public LinkedHashSet<Player> getPlayers() {
		return players;
	}

	public void setPlayers(LinkedHashSet<Player> players) {
		this.players = players;
	}

}
