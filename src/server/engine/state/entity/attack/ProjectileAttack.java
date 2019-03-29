package server.engine.state.entity.attack;

import java.util.LinkedList;

import server.engine.state.entity.projectile.Projectile;

/**
 * Projectile Attack class.
 * 
 * @author Richard
 *
 */
public class ProjectileAttack implements Attack {
    protected LinkedList<Projectile> projectiles;

    public ProjectileAttack(LinkedList<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public LinkedList<Projectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public AttackType getAttackType() {
        return AttackType.PROJECTILE;
    }

}
