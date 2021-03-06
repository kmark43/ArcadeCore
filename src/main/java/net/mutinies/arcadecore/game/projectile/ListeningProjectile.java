package net.mutinies.arcadecore.game.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListeningProjectile {
    private List<FlightTickHandler> flightHandlers;
    private List<ProjectileDamageHandler> damageHandlers;
    private List<ProjectileHitHandler> projectileHitHandlers;
    private List<ProjectileHitBlockHandler> projectileHitBlockHandlers;
    private Entity shooter;
    private ItemStack shootingItem;
    private Projectile projectile;
    private Location origin;
    private Vector initialVelocity;
    private long launchTime;
    
    public ListeningProjectile(Projectile projectile, Entity shooter) {
        this(projectile);
        this.shooter = shooter;
        if (shooter instanceof Player) {
            shootingItem = ((Player) shooter).getItemInHand();
        }
    }
    
    public ListeningProjectile(Projectile projectile) {
        this.projectile = Objects.requireNonNull(projectile);
        this.origin = projectile.getLocation();
        this.initialVelocity = projectile.getVelocity();
        this.launchTime = System.currentTimeMillis();
        
        flightHandlers = new ArrayList<>();
        damageHandlers = new ArrayList<>();
        projectileHitHandlers = new ArrayList<>();
        projectileHitBlockHandlers = new ArrayList<>();
    }
    
    public Projectile getProjectile() {
        return projectile;
    }
    
    public Location getOrigin() {
        return origin;
    }
    
    public Vector getInitialVelocity() {
        return initialVelocity;
    }
    
    public long getLaunchTime() {
        return launchTime;
    }
    
    public Entity getShooter() {
        return shooter;
    }
    
    public ItemStack getShootingItem() {
        return shootingItem;
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
    
    public List<ProjectileHitBlockHandler> getProjectileHitBlockHandlers() {
        return new ArrayList<>(projectileHitBlockHandlers);
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
    
    public void addHitBlockHandler(ProjectileHitBlockHandler hitBlockHandler) {
        projectileHitBlockHandlers.add(hitBlockHandler);
    }
}
