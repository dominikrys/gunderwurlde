package inputhandler;

import java.util.Iterator;
import java.util.LinkedHashSet;

import client.data.ItemDropView;
import client.data.ItemView;
import client.data.PlayerView;

public class PickItem extends Action{
	
	private PlayerView playerView;
	private LinkedHashSet<ItemDropView> itemDropView;

	public PickItem(PlayerView playerView, LinkedHashSet<ItemDropView> itemDropView) {
		super(playerView);
		this.playerView = playerView;
		this.itemDropView = itemDropView;
	}
	
	public void checkPick() {
		ItemDropView nearestItemDrop = findNearestItemDrop(itemDropView);
		System.out.println("nearestItemDrop x:" + nearestItemDrop.getPose().getX());
		System.out.println("nearestItemDrop y:" + nearestItemDrop.getPose().getY());
		System.out.println("nearestItemDrop name:" + nearestItemDrop.getName().toString());
	}
	
	public ItemDropView findNearestItemDrop(LinkedHashSet<ItemDropView> itemDropView) {
		ItemDropView nearestItemDrop = null;
		double distance = 0;
		for(ItemDropView idv : itemDropView) {
			if(nearestItemDrop == null) {
				nearestItemDrop = idv;
				distance = getDistance(idv);
			}
			if(getDistance(idv) < distance) {
				nearestItemDrop = idv;
				distance = getDistance(idv);
			}
		}
		return nearestItemDrop;
	}
	
	private double getDistance(ItemDropView idv) {
		double distance = 0;
		double xDiff = playerView.getPose().getX() - idv.getPose().getX();
		double yDiff = playerView.getPose().getY() - idv.getPose().getY();
		distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		return distance;
	}
	
	public void pick() {
		if(playerView.getItems().size() != 5) {
			// TODO: send pick request
		}
	}

}
