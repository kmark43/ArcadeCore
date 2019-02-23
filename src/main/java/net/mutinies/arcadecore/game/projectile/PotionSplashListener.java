package net.mutinies.arcadecore.game.projectile;

import org.bukkit.event.entity.PotionSplashEvent;

public interface PotionSplashListener {
    void onPotionSplash(PotionProjectile projectile, PotionSplashEvent potionSplashEvent);
}
