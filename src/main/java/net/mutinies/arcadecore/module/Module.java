package net.mutinies.arcadecore.module;

import org.bukkit.event.Listener;

public interface Module extends Listener {
    default void enable() {}
    default void disable() {}
}
