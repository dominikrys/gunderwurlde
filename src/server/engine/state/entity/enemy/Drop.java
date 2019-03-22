package server.engine.state.entity.enemy;

import java.util.Random;

import server.engine.state.item.Item;

public class Drop {
    
    private static Random rand = new Random();

    protected final double dropChance; // 0 to 1, higher value is higher drop chance
    protected final int minDrop;
    protected final int maxDrop;
    protected Item item;

    public Drop(Item item, double dropChance, int maxDrop, int minDrop) {
        assert (minDrop >= 0);
        assert (dropChance <= 1 && dropChance > 0);
        if (minDrop > maxDrop)
            maxDrop = minDrop;
        this.dropChance = dropChance;
        this.maxDrop = maxDrop;
        this.minDrop = minDrop;
        this.item = item;
    }

    public Drop(Item item, double dropChance, int dropAmount) {
        this(item, dropChance, dropAmount, dropAmount);
    }

    public int getDrop() {
        double val = rand.nextDouble();
        if (val <= dropChance) {
            if (maxDrop == minDrop)
                return minDrop;
            else {
                val = rand.nextInt(maxDrop - minDrop);
                return (int) (val + minDrop);
            }
        } else
            return 0;
    }
    
    public Item getItem() {
        return item;
    }
}
