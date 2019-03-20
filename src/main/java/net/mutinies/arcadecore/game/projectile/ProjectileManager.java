package net.mutinies.arcadecore.game.projectile;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectileManager implements Module {
    private Game game;
    private Map<UUID, ListeningProjectile> projectileMap;
    private BukkitTask flightTask;
    
    public ProjectileManager(Game game) {
        this.game = game;
    }
    
    @Override
    public void enable() {
        projectileMap = new HashMap<>();
        flightTask = Bukkit.getScheduler().runTaskTimer(ArcadeCorePlugin.getInstance(), this::handleFlightTick, 1, 1);
    }
    
    @Override
    public void disable() {
        flightTask.cancel();
        flightTask = null;
        projectileMap = null;
    }
    
    public boolean isRegistered(Projectile projectile) {
        return projectile == null || projectileMap.containsKey(projectile.getUniqueId());
    }
    
    public ListeningProjectile getListeningProjectile(Projectile projectile) {
        if (projectile != null) {
            return projectileMap.get(projectile.getUniqueId());
        } else {
            return null;
        }
    }
    
    public void registerProjectile(ListeningProjectile projectile) {
        UUID uuid = projectile.getProjectile().getUniqueId();
        projectileMap.put(uuid, projectile);
    }
    
    private void handleFlightTick() {
        for (ListeningProjectile projectile : projectileMap.values()) {
            for (FlightTickHandler handler : projectile.getFlightHandlers()) {
                handler.onFlightTick(projectile);
            }
        }
    }
    
    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent e) {
        if (projectileMap.containsKey(e.getDamager().getUniqueId())) {
            ListeningProjectile projectile = projectileMap.get(e.getDamager().getUniqueId());
            projectile.getDamageHandlers().forEach(handler -> handler.onProjectileDamage(projectile, e));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent e) {
        if (projectileMap.containsKey(e.getEntity().getUniqueId())) {
            ListeningProjectile projectile = projectileMap.get(e.getEntity().getUniqueId());
            projectile.getProjectileHitHandlers().forEach(handler -> handler.onProjectileHit(projectile, e));
            Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> projectileMap.remove(e.getEntity().getUniqueId()));
        }
        e.getEntity().remove();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHitBlock(ProjectileHitBlockEvent e) {
        if (projectileMap.containsKey(e.getProjectile().getUniqueId())) {
            ListeningProjectile projectile = projectileMap.get(e.getProjectile().getUniqueId());
            projectile.getProjectileHitBlockHandlers().forEach(handler -> handler.onProjectileHitBlock(projectile, e));
            Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> projectileMap.remove(e.getProjectile().getUniqueId()));
        }
    }
    
    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        if (projectileMap.containsKey(uuid) && projectileMap.get(uuid) instanceof PotionProjectile) {
            PotionProjectile projectile = (PotionProjectile)projectileMap.get(e.getEntity().getUniqueId());
            projectile.getPotionSplashHandlers().forEach(handler -> handler.onPotionSplash(projectile, e));
        }
    }
}
