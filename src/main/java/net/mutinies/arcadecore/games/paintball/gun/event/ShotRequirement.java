package net.mutinies.arcadecore.games.paintball.gun.event;

import org.bukkit.entity.Player;

public interface ShotRequirement {
    boolean canShoot(Player player);
}
