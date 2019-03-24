package client.input;

import javafx.scene.image.Image;
import server.engine.state.map.tile.Tile;
import shared.view.TileView;

public class Collision {
	
	private TileView[][] tileMap;
	private Image pImage;
	private int pWidth = 32;		// TODO: get image width and height
	private int pHeight = 32;
	
	public Collision(TileView[][] tileMap, Image pImage) {
		this.tileMap = tileMap;
		this.pImage = pImage;
	}
	
	/*
	private void getBoundary(GameMap map) {
		TileList[][] tileMap = map.getTileMap();
		ArrayList<Integer> columns = new ArrayList<Integer>();
		for(int row = 0 ; row < map.getXDim() ; row++) {
			columns = new ArrayList<Integer>();
			for(int column = 0 ; column < map.getYDim() ; column++) {
				if(tileMap[row][column].getState().toString().equals("SOLID")) {
					columns.add(column);
					System.out.println(column);
				}
			}
			if(!columns.isEmpty()) {
				obstacle.put(row, columns);
			}
		}
	}
	*/
	
	public boolean checkBoundary(int toGoX, int toGoY) {
        // System.out.println("toGoX: " + toGoX + " " + "toGoY: " + toGoY);
		int[] topLeft = checkTile(toGoX - pWidth/2, toGoY - pHeight/2);
        // System.out.println("topLeft: " + Arrays.toString(topLeft));
		int[] topRight = checkTile(toGoX + pWidth/2, toGoY - pHeight/2);
        // System.out.println("topRight: " + Arrays.toString(topRight));
		int[] downLeft = checkTile(toGoX - pWidth/2, toGoY + pHeight/2);
        // System.out.println("downLeft: " + Arrays.toString(downLeft));
		int[] downRight = checkTile(toGoX + pWidth/2, toGoY + pHeight/2);
        // System.out.println("downRight: " + Arrays.toString(downRight));
		if(tileMap[topLeft[0]][topLeft[1]].getTileState().toString().equals("SOLID") ||
				tileMap[topRight[0]][topRight[1]].getTileState().toString().equals("SOLID") ||
				tileMap[downLeft[0]][downLeft[1]].getTileState().toString().equals("SOLID") ||
				tileMap[downRight[0]][downRight[1]].getTileState().toString().equals("SOLID")) {
			return false;
		}
		return true;
	}
	
	private int[] checkTile(double x, double y) {
		int[] tile = {(int) (x/Tile.TILE_SIZE), (int) (y/Tile.TILE_SIZE)};
		return tile;
	}

}
