package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NoFriendlyFireModule implements Module {
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        
        GameManager gameManager = ArcadeCorePlugin.getGameManager();
        Game game = gameManager.getGame();
        
        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();
//        if (gameManager.getParticipationManager())
        if (game.getTeamManager().getTeam(player).equals(game.getTeamManager().getTeam(damager))) {
            e.setCancelled(true);
        }
    }
}
