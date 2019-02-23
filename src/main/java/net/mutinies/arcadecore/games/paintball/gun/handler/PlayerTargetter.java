package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.FlightTickHandler;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class PlayerTargetter implements FlightTickHandler {
    private final int MAX_RUN_COUNT = 30;
    
    private Game game;
    private Player target;
    private boolean cancelled;
    private int runCount;
    private Vector initialVelocity;
    
    public PlayerTargetter(Game game, Player target, Vector initialVelocity) {
        this.game = game;
        this.target = target;
        this.initialVelocity = initialVelocity;
        cancelled = false;
        runCount = 0;
    }
    
    @Override
    public void onFlightTick(ListeningProjectile proj) {
        if (cancelled ||
                !target.isOnline() ||
                game.getSpectateManager().isSpectator((Player) proj.getProjectile().getShooter()) ||
                proj.getProjectile().isDead()) {
            cancelled = true;
            return;
        }
    
        Projectile projectile = proj.getProjectile();
        Vector offset = target.getEyeLocation().toVector().subtract(projectile.getLocation().toVector());
    
        Vector currentVelocity = projectile.getVelocity();
    
        Vector horizontalVelocity = currentVelocity.clone().setY(0);
        Vector horizontalOffset = offset.clone().setY(0);
    
        Vector velocity = projectile.getVelocity();
    
        if (initialVelocity.clone().setY(0).dot(horizontalVelocity) < 0) {
            cancelled = true;
            return;
        }
    
        double targetAngle = (Math.atan2(-offset.getX(), offset.getZ()) + Math.PI * 2) % (Math.PI * 2);
    
        double mag = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
        double angle = (Math.atan2(-velocity.getX(), velocity.getZ()) + Math.PI * 2) % (Math.PI * 2);
    
        if (Math.abs(angle - targetAngle) > Math.PI) {
            if (angle > targetAngle) {
                angle -= 2 * Math.PI;
            } else {
                targetAngle -= 2 * Math.PI;
            }
        }
    
        double percentage = Math.min(.9, (double)runCount / MAX_RUN_COUNT);
        angle = angle * (1 - percentage) + targetAngle * percentage;
        velocity.setX(-mag * Math.sin(angle));
        velocity.setZ(mag * Math.cos(angle));
    
        double timeToDist = Math.sqrt(horizontalOffset.lengthSquared() / horizontalVelocity.lengthSquared());
    
        double targetYVelocity = (offset.getY() + 9.81 / 20 / 2 * Math.pow(timeToDist, 2)) / timeToDist;
        double yVelocity = velocity.getY();
        double yPercentage = .40;
        yVelocity = Math.min(yVelocity, yVelocity * (1 - yPercentage) + targetYVelocity * yPercentage);
        velocity.setY(yVelocity);
    
        projectile.setVelocity(velocity);
    
        runCount++;
    }
}
