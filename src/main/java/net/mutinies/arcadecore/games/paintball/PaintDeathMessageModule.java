package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.event.GamePreDeathEvent;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.mutinies.arcadecore.util.MessageUtil.getColoredName;

public class PaintDeathMessageModule implements Module  {
    @EventHandler
    public void onPreDeath(GamePreDeathEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getLastPlayerDamager() != null) {
                e.setDeathMessage(getColoredName(e.getKilled()) + ChatColor.WHITE + " was painted by " + getColoredName(e.getLastPlayerDamager()) + ChatColor.WHITE + ".");
            } else {
                e.setDeathMessage(getColoredName(e.getKilled()) + ChatColor.WHITE + " was shot.");
            }
        }
    }
}
