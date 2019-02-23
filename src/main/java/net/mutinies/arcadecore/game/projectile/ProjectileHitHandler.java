package net.mutinies.arcadecore.game.projectile;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileHitHandler {
    void onProjectileHit(ListeningProjectile projectile, ProjectileHitEvent projectileHitEvent);
}
