package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.game.projectile.FlightTickHandler;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import org.bukkit.entity.Projectile;

public class AccelerationHandler implements FlightTickHandler {
    @Override
    public void onFlightTick(ListeningProjectile projectile) {
        Projectile fireball = projectile.getProjectile();
        long delta = (System.currentTimeMillis() - projectile.getLaunchTime()) / 50;
        fireball.setVelocity(projectile.getInitialVelocity().clone()
                .multiply(Math.pow(1.07, (delta - 5))));
    }
}
