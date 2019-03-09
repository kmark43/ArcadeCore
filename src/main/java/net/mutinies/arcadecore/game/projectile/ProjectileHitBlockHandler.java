package net.mutinies.arcadecore.game.projectile;

import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;

public interface ProjectileHitBlockHandler {
    void onProjectileHitBlock(ListeningProjectile projectile, ProjectileHitBlockEvent projectileHitBlockEvent);
}
