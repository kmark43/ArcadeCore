package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class PaintBlockModule implements Module {
    private Game game;
    private double radius;
    
    public PaintBlockModule(Game game, double radius) {
        this.game = game;
        this.radius = radius;
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof ThrownPotion) return;
        
        if (game.getProjectileManager().isRegistered(e.getEntity())) {
            Player shooter = (Player)e.getEntity().getShooter();
            GameTeam shooterTeam = game.getTeamManager().getTeam(shooter);
            
            DyeColor dyeColor = shooterTeam.getColor().getDyeColor();
            
            Location loc = e.getEntity().getLocation().clone().add(e.getEntity().getVelocity().normalize().multiply(.1));
    
            for (Block block : getInRadius(e.getEntity().getLocation(), radius)) {
                switch (block.getType()) {
                    case CLAY:
                    case HARD_CLAY:
                        block.setType(Material.STAINED_CLAY);
                        break;
                    case GLASS:
                        block.setType(Material.STAINED_GLASS);
                        break;
                    case THIN_GLASS:
                        block.setType(Material.STAINED_GLASS_PANE);
                        break;
                }
                MaterialData data = block.getState().getData();
                switch (data.getItemType()) {
                    case WOOL:
                    case STAINED_CLAY:
                    case STAINED_GLASS:
                    case STAINED_GLASS_PANE:
                    case CARPET:
                        block.setData(dyeColor.getData());
                        break;
                }
            }
            loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.STAINED_CLAY.getId() | dyeColor.getWoolData() << 12);
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
