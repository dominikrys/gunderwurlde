package client.data;

import java.util.LinkedHashSet;

public class GameView {
    protected LinkedHashSet<PlayerView> players;
    protected LinkedHashSet<EnemyView> enemies;
    protected LinkedHashSet<ProjectileView> projectiles;
    protected LinkedHashSet<ItemDropView> itemDrops;
    protected TileView[][] tileMap;
    protected int xDim;
    protected int yDim;

    public GameView(LinkedHashSet<PlayerView> players, LinkedHashSet<EnemyView> enemies, LinkedHashSet<ProjectileView> projectiles,
            LinkedHashSet<ItemDropView> itemDrops, TileView[][] tileMap) {
        this.players = players;
        this.enemies = enemies;
        this.projectiles = projectiles;
        this.itemDrops = itemDrops;
        this.tileMap = tileMap;
        this.xDim = tileMap.length;
        this.yDim = tileMap[0].length;
    }

    public int getXDim() {
        return xDim;
    }

    public int getYDim() {
        return yDim;
    }

    public LinkedHashSet<PlayerView> getPlayers() {
        return players;
    }

    public LinkedHashSet<EnemyView> getEnemies() {
        return enemies;
    }

    public LinkedHashSet<ProjectileView> getProjectiles() {
        return projectiles;
    }

    public LinkedHashSet<ItemDropView> getItemDrops() {
        return itemDrops;
    }

    public TileView[][] getTileMap() {
        return tileMap;
    }

}
