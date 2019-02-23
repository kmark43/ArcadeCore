package net.mutinies.arcadecore.game.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageInstance {
    private EntityDamageEvent.DamageCause cause;
    private Entity causer;
    private long time;
    
    public DamageInstance(EntityDamageEvent.DamageCause cause, Entity causer) {
        this.cause = cause;
        this.causer = causer;
        this.time = System.currentTimeMillis();
    }
    
    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
    
    public Entity getCauser() {
        return causer;
    }
    
    public long getTime() {
        return time;
    }
}
