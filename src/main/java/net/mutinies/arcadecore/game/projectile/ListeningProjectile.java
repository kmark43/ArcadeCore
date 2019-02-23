package net.mutinies.arcadecore.game.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListeningProjectile {
    private List<FlightTickHandler> flightHandlers;
    private List<ProjectileDamageHandler> damageHandlers;
    private List<ProjectileHitHandler> projectileHitHandlers;
    private Projectile projectile;
    private Location origin;
    private long launchTime;
    
    public ListeningProjectile(Projectile projectile) {
        this.projectile = Objects.requireNonNull(projectile);
        this.origin = projectile.getLocation();
        this.launchTime = System.currentTimeMillis();
        
        flightHandlers = new ArrayList<>();
        damageHandlers = new ArrayList<>();
        projectileHitHandlers = new ArrayList<>();
    }
    
    public Projectile getProjectile() {
        return projectile;
    }
    
    public Location getOrigin() {
        return origin;
    }
    
    public long getLaunchTime() {
        return launchTime;
    }
    
    public List<FlightTickHandler> getFlightHandlers() {
        return new ArrayList<>(flightHandlers);
    }
    
    public List<ProjectileDamageHandler> getDamageHandlers() {
        return new ArrayList<>(damageHandlers);
    }
    
    public List<ProjectileHitHandler> getProjectileHitHandlers() {
        return new ArrayList<>(projectileHitHandlers);
    }
    
    public void addFlightHandler(FlightTickHandler flightTickHandler) {
        flightHandlers.add(flightTickHandler);
    }
    
    public void addDamageHandler(ProjectileDamageHandler damageHandler) {
        damageHandlers.add(damageHandler);
    }
    
    public void addHitHandler(ProjectileHitHandler hitHandler) {
        projectileHitHandlers.add(hitHandler);
    }
}
