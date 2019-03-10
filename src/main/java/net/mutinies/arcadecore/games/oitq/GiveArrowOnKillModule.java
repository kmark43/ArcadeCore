package net.mutinies.arcadecore.games.oitq;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class GiveArrowOnKillModule implements Module {
    @EventHandler
    public void onPlayerKill(GameDeathEvent e) {
        if (e.getKiller() instanceof Player) {
            Player player = (Player) e.getKiller();
            player.getInventory().addItem(new ItemStack(Material.ARROW));
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        ArcadeCorePlugin.getGame().getKitManager().getKit(e.getPlayer()).giveItems(e.getPlayer());
    }
}
