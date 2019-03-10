package net.mutinies.arcadecore.games.oitq.event;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileDamageHandler;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ArrowBounceHandler implements ProjectileDamageHandler {
    private int bounces;
    private int maxBounces;
    private float maxPower;
    
    private Set<UUID> ignored;
    
    public ArrowBounceHandler(int maxBounces, float maxPower) {
        this.bounces = 0;
        this.maxBounces = maxBounces;
        this.maxPower = maxPower;
        ignored = new HashSet<>();
    }
    
    public void addIgnored(Player player) {
        ignored.add(player.getUniqueId());
    }
    
    @Override
    public void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent damageByEntityEvent) {
        if (!(projectile.getProjectile().getShooter() instanceof Player)) return;
        if (!(damageByEntityEvent.getEntity() instanceof Player)) return;
        bounces++;
        
        if (bounces > maxBounces) return;
        
        Player shooter = (Player) projectile.getProjectile().getShooter();
        Player hit = (Player)damageByEntityEvent.getEntity();
        
        addIgnored(hit);
        
        Player target = getClosest(damageByEntityEvent.getEntity().getLocation(), ignored);
        
        if (target == null) return;
        
        Vector trajectory = target.getLocation().toVector().subtract(hit.getLocation().toVector());
        
        double distanceApart = hit.getLocation().distance(target.getLocation());
        trajectory.add(new Vector(0, distanceApart / 100d, 0));
    
        float power = (float) (0.8 + distanceApart / 30d);
        if (maxPower > 0 && power > maxPower)
            power = maxPower;
    
        Arrow ent = hit.getWorld().spawnArrow(hit.getEyeLocation().add(trajectory), trajectory, power, 0f);
        ent.setShooter(shooter);
        
        ListeningProjectile listeningProjectile = new ListeningProjectile(ent);
        listeningProjectile.addDamageHandler(this);
        listeningProjectile.addDamageHandler(new InstantKillHandler());
        ArcadeCorePlugin.getGame().getProjectileManager().registerProjectile(listeningProjectile);
    }
    
    private Player getClosest(Location location, Set<UUID> ignored) {
        List<Player> players = ArcadeCorePlugin.getGame().getTeamManager().getLivingPlayers();
        Player closest = null;
        double shortestDistanceSquared = Integer.MAX_VALUE;
        for (Player player : players) {
            if (ignored.contains(player.getUniqueId())) continue;
            
            double distanceSquared = location.distanceSquared(player.getLocation());
            if (closest == null || distanceSquared < shortestDistanceSquared) {
                closest = player;
                shortestDistanceSquared = distanceSquared;
            }
        }
        return closest;
    }
}
