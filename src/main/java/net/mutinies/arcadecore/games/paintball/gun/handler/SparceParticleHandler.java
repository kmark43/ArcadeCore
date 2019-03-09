package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.projectile.FlightTickHandler;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.inventivetalent.particle.ParticleEffect;

public class SparceParticleHandler implements FlightTickHandler {
    private int ticks;
    
    @Override
    public void onFlightTick(ListeningProjectile projectile) {
        // todo combine with dense particle handler and make a single one that can specify spread between particles
        if (ticks % 3 == 0) {
            Color color = getParticleColor(projectile);
            ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), projectile.getProjectile().getLocation(), color);
        }
        ticks++;
    }
    
    private Color getParticleColor(ListeningProjectile projectile) {
        Player shooter = (Player)projectile.getProjectile().getShooter();
        GameTeam team = ArcadeCorePlugin.getGame().getTeamManager().getTeam(shooter);
        return team.getColor().getColor();
    }
}
