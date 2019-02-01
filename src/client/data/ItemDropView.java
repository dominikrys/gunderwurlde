package client.data;

import data.Pose;
import data.entity.item.ItemDrop;

public class ItemDropView extends EntityView {
    protected ItemDrop name;

    public ItemDropView(Pose pose, int size, ItemDrop name) {
        super(pose, size);
        this.name = name;
    }

    public ItemDrop getName() {
        return name;
    }

}
