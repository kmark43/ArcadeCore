package net.mutinies.arcadecore.game.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


class ProjectileDamageEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Player damagee;
    private Entity shooter;
    private Projectile projectile;
    
    public ProjectileDamageEvent(Player damagee, Entity shooter, Projectile projectile) {
        this.damagee = damagee;
        this.shooter = shooter;
        this.projectile = projectile;
    }
    
    public Player getDamagee() {
        return damagee;
    }
    
    public Entity getShooter() {
        return shooter;
    }
    
    public Projectile getProjectile() {
        return projectile;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
