package net.mutinies.arcadecore.games.paintball.gun;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.games.paintball.gun.event.*;
import net.mutinies.arcadecore.games.paintball.gun.handler.CooldownRequirement;
import org.bukkit.Sound;
import org.bukkit.entity.*;

import java.util.*;

public class Gun {
    private String name;
    private String displayName;
    private String tag;
    
    private ProjectileType projectileType;
    
    private List<ShotRequirement> shotRequirements;
    
    private List<GunListener> listeners;
    
    private List<LaunchHandler> launchHandlers;
    private List<ScopeHandler> scopeHandlers;
    private List<ExpUpdater> expUpdaters;
    private InitialVelocityDeterminer initialVelocityDeterminer;
    private int numBullets;
    
    private Set<UUID> scopedPlayers;
    
    public Gun(String name, String displayName, String tag, int cooldown, ProjectileType projectileType, int numBullets, InitialVelocityDeterminer initialVelocityDeterminer) {
        this.name = name;
        this.displayName = displayName;
        this.tag = tag;
        this.projectileType = projectileType;
        this.numBullets = numBullets;
        this.initialVelocityDeterminer = initialVelocityDeterminer;
        
        shotRequirements = new ArrayList<>();
        listeners = new ArrayList<>();
        launchHandlers = new ArrayList<>();
        scopeHandlers = new ArrayList<>();
        expUpdaters = new ArrayList<>();
        scopedPlayers = new HashSet<>();
    
        CooldownRequirement cooldownRequirement = new CooldownRequirement(name + "_shot", cooldown);
        addShotRequirement(cooldownRequirement);
        addLaunchHandler(cooldownRequirement);
        addExpUpdater(cooldownRequirement);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void addListener(GunListener listener) {
        listener.register(this);
        listeners.add(listener);
    }
    
    public void onEnable() {
        for (GunListener listener : listeners) {
            listener.enable();
        }
    }
    
    public void onDisable() {
        for (GunListener listener : listeners) {
            listener.cleanup();
        }
    }
    
    public void addShotRequirement(ShotRequirement requirement) {
        shotRequirements.add(requirement);
    }
    
    public void addLaunchHandler(LaunchHandler handler) {
        launchHandlers.add(handler);
    }
    
    public void addScopeHandler(ScopeHandler handler) {
        scopeHandlers.add(handler);
    }
    
    public void addExpUpdater(ExpUpdater updater) {
        int index = Collections.binarySearch(expUpdaters, updater, Comparator.comparingInt(ExpUpdater::getPriority));
        if (index < 0) {
            index = -index - 1;
        }
        expUpdaters.add(index, updater);
    }
    
    public void scope(Player player) {
        if (!scopedPlayers.contains(player.getUniqueId())) {
            scopedPlayers.add(player.getUniqueId());
            for (ScopeHandler handler : scopeHandlers) {
                handler.onScope(player);
            }
        }
    }
    
    public void unscope(Player player) {
        if (scopedPlayers.contains(player.getUniqueId())) {
            for (ScopeHandler handler : scopeHandlers) {
                handler.onUnscope(player);
            }
            scopedPlayers.remove(player.getUniqueId());
        }
    }
    
    public void updateExp(Player player) {
        float exp = 0;
    
        for (int i = 0; i < expUpdaters.size() && exp == 0; i++) {
            exp = expUpdaters.get(i).getExp(player);
        }
        
        player.setExp(exp);
    }
    
    public void shoot(Player player) {
        Game game = ArcadeCorePlugin.getInstance().getGameManager().getGame();

        for (ShotRequirement shotRequirement : shotRequirements) {
            if (!shotRequirement.canShoot(player)) {
                return;
            }
        }
        
        for (int i = 0; i < numBullets; i++) {
            Projectile projectile;
            switch (projectileType) {
                case PELLET:
                    projectile = player.launchProjectile(EnderPearl.class);
                    // Use 1.5f pitch if using a snowball instead
                    player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1.2f);
                    break;
                case ARROW:
                    projectile = player.launchProjectile(Arrow.class);
                    break;
                case FIREBALL:
                    projectile = player.launchProjectile(Fireball.class);
                    player.getWorld().playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.8f, .7f);
                    break;
                default:
                    return;
            }
            
            projectile.setVelocity(initialVelocityDeterminer.getInitialVelocity(player, projectile));
            
            ListeningProjectile listeners = new ListeningProjectile(projectile);
            
            game.getProjectileManager().registerProjectile(listeners);
    
            for (LaunchHandler launchHandler : launchHandlers) {
                launchHandler.onProjectileLaunch(this, player, listeners);
            }
        }
    }
}
