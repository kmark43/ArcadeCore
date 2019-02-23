package net.mutinies.arcadecore.games.paintball.gun.event;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public interface InitialVelocityDeterminer {
    Vector getInitialVelocity(Player player, Projectile projectile);
}
