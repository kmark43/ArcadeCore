package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GamePreDeathEvent;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.mutinies.arcadecore.util.MessageUtil.getColoredName;

public class PaintDeathMessageModule implements Module  {
    @EventHandler
    public void onPreDeath(GamePreDeathEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile p = (Projectile)e.getDirectDamager();
            ListeningProjectile projectile = ArcadeCorePlugin.getGame().getProjectileManager().getListeningProjectile(p);
            if (projectile == null) return;
            
            String playerAddon = "";
            String gunAddon = "";
            String distanceSuffix = "";
            
            if (e.getLastDamagerOrPlayer() != null) {
                playerAddon = " by " + getColoredName(e.getLastDamagerOrPlayer()) + MessageUtil.DEFAULT;
            }
            
            if (projectile.getShootingItem() != null) {
                String itemName = ChatColor.stripColor(projectile.getShootingItem().getItemMeta().getDisplayName());
                gunAddon = " with " + MessageUtil.VARIABLE + itemName + MessageUtil.DEFAULT;
            }
            
            if (projectile.getOrigin() != null) {
                double distance = projectile.getOrigin().distance(e.getKilled().getLocation());
                int roundedDist = (int)Math.ceil(distance);
                String blockOrBlocks = roundedDist == 1 ? "block" : "blocks";
                distanceSuffix = " (" + roundedDist + " " + blockOrBlocks + ")";
            }
    
            e.setDeathMessage(getColoredName(e.getKilled()) + MessageUtil.DEFAULT + " was painted" + playerAddon + gunAddon + distanceSuffix + ".");
        }
    }
}
