package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.damage.DamageInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameDeathEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private Player killed;
    private Entity killer;
    private String deathMessage;
    private Location deathLocation;
    private List<DamageInstance> causes;

    public GameDeathEvent(Player killed, Entity killer, String deathMessage, Location deathLocation, LinkedList<DamageInstance> causes) {
        this.killed = killed;
        this.killer = killer;
        this.deathMessage = deathMessage;
        this.deathLocation = deathLocation;
        this.causes = new ArrayList<>(causes);
    }

    public Player getKilled() {
        return killed;
    }

    public Entity getKiller() {
        return killer;
    }

    public String getDeathMessage() {
        return deathMessage;
    }
    
    public Location getDeathLocation() {
        return deathLocation;
    }
    
    public List<DamageInstance> getCauses() {
        return causes;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
