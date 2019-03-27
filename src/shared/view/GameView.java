package shared.view;

import java.io.Serializable;
import java.util.LinkedHashSet;

import shared.lists.Team;
import shared.view.entity.EnemyView;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;
import shared.view.entity.ProjectileView;

public class GameView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final LinkedHashSet<PlayerView> players;
    protected final LinkedHashSet<EnemyView> enemies;
    protected final LinkedHashSet<ProjectileView> projectiles;
    protected final LinkedHashSet<ItemDropView> itemDrops;
    protected final LinkedHashSet<LaserView> lasers;
    protected final LinkedHashSet<ExplosionView> explosions;
    protected final TileView[][] tileMap;
    protected final Team winningTeam; // NONE if nobody has won yet
    protected final int xDim;
    protected final int yDim;

    public GameView(LinkedHashSet<PlayerView> players, LinkedHashSet<EnemyView> enemies, LinkedHashSet<ProjectileView> projectiles,
            LinkedHashSet<ItemDropView> itemDrops, LinkedHashSet<LaserView> lasers, LinkedHashSet<ExplosionView> explosions, TileView[][] tileMap,
            Team winningTeam) {
        this.players = players;
        this.enemies = enemies;
        this.projectiles = projectiles;
        this.itemDrops = itemDrops;
        this.lasers = lasers;
        this.explosions = explosions;
        this.tileMap = tileMap;
        this.xDim = tileMap.length;
        this.yDim = tileMap[0].length;
        this.winningTeam = winningTeam;
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

    public LinkedHashSet<LaserView> getLasers() {
        return lasers;
    }

    public LinkedHashSet<ExplosionView> getExplosions() {
        return explosions;
    }

    public TileView[][] getTileMap() {
        return tileMap;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

}
