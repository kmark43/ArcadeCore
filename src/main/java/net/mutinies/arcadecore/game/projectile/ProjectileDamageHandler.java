package net.mutinies.arcadecore.game.projectile;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface ProjectileDamageHandler {
    void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent damageByEntityEvent);
}
