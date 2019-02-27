package net.mutinies.arcadecore.games.paintball.gun.event;

import net.mutinies.arcadecore.games.paintball.gun.Gun;

public interface GunListener {
    void register(Gun gun);
    default void enable(){}
    default void cleanup(){}
}
