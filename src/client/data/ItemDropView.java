package client.data;

import data.Pose;
import data.entity.item.ItemList;

public class ItemDropView extends EntityView {
    protected ItemList name;

    public ItemDropView(Pose pose, int size, ItemList name) {
        super(pose, size);
        this.name = name;
    }

    public ItemList getName() {
        return name;
    }

}
