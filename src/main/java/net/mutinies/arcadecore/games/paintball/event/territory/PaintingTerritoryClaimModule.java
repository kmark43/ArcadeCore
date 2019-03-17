package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.territory.Territory;
import net.mutinies.arcadecore.modules.territory.TerritoryClaimEvent;
import net.mutinies.arcadecore.modules.territory.TerritoryModule;
import net.mutinies.arcadecore.modules.territory.TerritoryUnclaimEvent;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;

public class PaintingTerritoryClaimModule implements Module {
    private Game game;
    private TerritoryModule territoryModule;
    
    public PaintingTerritoryClaimModule(Game game, TerritoryModule territoryModule) {
        this.game = game;
        this.territoryModule = territoryModule;
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.BOOLEAN, "neutralize_territories_first", true));
    }
    
    @EventHandler
    public void onProjectileHitBlock(ProjectileHitBlockEvent e) {
        if (e.getProjectile() instanceof ThrownPotion) return;
        if (!(e.getProjectile().getShooter() instanceof Player)) return;
        Player shooter = (Player)e.getProjectile().getShooter();
        GameTeam team = game.getTeamManager().getTeam(shooter);
        DyeColor dyeColor = team.getColor().getDyeColor();
        
        Territory territory = territoryModule.getTerritory(e.getHitBlock());
        if (territory == null) return;
        
        GameTeam owningTeam = territory.getOwningTeam();
        
        Block block = e.getHitBlock();
        BlockState state = block.getState();
        MaterialData data = state.getData();
        
        if (block.equals(territory.getCenterLocation().getBlock())) return;
        
        switch (data.getItemType()) {
            case WOOL:
            case STAINED_CLAY:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case CARPET:
                if ((boolean) game.getConfigManager().getProperty("neutralize_territories_first").getValue()) {
                    if (block.getData() == DyeColor.WHITE.getData() || block.getData() == team.getColor().getDyeColor().getData()) {
                        block.setData(dyeColor.getData());
                    } else {
                        block.setData(DyeColor.WHITE.getData());
                    }
                } else {
                    block.setData(dyeColor.getData());
                }
                break;
        }
        
        if (owningTeam == null) {
            BlockFace[] relatives = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
            Block center = territory.getCenterLocation().getBlock();
            
            boolean allMatch = true;
            for (BlockFace relative : relatives) {
                Block b = center.getRelative(relative);
                DyeColor bColor = DyeColor.getByData(b.getData());
                if (!bColor.equals(dyeColor)) {
                    allMatch = false;
                    break;
                }
            }
            
            if (allMatch) {
                territory.claim(team);
            }
        } else if (!owningTeam.equals(team)) {
            if ((boolean) game.getConfigManager().getProperty("neutralize_territories_first").getValue()) {
                BlockFace[] relatives = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
                Block center = territory.getCenterLocation().getBlock();
                
                boolean noneMatch = true;
                for (BlockFace relative : relatives) {
                    Block b = center.getRelative(relative);
                    DyeColor bColor = DyeColor.getByData(b.getData());
                    if (bColor.equals(owningTeam.getColor().getDyeColor())) {
                        noneMatch = false;
                        break;
                    }
                }
                
                if (noneMatch) {
                    territory.unclaim();
                }
            } else {
                territory.unclaim();
            }
        }
    }
    
    @EventHandler
    public void onTerritoryClaim(TerritoryClaimEvent e) {
        colorCenter(e.getTerritory(), e.getTeam().getColor().getDyeColor());
    }
    
    @EventHandler
    public void onTerritoryUnclaim(TerritoryUnclaimEvent e) {
        colorCenter(e.getTerritory(), DyeColor.WHITE);
    }
    
    private void colorCenter(Territory territory, DyeColor color) {
        Block block = territory.getCenterLocation().getBlock();
        block.setData(color.getData());
    }
}
