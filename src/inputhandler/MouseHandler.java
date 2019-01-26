package inputhandler;

import data.GameState;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class MouseHandler {
	
	private Scene scene;
	private double mouseX;
	private double mouseY;
	private double mouseDegree = 0;
	double toRotate;
	
	public MouseHandler(Scene scene, GameState gameState) {
		this.scene = scene;
		
		scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseX = event.getSceneX();
				mouseY = event.getSceneY();
				System.out.println("scenex: " + event.getSceneX() + ", sceney: " + event.getSceneY());
				
				System.out.println("pCoordinateX: " + pCoordinate.getX() + ", pCoordinateY: " + pCoordinate.getY());
				toRotate = Math.toDegrees(Math.atan((mouseX - pCoordinate.getX())/(mouseY - pCoordinate.getY())));
				System.out.println("toRotate: " + toRotate);
				int quarter = Coordinate.quarter(pCoordinate, new Coordinate(mouseX, mouseY));
				System.out.println("quater: " + quarter);
				if(quarter == 1) {
					mouseDegree = 360 - toRotate;
				}
				else if(quarter == 2) {
					mouseDegree = 0 - toRotate;
				}
				else if(quarter == 3) {
					mouseDegree = 180 - toRotate;
				}
				else if(quarter == 4) {
					mouseDegree = 180 - toRotate;
				}
				if(mouseDegree == 360) {
					mouseDegree = 0;
				}
				playerDegree = mouseDegree;
				System.out.println("mouseDegree: " + mouseDegree);
				System.out.println("playerDegree: " + playerDegree);
			}
		});
		
	}
	
	public void handle() {
		pImageV.setRotate(toRotate);
		
	}

}
