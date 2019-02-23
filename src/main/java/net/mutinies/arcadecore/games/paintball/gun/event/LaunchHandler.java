package net.mutinies.arcadecore.games.paintball.gun.event;

import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import org.bukkit.entity.Player;

public interface LaunchHandler {
    void onProjectileLaunch(Gun gun, Player player, ListeningProjectile projectile);
}
