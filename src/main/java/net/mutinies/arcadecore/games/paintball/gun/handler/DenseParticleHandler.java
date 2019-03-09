package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.projectile.FlightTickHandler;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

public class DenseParticleHandler implements FlightTickHandler {
    private Location lastLocation;
    
    @Override
    public void onFlightTick(ListeningProjectile projectile) {
        // todo combine with sparce particle handler and make a single one that can specify spread between particles
        if (lastLocation == null) {
            lastLocation = projectile.getOrigin();
        }
        
        Color color = getParticleColor(projectile);
        int maxT = (int) projectile.getProjectile().getLocation().distance(lastLocation);
        Location loc = lastLocation.clone();
        Vector dLoc = projectile.getProjectile().getLocation().clone().subtract(lastLocation).toVector().normalize();
        for (int t = 0; t < maxT; t++) {
//                        world.playEffect(loc, Effect.FIREWORKS_SPARK, data);
            ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), loc, color);
            loc.add(dLoc);
        }
        lastLocation = projectile.getProjectile().getLocation();
    
    }
    
    private Color getParticleColor(ListeningProjectile projectile) {
        Player shooter = (Player)projectile.getProjectile().getShooter();
        GameTeam team = ArcadeCorePlugin.getGame().getTeamManager().getTeam(shooter);
        return team.getColor().getColor();
    }
}
