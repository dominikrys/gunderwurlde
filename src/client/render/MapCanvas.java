package client.render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import shared.Constants;
import shared.view.GameView;

public class MapCanvas extends Canvas {
    GraphicsContext mapGC;

    public MapCanvas(int width, int height) {
        super(width, height);
        mapGC = this.getGraphicsContext2D();
    }

    // Render map from tiles
    public void renderMap(GameView gameView, RendererResourceLoader rendererResourceLoader) {
        // Get map X and Y dimensions
        int mapX = gameView.getXDim();
        int mapY = gameView.getYDim();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tile graphic
                Image tileImage = rendererResourceLoader.getSprite(gameView.getTileMap()[x][y].getTileType().getEntityListName());

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE);
            }
        }
    }
}
