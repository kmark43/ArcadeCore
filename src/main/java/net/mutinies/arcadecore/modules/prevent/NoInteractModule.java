package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoInteractModule implements Module {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }
    
    // todo more
}
