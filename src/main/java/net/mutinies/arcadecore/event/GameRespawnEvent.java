package net.mutinies.arcadecore.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameRespawnEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private Player player;
    private Location respawnLocation;

    public GameRespawnEvent(Player player, Location respawnLocation) {
        this.player = player;
        this.respawnLocation = respawnLocation;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
