package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileDamageHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class StaticDamageHandler implements ProjectileDamageHandler {
    private double damage;
    
    public StaticDamageHandler(double damage) {
        this.damage = damage;
    }
    
    @Override
    public void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent damageByEntityEvent) {
        damageByEntityEvent.setDamage(damage);
    }
}
