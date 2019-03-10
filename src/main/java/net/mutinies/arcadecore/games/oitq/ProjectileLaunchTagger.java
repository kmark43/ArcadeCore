package net.mutinies.arcadecore.games.oitq;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.games.oitq.event.LaunchHandler;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.ArrayList;
import java.util.List;

public class ProjectileLaunchTagger implements Module {
    private List<LaunchHandler> launchHandlers;
    
    public ProjectileLaunchTagger() {
        launchHandlers = new ArrayList<>();
    }
    
    public void registerLaunchHandler(LaunchHandler launchHandler) {
        launchHandlers.add(launchHandler);
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) e.getEntity().getShooter();
        
        ListeningProjectile projectile = new ListeningProjectile(e.getEntity());
        ArcadeCorePlugin.getGame().getProjectileManager().registerProjectile(projectile);
        
        for (LaunchHandler launchHandler : launchHandlers) {
            launchHandler.onProjectileLaunch(player, projectile);
        }
    }
}
