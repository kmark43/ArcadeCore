package net.mutinies.arcadecore.game.damage;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface DamageManager extends Module {
    /*
    die if you leave
    pb
        can join, pick kit if at least n players
        quit - if damaged in past 10 seconds, get killed
        rejoin, if was alive and more than n players, respawn where you were
        rejoin, if was dead, your body respawns where it was
        
        new player joins mid-game
        button in inv with option to join smaller team at spawnpoint if both have at least n players
        
        player from game who quit at start rejoins
        rejoin alive if both teams have at least n, rejoin with dead body where they were otherwise
        
        player from game who quit after dying rejoins
        rejoin with dead body where they were
        
        player who was spectated because they were afk comes back and wants to unspec mid-game
        
        
        player who was in game dies and does /spec -f
        
        
        player at start of game does /spec, wants to unspec later
     */
    
//    void kill(Player player);
    //    void setHealth(Player player);
    boolean isAlive(Player player);
    void respawn(Player player);
    void damage(Player player, double damage, Entity damager, EntityDamageEvent.DamageCause cause);
}
