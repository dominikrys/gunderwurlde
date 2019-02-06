package client.data;

import java.io.Serializable;

import data.Pose;
import data.entity.item.ItemList;

public class ItemDropView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected ItemList name;

    public ItemDropView(Pose pose, int size, ItemList name) {
        super(pose, size);
        this.name = name;
    }

    public ItemList getName() {
        return name;
    }

}
