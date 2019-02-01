package client.data;

import data.entity.item.ItemList;

public class ItemView {
    protected ItemList name;
    protected String pathToGraphic;

    public ItemView(ItemList name) {
        this.name = name;

        switch (name) {
        case BASIC_AMMO:
            break;
        case PISTOL:
            this.pathToGraphic = "file:assets/img/items/pistol.png";
            break;
        }
    }

    public ItemList getName() {
        return name;
    }

    public String getPathToGraphic() {
        return pathToGraphic;
    }

}
