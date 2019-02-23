package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class NoHungerChangeModule implements Module {
    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
