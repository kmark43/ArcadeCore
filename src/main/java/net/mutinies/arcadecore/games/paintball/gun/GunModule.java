package net.mutinies.arcadecore.games.paintball.gun;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.item.ClickEvent;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.manager.ManagerHandler;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class GunModule implements Module {
    private Game game;
    private Map<String, Gun> gunTagToGunMap;
    private BukkitTask expTask;
    
    public GunModule(Game game) {
        this.game = game;
        gunTagToGunMap = new HashMap<>();
    }
    
    @Override
    public void enable() {
        ManagerHandler managerHandler = ArcadeCorePlugin.getManagerHandler();
        for (String tag : gunTagToGunMap.keySet()) {
            managerHandler.getManager(ItemManager.class).registerTag(tag, clickEvent -> {
                if (clickEvent.getClickType() != ClickEvent.ClickType.RIGHT) return;
                if (game.getSpectateManager().isSpectator(clickEvent.getPlayer())) return;
                clickEvent.setCancelled(true);
                gunTagToGunMap.get(tag).shoot(clickEvent.getPlayer());
            });
            gunTagToGunMap.get(tag).onEnable();
        }

        expTask = Bukkit.getScheduler().runTaskTimer(ArcadeCorePlugin.getInstance(), this::updateExps, 1, 1);
    }
    
    @Override
    public void disable() {
        expTask.cancel();
        expTask = null;
        
        ManagerHandler managerHandler = ArcadeCorePlugin.getManagerHandler();
        
        for (String tag : gunTagToGunMap.keySet()) {
            managerHandler.getManager(ItemManager.class).unregister(tag);
            gunTagToGunMap.get(tag).onDisable();
        }
    }
    
    public void registerGun(Gun gun) {
        ManagerHandler managerHandler = ArcadeCorePlugin.getManagerHandler();
        gunTagToGunMap.put(gun.getTag(), gun);
    }
    
    @EventHandler
    public void preventFireballExplode(ExplosionPrimeEvent e) {
        if (e.getEntityType() == EntityType.FIREBALL) {
            e.setRadius(0);
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Gun heldGun = getHeldGun(e.getPlayer());
        if (heldGun == null) return;
        
        if (e.isSneaking()) {
            heldGun.scope(e.getPlayer());
        } else {
            heldGun.unscope(e.getPlayer());
        }
    }
    
    @EventHandler
    public void playerSwitchItemEvent(PlayerItemHeldEvent e) {
        Gun heldGun = getHeldGun(e.getPlayer());
        if (heldGun == null) return;
        heldGun.unscope(e.getPlayer());
    }
    
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e) {
        Gun heldGun = getHeldGun(e.getPlayer());
        if (heldGun == null) return;
        heldGun.unscope(e.getPlayer());
    }
    
    private void updateExps() {
        for (Player player : game.getTeamManager().getLivingPlayers()) {
            ItemStack heldItem = player.getItemInHand();
            String tag = ItemManager.getTag(heldItem);
            if (tag == null || !gunTagToGunMap.containsKey(tag)) {
                player.setExp(0);
                return;
            }
            Gun heldGun = gunTagToGunMap.get(tag);
            heldGun.updateExp(player);
        }
    }
    
    private Gun getHeldGun(Player player) {
        if (game.getSpectateManager().isSpectator(player)) return null;
        ItemStack heldItem = player.getItemInHand();
        String tag = ItemManager.getTag(heldItem);
        if (tag == null || !gunTagToGunMap.containsKey(tag)) return null;
        return gunTagToGunMap.get(tag);
    }
}
