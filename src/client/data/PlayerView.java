package client.data;


import java.util.ArrayList;

import data.Pose;

public class PlayerView extends EntityView {
    protected int health;
    protected int maxHealth;
    protected ArrayList<ItemView> items;
    protected int currentItemIndex;
    protected int score;
    protected String name;

    protected PlayerView(Pose pose, int size, int health, int maxHealth, ArrayList<ItemView> items, int currentItemIndex, int score, String name) {
        super(pose, size);
        this.pathToGraphic = "file:assets/img/mobs/player.png";
        this.health = health;
        this.maxHealth = health;
        this.items = items;
        this.currentItemIndex = currentItemIndex;
        this.score = score;
        this.name = name;
    }  

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public ItemView getCurrentItem() {
        return items.get(currentItemIndex);
    }

    public ArrayList<ItemView> getItems() {
        return items;
    }

}
