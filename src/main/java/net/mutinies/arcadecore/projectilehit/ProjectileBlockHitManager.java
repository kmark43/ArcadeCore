package net.mutinies.arcadecore.projectilehit;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProjectileBlockHitManager implements Manager {
    private Set<UUID> damagingProjectiles;
    
    public ProjectileBlockHitManager() {
        damagingProjectiles = new HashSet<>();
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Location hitLocation = e.getEntity().getLocation();
        Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> {
            if (!damagingProjectiles.remove(e.getEntity().getUniqueId())) {
                World world = e.getEntity().getWorld();
                BlockIterator bi = new BlockIterator(world, hitLocation.toVector(), e.getEntity().getVelocity().normalize(), 0, 4);
                Block hit = null;
                
                while (bi.hasNext()) {
                    hit = bi.next();
                    if (hit.getType() != Material.AIR) {
                        break;
                    }
                }
                if (hit != null) {
                    Bukkit.getPluginManager().callEvent(new ProjectileHitBlockEvent(e.getEntity(), hit));
                }
            }
        });
    }
    
    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile) {
            damagingProjectiles.add(e.getDamager().getUniqueId());
        }
    }
}
