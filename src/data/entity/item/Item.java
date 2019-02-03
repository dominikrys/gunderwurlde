package data.entity.item;

import data.Constants;

public abstract class Item {
    protected final IsItem itemName;
    protected String pathToGraphic;

    protected Item(IsItem itemName) {
        this.itemName = itemName;    
        this.pathToGraphic = Constants.DEFAULT_GRAPHIC_PATH;
    }

    public ItemList getItemName() {
        return itemName.toItemList();
    }

    public ItemType getItemType() {
        return itemName.getItemType();
    }

    public String getPathToGraphic() {
        return pathToGraphic;
    }

}
