package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileDamageHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SetDamageHandler implements ProjectileDamageHandler {
    private double damage;
    
    public SetDamageHandler(double damage) {
        this.damage = damage;
    }
    
    @Override
    public void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent damageByEntityEvent) {
        damageByEntityEvent.setDamage(damage);
    }
}
