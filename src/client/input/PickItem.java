package client.input;

import client.Client;
import client.GameHandler;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;

import java.util.LinkedHashSet;

public class PickItem extends Action {

    private Client handler;
    private PlayerView playerView;
    private LinkedHashSet<ItemDropView> itemDropView;

    public PickItem(Client handler, PlayerView playerView, LinkedHashSet<ItemDropView> itemDropView) {
        super(handler, playerView);
        this.handler = handler;
        this.playerView = playerView;
        this.itemDropView = itemDropView;
    }

    public void checkPick() {
        if (!itemDropView.isEmpty()) {
            ItemDropView nearestItemDrop = findNearestItemDrop(itemDropView);
            // System.out.println("nearestItemDrop x:" + nearestItemDrop.getPose().getX());
            // System.out.println("nearestItemDrop y:" + nearestItemDrop.getPose().getY());
            // System.out.println("nearestItemDrop name:" +
            // nearestItemDrop.getEntityListName().toString());
        }
    }

    public ItemDropView findNearestItemDrop(LinkedHashSet<ItemDropView> itemDropView) {
        ItemDropView nearestItemDrop = null;
        double distance = 0;
        for (ItemDropView idv : itemDropView) {
            if (nearestItemDrop == null) {
                nearestItemDrop = idv;
                distance = getDistance(idv);
            }
            if (getDistance(idv) < distance) {
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
        if (playerView.getItems().size() != 5) {
            // TODO: send pick request
        }
    }

}
