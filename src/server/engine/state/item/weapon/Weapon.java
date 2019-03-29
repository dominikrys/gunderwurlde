package server.engine.state.item.weapon;

import server.engine.state.item.Item;
import shared.lists.ItemList;
import shared.lists.ItemType;

/**
 * Class for Weapons
 * 
 * @author Richard
 *
 */
public abstract class Weapon extends Item {

    public Weapon(ItemList weaponName) {
        super(weaponName, ItemType.GUN);
    }

}
