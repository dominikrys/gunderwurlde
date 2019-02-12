package shared.lists;

public enum AmmoList implements IsItem {
    BASIC_AMMO, NONE/* Use none for items with infinite ammo */, SHOTGUN_ROUND;

    @Override
    public ItemType getItemType() {
        return ItemType.AMMO;
    }

    @Override
    public ItemList getItemListName() {
        return ItemList.valueOf(this.toString());
    }
}
