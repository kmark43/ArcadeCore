package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.games.paintball.PaintBlocksEvent;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.territory.Territory;
import net.mutinies.arcadecore.modules.territory.TerritoryModule;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;

public class PreventTerritoryPaintModule implements Module {
    private TerritoryModule territoryModule;
    
    public PreventTerritoryPaintModule(TerritoryModule territoryModule) {
        this.territoryModule = territoryModule;
    }
    
    @EventHandler
    public void onBlocksPainted(PaintBlocksEvent e) {
        for (Block block : new ArrayList<>(e.getBlocks())) {
            Territory territory = territoryModule.getTerritory(block);
            if (territory != null) {
                e.getBlocks().remove(block);
            }
        }
    }
}
