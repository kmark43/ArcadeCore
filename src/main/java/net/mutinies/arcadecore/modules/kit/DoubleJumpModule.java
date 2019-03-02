package net.mutinies.arcadecore.modules.kit;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.cooldown.CooldownManager;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DoubleJumpModule extends KitModule {
    private Set<UUID> falling;
    
    private double power;
    private double heightMax;
    private boolean control;
    private long recharge;
    
    public DoubleJumpModule(double power, double heightLimit, boolean control) {
        this.power = power;
        this.heightMax = heightLimit;
        this.control = control;
        this.recharge = 0;
    }
    
    public DoubleJumpModule(double power, double heightLimit, boolean control, long recharge) {
        this.power = power;
        this.heightMax = heightLimit;
        this.control = control;
        this.recharge = recharge;
    }
    
    @Override
    public void enable() {
        falling = new HashSet<>();
        Bukkit.getOnlinePlayers().stream().filter(this::inGameWithKit).forEach(player -> {
            player.setAllowFlight(true);
        });
    }
    
    @Override
    public void disable() {
//        Bukkit.getOnlinePlayers().stream().filter(this::inGameWithKit).forEach(player -> player.setAllowFlight(false));
        falling = null;
    }
    
    public static boolean isGrounded(Player ent) {
        return ((CraftEntity) ent).getHandle().onGround;
    }
    
    private void velocity(Player player, double str, double yAdd, double yMax, boolean groundBoost) {
        velocity(player, player.getLocation().getDirection(), str, false, 0, yAdd, yMax, groundBoost);
    }
    
    private void velocity(Player player, Vector vec, double str, boolean ySet, double yBase, double yAdd, double yMax, boolean groundBoost) {
        if (Double.isNaN(vec.getX()) || Double.isNaN(vec.getY()) || Double.isNaN(vec.getZ()) || vec.length() == 0)
            return;
        
        //YSet
        if (ySet)
            vec.setY(yBase);
        
        //Modify
        vec.normalize();
        vec.multiply(str);
        
        //YAdd
        vec.setY(vec.getY() + yAdd);
        
        //Limit
        if (vec.getY() > yMax)
            vec.setY(yMax);
        
        if (groundBoost)
            if (isGrounded(player))
                vec.setY(vec.getY() + 0.2);
        
        //Velocity
        player.setFallDistance(0);
        
        player.setVelocity(vec);
    }
    
    @EventHandler
    public void onPlayerFly(PlayerToggleFlightEvent e) {
        if (!inGameWithKit(e.getPlayer())) return;
    
        Player player = e.getPlayer();
        
        //Recharge
        if (recharge > 0) {
            CooldownManager manager = ArcadeCorePlugin.getManagerHandler().getManager(CooldownManager.class);
            if (!manager.checkAvailableOrStartCooldown(player, "double_jump", recharge)) {
                return;
            }
        }
        
        if (control) {
            velocity(player, power, 0.4, heightMax, false);
//            velocity(player, power, 0.2, heightMax, true);
        } else {
            velocity(player, player.getLocation().getDirection(), power, true, power, 0, heightMax, true);
        }
    
        //Sound
        player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
        
        e.getPlayer().setFlying(false);
        e.getPlayer().setAllowFlight(false);
        falling.add(e.getPlayer().getUniqueId());
        
        Vector velocity = e.getPlayer().getLocation().getDirection().normalize();
        velocity.multiply(2);
        velocity.setY(Math.abs(velocity.getY()) + .2);
        
        e.getPlayer().getWorld().playEffect(e.getPlayer().getLocation(), Effect.BLAZE_SHOOT, 0);
        e.getPlayer().setVelocity(velocity);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!inGameWithKit(e.getPlayer())) return;
        if (e.getPlayer().getAllowFlight()) return;
        
        Block block = e.getPlayer().getLocation().getBlock();
        if (block.getType() != Material.AIR || block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(false);
            falling.remove(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!falling.contains(e.getEntity().getUniqueId())) return;
        
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) e.getEntity();
            e.setCancelled(true);
            player.setAllowFlight(true);
            player.setFlying(false);
            falling.remove(e.getEntity().getUniqueId());
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(GameRespawnEvent e) {
        if (inGameWithKit(e.getPlayer())) {
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(false);
            falling.remove(e.getPlayer().getUniqueId());
        }
    }
}
