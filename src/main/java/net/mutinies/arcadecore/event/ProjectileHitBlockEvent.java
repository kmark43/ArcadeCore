package net.mutinies.arcadecore.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProjectileHitBlockEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Projectile projectile;
    private Block hitBlock;
    
    public ProjectileHitBlockEvent(Projectile projectile, Block hitBlock) {
        this.projectile = projectile;
        this.hitBlock = hitBlock;
    }
    
    public Projectile getProjectile() {
        return projectile;
    }
    
    public Block getHitBlock() {
        return hitBlock;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
