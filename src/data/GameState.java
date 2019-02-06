package data;

import data.entity.enemy.Enemy;
import data.entity.ItemDrop;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
import data.map.GameMap;
import data.map.tile.Tile;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameState {
    protected GameMap currentMap;
    protected LinkedHashMap<Integer, Enemy> enemies;
    protected LinkedHashSet<Projectile> projectiles;
    protected LinkedHashMap<Integer, Player> players;
    protected LinkedHashMap<Integer, ItemDrop> items;
    protected Iterator<Location> spawnIterator;

    public GameState(GameMap currentMap, LinkedHashMap<Integer, Player> players) {
        this.players = players;
        this.spawnIterator = currentMap.getPlayerSpawns().iterator();
        setCurrentMap(currentMap);
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
        this.enemies = new LinkedHashMap<>();
        this.projectiles = new LinkedHashSet<>(); // TODO see if setting to higher initial size improves performance (probably
                                                  // negligible)
        this.items = new LinkedHashMap<>();

        for (Player p : players.values()) {
            addPlayer(p);
        }
    }

    public LinkedHashMap<Integer, ItemDrop> getItems() {
        return items;
    }

    public void setItems(LinkedHashMap<Integer, ItemDrop> items) {
        this.items = items;
    }

    public void addItem(ItemDrop item) {
        this.items.put(item.getID(), item);
    }

    public LinkedHashMap<Integer, Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(LinkedHashMap<Integer, Enemy> enemies) {
        this.enemies = enemies;
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.put(enemy.getID(), enemy);
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

    public LinkedHashMap<Integer, Player> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedHashMap<Integer, Player> players) {
        this.players = players;
    }

    public void setTileMap(Tile[][] tileMap) {
        this.currentMap.setTileMap(tileMap);
    }

    public void addPlayer(Player player) {
        if (!spawnIterator.hasNext())
            spawnIterator = currentMap.getPlayerSpawns().iterator();
        player.setPose(new Pose(spawnIterator.next()));
        this.players.put(player.getID(), player);
    }

}
