package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NoFriendlyFireModule implements Module {
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
    
        Player player = (Player) e.getEntity();
        Player damager = getDamager(e);
    
        if (damager == null) return;
    
        GameManager gameManager = ArcadeCorePlugin.getGameManager();
        Game game = gameManager.getGame();
        
        if (game.getTeamManager().getTeam(player).equals(game.getTeamManager().getTeam(damager))) {
            e.setCancelled(true);
        }
    }
    
    private Player getDamager(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            return (Player) e.getDamager();
        } else if (e.getDamager() instanceof Projectile) {
            Projectile damager = (Projectile) e.getDamager();
            if (damager.getShooter() instanceof Player) {
                return (Player) damager.getShooter();
            }
        }
        return null;
    }
}
