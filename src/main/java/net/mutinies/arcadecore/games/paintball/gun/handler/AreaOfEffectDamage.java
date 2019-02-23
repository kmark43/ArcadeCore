package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileHitHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AreaOfEffectDamage implements ProjectileHitHandler {
    @Override
    public void onProjectileHit(ListeningProjectile projectile, ProjectileHitEvent projectileHitEvent) {
        Game game = ArcadeCorePlugin.getInstance().getGameManager().getGame();
        
        for (Entity entity : getNearbyEntities(projectile.getProjectile().getLocation(), 5)) {
            if (entity instanceof Player) {

                
                Player damagee = ((Player) entity);
                Player shooter = (Player) projectile.getProjectile().getShooter();
                
                if (game.getSpectateManager().isSpectator(damagee) ||
                        game.getTeamManager().getTeam(damagee).equals(game.getTeamManager().getTeam(shooter))) {
                    continue;
                }
            
                Vector trajectory = getTrajectory(damagee.getLocation(), projectile.getProjectile().getLocation());
                double damage;
                double distance = Math.min(damagee.getLocation().distance(((Player) projectile.getProjectile().getShooter()).getLocation()), damagee.getEyeLocation().distance(((Player) projectile.getProjectile().getShooter()).getLocation()));
                if (distance <= 2) {
                    damage = 20;
                } else if (distance <= 3) {
                    damage = 15;
                } else if (distance <= 4) {
                    damage = 10;
                } else {
                    damage = 5;
                }
                
                game.getDamageManager().damage(damagee, damage, projectile.getProjectile(), EntityDamageEvent.DamageCause.PROJECTILE);
            }
        }
    }
    
    private Collection<Entity> getNearbyEntities(Location location, double radius) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity.getLocation().distanceSquared(location) <= radius * radius) {
                entities.add(entity);
            }
        }
        return entities;
    }
    
    private Vector getTrajectory(Location damagedPosition, Location shooterPosition) {
        return damagedPosition.clone().subtract(shooterPosition).toVector().setY(0).normalize();
    }
}
