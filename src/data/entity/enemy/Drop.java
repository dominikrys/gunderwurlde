package data.entity.enemy;

import java.util.Random;

import data.item.Item;

public class Drop {
    private static int MAX_DROP_CHANCE = 10000;
    
    private static Random rand = new Random();

    protected final int dropChance; // 10000 to 1, higher value is higher drop chance
    protected final int minDrop;
    protected final int maxDrop;
    protected Item item;

    Drop(Item item, int dropChance, int maxDrop, int minDrop) {
        assert(minDrop >= 0);
        if (minDrop > maxDrop)
            maxDrop = minDrop;
        this.dropChance = dropChance;
        this.maxDrop = maxDrop;
        this.minDrop = minDrop;
        this.item = item;
    }

    Drop(Item item, int dropChance, int maxDrop) {
        this(item, dropChance, maxDrop, maxDrop);
    }

    public int getDrop() {
        int val = rand.nextInt(MAX_DROP_CHANCE);
        if (val <= dropChance) {
            if (maxDrop == minDrop)
                return minDrop;
            else {
                val = rand.nextInt(maxDrop - minDrop);
                return (val + minDrop);
            }
        } else
            return 0;
    }
    
    public Item getItem() {
        return item;
    }
}
