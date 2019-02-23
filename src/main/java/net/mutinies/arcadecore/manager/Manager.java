package net.mutinies.arcadecore.manager;

import org.bukkit.event.Listener;

public interface Manager extends Listener {
    default void enable() {}
    default void disable() {}
}
