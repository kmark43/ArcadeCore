package net.mutinies.arcadecore.games.paintball.gun.event;

import org.bukkit.entity.Player;

public interface ScopeHandler {
    void onScope(Player player);
    void onUnscope(Player player);
}
