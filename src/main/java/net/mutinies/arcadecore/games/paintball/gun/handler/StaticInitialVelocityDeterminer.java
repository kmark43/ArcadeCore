package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.games.paintball.gun.event.InitialVelocityDeterminer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class StaticInitialVelocityDeterminer implements InitialVelocityDeterminer {
    private double speed;
    private double variation;
    
    public StaticInitialVelocityDeterminer(double speed) {
        this(speed, 0);
    }
    
    public StaticInitialVelocityDeterminer(double speed, double variation) {
        this.speed = speed;
        this.variation = variation;
    }
    
    @Override
    public Vector getInitialVelocity(Player player, Projectile projectile) {
        Vector rand = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
        rand.multiply(variation);
        Vector velocity = player.getLocation().getDirection().normalize();
        velocity.multiply(speed).add(rand);
        return velocity;
    }
}
