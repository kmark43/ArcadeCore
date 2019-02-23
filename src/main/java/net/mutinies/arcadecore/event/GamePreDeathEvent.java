package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.damage.DamageInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.LinkedList;

public class GamePreDeathEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private Player killed;
    private Entity lastDamager;
    private Entity lastPlayerDamager;
    private String deathMessage;
    private EntityDamageEvent.DamageCause cause;
    private LinkedList<DamageInstance> causes;

    public GamePreDeathEvent(Player killed, Entity lastDamager, Player lastPlayerDamager, String deathMessage, EntityDamageEvent.DamageCause cause, LinkedList<DamageInstance> causes) {
        this.killed = killed;
        this.lastDamager = lastDamager;
        this.lastPlayerDamager = lastPlayerDamager;
        this.deathMessage = deathMessage;
        this.cause = cause;
        this.causes = causes;
    }

    public Player getKilled() {
        return killed;
    }
    
    public Entity getLastDamager() {
        return lastDamager;
    }
    
    public Entity getLastPlayerDamager() {
        return lastPlayerDamager;
    }
    
    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    public String getDeathMessage() {
        return deathMessage;
    }
    
    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
    
    public LinkedList<DamageInstance> getCauses() {
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
