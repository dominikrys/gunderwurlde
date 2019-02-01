package client.data;

import data.Pose;
import data.entity.item.ItemDrop;

public class ItemDropView extends EntityView {
    protected ItemDrop name;

    protected ItemDropView(Pose pose, double size, ItemDrop name) {
        super(pose, size);
        this.name = name;
    }

    public ItemDrop getName() {
        return name;
    }

}
