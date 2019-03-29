package net.mutinies.arcadecore.games.paintball.event.spleef;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import java.util.HashSet;
import java.util.Set;
import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileHitBlockHandler;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.util.BuildUtil;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BreakBlockModule implements ProjectileHitBlockHandler {
    private Game game;
    private double radius;
    private double outerRadius;
    
    public BreakBlockModule(Game game, double radius, double outerRadius) {
        this.game = game;
        this.radius = radius;
        this.outerRadius = outerRadius;
    }
    
    @Override
    public void onProjectileHitBlock(ListeningProjectile projectile, ProjectileHitBlockEvent e) {
        double radiusScale = (double)(game.getConfigManager().getProperty("radius_scale").getValue());

        Block block = e.getHitBlock();
        BlockState state = block.getState();
        MaterialData data = state.getData();

        FaweQueue queue = FaweAPI.createQueue(block.getWorld().getName(), false);

        Player shooter = (Player) projectile.getShooter();
        GameTeam shooterTeam = game.getTeamManager().getTeam(shooter);

        DyeColor dyeColor = shooterTeam.getColor().getDyeColor();

        Location loc = e.getHitBlock().getLocation();

        Set<Block> outerBlocks = getInRadius(e.getHitBlock().getLocation(), outerRadius * radiusScale);
        Set<Block> innerBlocks = getInRadius(e.getHitBlock().getLocation(), radius * radiusScale);

//        e.getHitBlock().setType(Material.AIR, false);

        switch (data.getItemType()) {
            case CLAY:
            case HARD_CLAY:
            case GLASS:
            case THIN_GLASS:
            case WOOL:
            case STAINED_CLAY:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case CARPET:
                BuildUtil.setNMSBlock(e.getHitBlock(), Material.AIR, (byte) 0, false);
        }

        outerBlocks.removeAll(innerBlocks);

        for (Block b : innerBlocks) {
            MaterialData d = b.getState().getData();
            switch (d.getItemType()) {
                case CLAY:
                case HARD_CLAY:
                case GLASS:
                case THIN_GLASS:
                case WOOL:
                case STAINED_CLAY:
                case STAINED_GLASS:
                case STAINED_GLASS_PANE:
                case CARPET:
                    queue.setBlock(b.getX(), b.getY(), b.getZ(), 0, 0);
                    break;
            }
        }

        for (Block b : outerBlocks) {
            switch (b.getType()) {
                case CLAY:
                case HARD_CLAY:
//                    BuildUtil.setNMSBlock(b, Material.STAINED_CLAY, dyeColor.getData(), false);
                    queue.setBlock(b.getX(), b.getY(), b.getZ(), Material.STAINED_CLAY.getId(), dyeColor.getWoolData());
                    break;
                case GLASS:
//                    BuildUtil.setNMSBlock(b, Material.STAINED_GLASS, dyeColor.getData(), false);
                    queue.setBlock(b.getX(), b.getY(), b.getZ(), Material.STAINED_GLASS.getId(), dyeColor.getWoolData());
                    break;
                case THIN_GLASS:
//                    BuildUtil.setNMSBlock(b, Material.STAINED_GLASS_PANE, dyeColor.getData(), false);
                    queue.setBlock(b.getX(), b.getY(), b.getZ(), Material.STAINED_GLASS_PANE.getId(), dyeColor.getWoolData());
                    break;
                case WOOL:
                case STAINED_CLAY:
                case STAINED_GLASS:
                case STAINED_GLASS_PANE:
                case CARPET:
//                    BuildUtil.setNMSBlock(b, b.getType(), dyeColor.getData(), false);
                    queue.setBlock(b.getX(), b.getY(), b.getZ(), b.getType().getId(), dyeColor.getWoolData());
                    break;
            }
        }
        queue.flush();
        Location loc2 = loc.clone().add(.5, .5, .5).subtract(e.getProjectile().getVelocity().normalize().multiply(.1));

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
