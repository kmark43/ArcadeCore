package net.mutinies.arcadecore.event;

import net.mutinies.arcadecore.game.damage.DamageInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class GamePreDeathEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private Player killed;
    private Entity lastDamagerOrPlayer;
    private Set<Player> contributingPlayers;
    private String deathMessage;
    private EntityDamageEvent.DamageCause cause;
    private LinkedList<DamageInstance> causes;

    public GamePreDeathEvent(Player killed, Entity lastDamagerOrPlayer, Set<Player> contributingPlayers, String deathMessage, EntityDamageEvent.DamageCause cause, LinkedList<DamageInstance> causes) {
        this.killed = killed;
        this.lastDamagerOrPlayer = lastDamagerOrPlayer;
        this.contributingPlayers = contributingPlayers;
        this.deathMessage = deathMessage;
        this.cause = cause;
        this.causes = causes;
    }

    public Player getKilled() {
        return killed;
    }
    
    public Entity getLastDamagerOrPlayer() {
        return lastDamagerOrPlayer;
    }
    
    public Set<Player> getContributingPlayers() {
        return new LinkedHashSet<>(contributingPlayers);
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
    
    public Entity getDirectDamager() {
        return getLastDamageInstance().getCauser();
    }
    
    public DamageInstance getLastDamageInstance() {
        return causes.getFirst();
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
