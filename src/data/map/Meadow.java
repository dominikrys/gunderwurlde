package data.map;

import java.util.ArrayList;

import data.Location;

public class Meadow extends GameMap{
	public static final int DEFAULT_X_DIM = 17;
	public static final int DEFAULT_Y_DIM = 17;

	public Meadow() {
		super(DEFAULT_X_DIM, DEFAULT_Y_DIM, generateTileMap(), generatePlayerSpawns(), generateEnemySpawns());
	}
	
	/* Map Meadow (17x17):
	 * 
	 * WWWWWWWWWWWWWWWWW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * GGGGGGGGGGGGGGGGG
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WGGGGGGGGGGGGGGGW
	 * WWWWWWWWWWWWWWWWW
	 */
	private static Tile[][] generateTileMap() {
		Tile[][] tileMap = new Tile[DEFAULT_X_DIM][DEFAULT_Y_DIM];
		
		for (int i = 0;i<DEFAULT_X_DIM;i++) {
			tileMap[i][0] = new Tile(TileList.WOOD, TileType.SOLID);
			for (int i2 =1;i<DEFAULT_Y_DIM;i2++) {
				if (i ==0 || i==DEFAULT_X_DIM-1) tileMap[i][i2] = new Tile(TileList.WOOD, TileType.SOLID);
				else tileMap[i][i2] = new Tile(TileList.GRASS, TileType.PASSABLE);
			}
			tileMap[i][DEFAULT_Y_DIM-1] = new Tile(TileList.WOOD, TileType.SOLID);			
		}
		tileMap[(DEFAULT_X_DIM-1)/2][0] = new Tile(TileList.GRASS, TileType.PASSABLE);
		tileMap[(DEFAULT_X_DIM-1)/2][DEFAULT_Y_DIM-1] = new Tile(TileList.GRASS, TileType.PASSABLE);
		return tileMap;
	}
	
	private static ArrayList<Location> generatePlayerSpawns() {
		ArrayList<Location> playerSpawns = new ArrayList<Location>();
		playerSpawns.add(Tile.tileToLocation(1, 1));
		playerSpawns.add(Tile.tileToLocation(1, DEFAULT_Y_DIM-1));
		playerSpawns.add(Tile.tileToLocation(DEFAULT_X_DIM-1, 1));
		playerSpawns.add(Tile.tileToLocation(DEFAULT_X_DIM-1, DEFAULT_Y_DIM-1));
		return playerSpawns;
	}
	
	private static ArrayList<Location> generateEnemySpawns() {
		ArrayList<Location> enemySpawns = new ArrayList<Location>();
		enemySpawns.add(Tile.tileToLocation((DEFAULT_X_DIM-1)/2, 0));
		enemySpawns.add(Tile.tileToLocation((DEFAULT_X_DIM-1)/2, DEFAULT_Y_DIM-1));
		return enemySpawns;
	}

}
