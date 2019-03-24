package server.engine.state.item.pickup;

import server.engine.state.item.Item;
import shared.lists.ItemList;
import shared.lists.ItemType;

public class Health extends Item {

    private Health(ItemList type) {
        super(type, ItemType.HEALTH);
    }

    public static Health makeHealth(int amount) {
        if (amount <= 1) {
            return new Health(ItemList.HEART_HALF);
        } else {
            return new Health(ItemList.HEART_FULL);
        }
    }

}
