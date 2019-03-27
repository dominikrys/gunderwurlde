package server.engine.state.item.consumable;

import java.util.LinkedList;

import server.engine.state.entity.projectile.LiveGrenade;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.CreatesProjectiles;
import shared.Pose;
import shared.lists.ItemList;
import shared.lists.ItemType;
import shared.lists.Team;

public class Grenade extends Consumable implements CreatesProjectiles {
    public static final ItemList NAME = ItemList.GRENADE;
    public static final int DEFAULT_MAX_QUANTITY = 3;

    public Grenade() {
        this(1);
    }

    public Grenade(int quantity) {
        super(NAME, ItemType.CONSUMEABLE, ConsumableType.PROJECTILE, DEFAULT_MAX_QUANTITY, quantity);
    }

    @Override
    public LinkedList<Projectile> getProjectiles(Pose origin, Team team, int desiredDistance) {
        quantity--;
        LinkedList<Projectile> grenades = new LinkedList<>();
        grenades.add(new LiveGrenade(desiredDistance).createFor(origin, team));
        return grenades;
    }

}
