package data;

import java.util.LinkedHashSet;

import data.ai.Enemy;
import data.map.GameMap;
import data.player.Player;
import data.projectile.Projectile;

public class GameState {
	protected GameMap currentMap;
	protected LinkedHashSet<Enemy> enemies;
	protected LinkedHashSet<Projectile> projectiles;
	protected LinkedHashSet<Player> players;	

	public GameState() {
		// TODO Auto-generated constructor stub
	}

	public GameMap getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(GameMap currentMap) {
		this.currentMap = currentMap;
		//TODO: reset everything else and move players.
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
