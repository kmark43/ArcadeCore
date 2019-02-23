package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoNaturalChangesModule implements Module {
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerJump(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            e.setCancelled(true);
        }
    }
}
