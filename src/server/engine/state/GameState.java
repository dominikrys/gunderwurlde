package server.engine.state;

import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.player.Player;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.map.GameMap;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.Team;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameState {
    protected GameMap currentMap;
    protected LinkedHashMap<Integer, Enemy> enemies;
    protected LinkedHashSet<Projectile> projectiles;
    protected LinkedHashMap<Integer, Player> players;
    protected LinkedHashMap<Integer, ItemDrop> items;
    protected EnumMap<Team, Location> teamSpawns;

    public GameState(GameMap currentMap, LinkedHashMap<Integer, Player> players) {
        this.players = players;
        this.teamSpawns = currentMap.getTeamSpawns();
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
        player.setPose(new Pose(teamSpawns.get(player.getTeam())));
        this.players.put(player.getID(), player);
    }

}
