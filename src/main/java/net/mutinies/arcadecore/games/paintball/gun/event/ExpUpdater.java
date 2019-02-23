package net.mutinies.arcadecore.games.paintball.gun.event;

import org.bukkit.entity.Player;

public interface ExpUpdater {
    int getPriority();
    float getExp(Player player);
}
