package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.util.BuildUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class PaintBlockModule implements Module {
    private Game game;
    
    public PaintBlockModule(Game game, double radius) {
        this.game = game;
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.DOUBLE, "block_paint_radius", radius));
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitBlockEvent e) {
        if (e.getProjectile() instanceof ThrownPotion) return;
        
        if (game.getProjectileManager().isRegistered(e.getProjectile())) {
            Player shooter = (Player)e.getProjectile().getShooter();
            GameTeam shooterTeam = game.getTeamManager().getTeam(shooter);
            
            DyeColor dyeColor = shooterTeam.getColor().getDyeColor();
            
            Location loc = e.getHitBlock().getLocation();
            
            double radius = (Double) game.getConfigManager().getProperty("block_paint_radius").getValue();
    
            Set<Block> blocks = getInRadius(e.getProjectile().getLocation(), radius);
            Bukkit.getPluginManager().callEvent(new PaintBlocksEvent(blocks));
            
            for (Block block : blocks) {
                switch (block.getType()) {
                    case CLAY:
                    case HARD_CLAY:
                        BuildUtil.setNMSBlock(block, Material.STAINED_CLAY, dyeColor.getData(), false);
                        break;
                    case GLASS:
                        BuildUtil.setNMSBlock(block, Material.STAINED_GLASS, dyeColor.getData(), false);
                        break;
                    case THIN_GLASS:
                        BuildUtil.setNMSBlock(block, Material.STAINED_GLASS_PANE, dyeColor.getData(), false);
                        break;
                    case WOOL:
                    case STAINED_CLAY:
                    case STAINED_GLASS:
                    case STAINED_GLASS_PANE:
                    case CARPET:
                        BuildUtil.setNMSBlock(block, block.getType(), dyeColor.getData(), false);
                        break;
                }
            }

            Location loc2 = loc.clone().add(.5, .5, .5).subtract(e.getProjectile().getVelocity().normalize().multiply(.1));

            MaterialData data = e.getHitBlock().getState().getData();
            switch (data.getItemType()) {
                case WOOL:
                case STAINED_CLAY:
                case STAINED_GLASS:
                case STAINED_GLASS_PANE:
                case CARPET:
                    loc2.getWorld().playEffect(loc2, Effect.STEP_SOUND, e.getHitBlock().getType().getId() | dyeColor.getWoolData() << 12);
                    break;
                default:
                    loc2.getWorld().playEffect(loc2, Effect.STEP_SOUND, e.getHitBlock().getType().getId());
                    break;
            }
        }
    }
    
    private Set<Block> getInRadius(Location loc, double radius) {
        Set<Block> blockList = new HashSet<>();
        int iR = (int)radius + 1;
        
        for (int x = -iR; x <= iR; x++) {
            for (int z = -iR; z <= iR; z++) {
                for (int y = -iR; y <= iR; y++) {
                    Vector off = new Vector(x + .5, y + .5, z + .5);
                    if (off.lengthSquared() <= radius * radius) {
                        Location temp = loc.clone().add(off);
                        blockList.add(new Location(loc.getWorld(),
                                (int) Math.floor(temp.getX()),
                                (int) Math.floor(temp.getY()),
                                (int) Math.floor(temp.getZ())).getBlock());
                    }
                }
            }
        }
        return blockList;
    }
}
