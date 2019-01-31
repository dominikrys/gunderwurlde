package data;

import data.entity.enemy.Enemy;
import data.entity.item.ItemDrop;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
import data.map.GameMap;
import data.map.tile.Tile;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class GameState {
    protected GameMap currentMap;
    protected LinkedHashSet<Enemy> enemies;
    protected LinkedHashSet<Projectile> projectiles;
    protected LinkedHashSet<Player> players;
    protected LinkedHashSet<ItemDrop> items;
    protected Iterator<Location> spawnIterator;

    public GameState(GameMap currentMap, LinkedHashSet<Player> players) {
        this.players = players;
        this.spawnIterator = currentMap.getPlayerSpawns().iterator();
        setCurrentMap(currentMap);
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
        this.enemies = new LinkedHashSet<Enemy>();
        this.projectiles = new LinkedHashSet<Projectile>();
        this.items = new LinkedHashSet<ItemDrop>();
       
        for (Player p : players) {
            addPlayer(p);
        }
    }

    public LinkedHashSet<ItemDrop> getItems() {
        return items;
    }

    public void setItems(LinkedHashSet<ItemDrop> items) {
        this.items = items;
    }

    public void addItem(ItemDrop item) {
        this.items.add(item);
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

    public void setTileMap(Tile[][] tileMap) {
        this.currentMap.setTileMap(tileMap);
    }

    public void addPlayer(Player player) {
        if (!spawnIterator.hasNext())
            spawnIterator = currentMap.getPlayerSpawns().iterator();
        player.setPose(new Pose(spawnIterator.next()));
    }

}
