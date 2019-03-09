package net.mutinies.arcadecore.games.paintball.event;

import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileHitBlockHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class BreakBlockModule implements ProjectileHitBlockHandler {
    private double radius;
    
    public BreakBlockModule(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void onProjectileHitBlock(ListeningProjectile projectile, ProjectileHitBlockEvent projectileHitBlockEvent) {
        for (Block block : getInRadius(projectileHitBlockEvent.getHitBlock().getLocation(), radius)) {
            block.setType(Material.AIR, false);
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
