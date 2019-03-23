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
    protected LinkedHashSet<PlayerView> players;
    protected LinkedHashSet<EnemyView> enemies;
    protected LinkedHashSet<ProjectileView> projectiles;
    protected LinkedHashSet<ItemDropView> itemDrops;
    protected TileView[][] tileMap;
    protected Team winningTeam; // NONE if nobody has won yet
    protected int xDim;
    protected int yDim;

    public GameView(LinkedHashSet<PlayerView> players, LinkedHashSet<EnemyView> enemies, LinkedHashSet<ProjectileView> projectiles,
            LinkedHashSet<ItemDropView> itemDrops, TileView[][] tileMap, Team winningTeam) {
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

    public Team getWinningTeam() {
        return winningTeam;
    }

}
