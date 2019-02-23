package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import net.mutinies.arcadecore.games.paintball.gun.event.LaunchHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.Optional;

public class TargetChooser implements LaunchHandler {
    private Game game;
    private boolean targetAllies;
    private boolean targetEnemies;
    
    public TargetChooser(Game game, boolean targetAllies, boolean targetEnemies) {
        this.game = game;
        this.targetAllies = targetAllies;
        this.targetEnemies = targetEnemies;
    }
    
    @Override
    public void onProjectileLaunch(Gun gun, Player player, ListeningProjectile projectile) {
        Vector initialVelocity = projectile.getProjectile().getVelocity();
        Player target = getTarget(player);
        if (target != null) {
            initialVelocity.multiply(4.0 / 3.0);
            projectile.getProjectile().setVelocity(initialVelocity);
            projectile.addFlightHandler(new PlayerTargetter(game, target, initialVelocity));
        }
    }
    
    private Player getTarget(Player player) {
        Optional<? extends Player> target = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !game.getSpectateManager().isSpectator(player))
                .filter(p -> {
                    GameTeam team = game.getTeamManager().getTeam(p);
                    boolean teamSame = game.getTeamManager().getTeam(player).getName().equals(team.getName());
                    return teamSame && targetAllies ||
                            !teamSame && targetEnemies;
                })
                .filter(p -> !p.equals(player))
                .filter(p -> Math.abs(getAngle(player, p)) < 15)
                .min(Comparator.comparingDouble(p -> getNormalizedTargetDistance(player, p)));
//                .min(Comparator.comparingDouble(p -> Math.abs(getAngle(player, p))));
        
        return target.orElse(null);
    }
    
    private double getAngle(Player player, Player p) {
        Location offset = p.getLocation().subtract(player.getLocation());
        double offsetAngle = (Math.toDegrees(Math.atan2(-offset.getX(), offset.getZ())) + 360) % 360;
        double yaw = (player.getLocation().getYaw() + 360) % 360;
        return ((offsetAngle - yaw + 180 + 360) % 360) - 180; // accounts for wrapping (eg 360 degrees - 0 degrees = 0)
    }
    
    private double getNormalizedTargetDistance(Player player, Player p) {
        Vector initialLocation = p.getEyeLocation().toVector();
        Vector initialVelocity = player.getLocation().getDirection().multiply(2);
        Vector offset = p.getLocation().subtract(player.getLocation()).toVector();
        
        Vector horizontalVelocity = initialVelocity.clone().setY(0);
        Vector horizontalOffset = offset.clone().setY(0);
        
        double timeToDist = Math.sqrt(horizontalOffset.lengthSquared() / horizontalVelocity.lengthSquared());
        Vector targetPosition = getTargetPosition(initialLocation, initialVelocity, timeToDist);
        
        return targetPosition.distance(p.getLocation().toVector()) / offset.length();
    }
    
    private Vector getTargetPosition(Vector initialLocation, Vector initialVelocity, double time) {
        final double GRAVITY = -9.81 / 20;
        double yAtDist = 1 / 2.0 * GRAVITY * Math.pow(time, 2) +
                initialVelocity.getY() * time + initialLocation.getY();
        
        return initialLocation.clone().add(initialVelocity.clone().setY(0).multiply(time)).setY(yAtDist);
    }
}
