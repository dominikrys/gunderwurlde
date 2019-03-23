package shared.lists;

/**
 * Ammolist enum. Contains the different types of ammo in the game
 */
public enum AmmoList implements IsItem {
    BASIC_AMMO, SHOTGUN_ROUND, ENERGY, NONE/* Use none for items with infinite ammo */;

    @Override
    public ItemType getItemType() {
        return ItemType.AMMO;
    }

    @Override
    public ItemList getItemListName() {
        return ItemList.valueOf(this.toString());
    }
}
