package net.mutinies.arcadecore.games.oitq.event;

import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import org.bukkit.entity.Player;

public interface LaunchHandler {
    void onProjectileLaunch(Player player, ListeningProjectile projectile);
}
