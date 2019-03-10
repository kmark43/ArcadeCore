package net.mutinies.arcadecore.games.paintball.gun;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.games.paintball.gun.handler.*;
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
    private Map<String, Gun> gunNameToGunMap;
    private BukkitTask expTask;
    
    public GunModule(Game game) {
        this.game = game;
        gunTagToGunMap = new HashMap<>();
        gunNameToGunMap = new HashMap<>();
        createDefaultGuns();
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
        gunNameToGunMap.put(gun.getName(), gun);
        gunTagToGunMap.put(gun.getTag(), gun);
    }
    
    public Gun getGun(String gunName) {
        return gunNameToGunMap.get(gunName);
    }
    
    public void unregisterGun(String gunName) {
        Gun gun = gunNameToGunMap.remove(gunName);
        if (gun != null) {
            gunTagToGunMap.remove(gun.getTag());
        }
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
    
    private void createDefaultGuns() {
        // Rifle
        Gun rifle = new Gun("rifle", "Rifle", "gun_rifle", 500, ProjectileType.PELLET, 1, new StaticInitialVelocityDeterminer(3, .01));
        rifle.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(15))));
        rifle.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new SparceParticleHandler())));
        registerGun(rifle);
    
        // Shotgun
        Gun shotgun = new Gun("shotgun", "Shotgun", "gun_shotgun", 1400, ProjectileType.PELLET, 8, new StaticInitialVelocityDeterminer(1.5, .4));
        shotgun.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(5))));
        shotgun.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new SparceParticleHandler())));
        registerGun(shotgun);
        
        // Machine gun
        Gun machineGun = new Gun("machine_gun", "Machine Gun", "gun_machine_gun", 150, ProjectileType.PELLET, 1, new StaticInitialVelocityDeterminer(2.4, .25));
        machineGun.addListener(new OverheatingModule("machine_gun_heating", .97, 250, 0.025, 0.020));
        machineGun.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(5))));
        machineGun.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new SparceParticleHandler())));
        registerGun(machineGun);
        
        // Sniper
        Gun sniper = new Gun("sniper", "Sniper", "gun_sniper", 1400, ProjectileType.ARROW, 1, new StaticInitialVelocityDeterminer(10));
        sniper.addScopeHandler(new FreezeWhenScopedHandler());
        sniper.addListener(new ChargingScope(1000, 1, 4, 1));
        sniper.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new DenseParticleHandler())));
        registerGun(sniper);
        
        // Bazooka
        Gun bazooka = new Gun("bazooka", "Bazooka", "gun_bazooka", 5000, ProjectileType.FIREBALL, 1, new StaticInitialVelocityDeterminer(.8));
        bazooka.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new AccelerationHandler())));
        bazooka.addLaunchHandler(((gun, player, projectile) -> projectile.addHitHandler(new AreaOfEffectDamage())));
        bazooka.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new SparceParticleHandler())));
    
        registerGun(bazooka);
    
        // Needler
        Gun needler = new Gun("needler", "Needler", "gun_needler", 500, ProjectileType.ARROW, 1, new StaticInitialVelocityDeterminer(10));
        ConsecutiveHitCounter needlerHitCounter = new ConsecutiveHitCounter();
        needler.addListener(needlerHitCounter);
    
        needler.addLaunchHandler(((gun, player, projectile) -> {
            int hits = needlerHitCounter.getNumConsectuveHits(player);
            if (hits > 0 && hits % 3 == 0) {
                projectile.addDamageHandler(new StaticDamageHandler(10));
            } else {
                projectile.addDamageHandler(new StaticDamageHandler(5));
            }
        }));
    
        needler.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new DenseParticleHandler())));
        registerGun(needler);
        
        // Medic
        Gun medic = new Gun("medic", "Medic", "gun_medic", 1000, ProjectileType.PELLET, 4, new StaticInitialVelocityDeterminer(1.5, .6));
        medic.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(5))));
        medic.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticHealHandler(5))));
        medic.addLaunchHandler(new TargetChooser(game, true, false));
        medic.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new SparceParticleHandler())));
        registerGun(medic);
    }
}
