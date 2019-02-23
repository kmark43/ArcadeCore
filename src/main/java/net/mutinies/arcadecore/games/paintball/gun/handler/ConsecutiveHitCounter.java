package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileDamageHandler;
import net.mutinies.arcadecore.game.projectile.ProjectileHitHandler;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import net.mutinies.arcadecore.games.paintball.gun.event.LaunchHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsecutiveHitCounter implements LaunchHandler, ProjectileDamageHandler, ProjectileHitHandler {
    private Map<UUID, Integer> hitMap = new HashMap<>();
    private Map<UUID, ListeningProjectile> lastBulletMap = new HashMap<>();
    
    @Override
    public void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent damageByEntityEvent) {
        UUID playerId = ((Player) projectile.getProjectile().getShooter()).getUniqueId();
        hitMap.put(playerId, hitMap.getOrDefault(playerId, 1) + 1);
        lastBulletMap.put(playerId, projectile);
    }
    
    @Override
    public void onProjectileHit(ListeningProjectile projectile, ProjectileHitEvent projectileHitEvent) {
        Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> {
            UUID playerId = ((Player) projectile.getProjectile().getShooter()).getUniqueId();
            if (!projectile.equals(lastBulletMap.get(playerId))) {
                hitMap.put(playerId, 1);
            }
        });
    }
    
    public int getNumConsectuveHits(Player shooter) {
        return hitMap.getOrDefault(shooter.getUniqueId(), 0);
    }
    
    @Override
    public void onProjectileLaunch(Gun gun, Player player, ListeningProjectile projectile) {
        int hits = hitMap.getOrDefault(player.getUniqueId(), 0);
        double damage;
        if (hits == 3) {
            hitMap.put(player.getUniqueId(), 0);
            damage = 10;
        } else {
            damage = 5;
        }
        projectile.addDamageHandler(new StaticDamageHandler(damage));
        projectile.addDamageHandler(this);
        projectile.addHitHandler(this);
    }
}
