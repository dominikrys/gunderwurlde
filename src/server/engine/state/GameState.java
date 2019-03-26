package server.engine.state;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.LivingEntity;
import server.engine.state.entity.player.Player;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.laser.Laser;
import server.engine.state.map.GameMap;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.Team;

public class GameState {
    protected GameMap currentMap;
    protected LinkedHashMap<Integer, LivingEntity> livingEntities;
    protected LinkedHashSet<Integer> playerIDs;
    protected LinkedHashSet<Integer> enemyIDs;
    protected LinkedHashSet<Projectile> projectiles;
    protected LinkedHashMap<Integer, ItemDrop> items;
    protected LinkedHashSet<Laser> lasers;
    protected EnumMap<Team, Location> teamSpawns;

    public GameState(GameMap currentMap, LinkedHashMap<Integer, LivingEntity> players) {
        this.livingEntities = players;
        this.playerIDs = new LinkedHashSet<>(players.keySet());
        this.teamSpawns = currentMap.getTeamSpawns();
        setCurrentMap(currentMap);
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
        this.enemyIDs = new LinkedHashSet<>();
        this.projectiles = new LinkedHashSet<>();
        this.lasers = new LinkedHashSet<>();
        this.items = new LinkedHashMap<>();
        LinkedHashMap<Integer, LivingEntity> livingEntities = new LinkedHashMap<>();
        for (Integer p : playerIDs) {
            Player player = (Player) this.livingEntities.get(p);
            player.setPose(new Pose(teamSpawns.get(player.getTeam())));
            livingEntities.put(player.getID(), player);
        }

        this.livingEntities = livingEntities;
    }

    public LinkedHashMap<Integer, ItemDrop> getItems() {
        return items;
    }

    public void setItems(LinkedHashMap<Integer, ItemDrop> items) {
        this.items = items;
    }

    public LinkedHashSet<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setProjectiles(LinkedHashSet<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public LinkedHashSet<Laser> getLasers() {
        return lasers;
    }

    public void setLasers(LinkedHashSet<Laser> lasers) {
        this.lasers = lasers;
    }

    public void setTileMap(Tile[][] tileMap) {
        this.currentMap.setTileMap(tileMap);
    }

    public void addPlayer(Player player) { // TODO remove if unused
        player.setPose(new Pose(teamSpawns.get(player.getTeam())));
        this.livingEntities.put(player.getID(), player);
        this.playerIDs.add(player.getID());
    }

    public LinkedHashMap<Integer, LivingEntity> getLivingEntities() {
        return livingEntities;
    }

    public void setLivingEntities(LinkedHashMap<Integer, LivingEntity> livingEntities) {
        this.livingEntities = livingEntities;
    }

    public LinkedHashSet<Integer> getPlayerIDs() {
        return playerIDs;
    }

    public void setPlayerIDs(LinkedHashSet<Integer> playerIDs) {
        this.playerIDs = playerIDs;
    }

    public LinkedHashSet<Integer> getEnemyIDs() {
        return enemyIDs;
    }

    public void setEnemyIDs(LinkedHashSet<Integer> enemyIDs) {
        this.enemyIDs = enemyIDs;
    }

}
