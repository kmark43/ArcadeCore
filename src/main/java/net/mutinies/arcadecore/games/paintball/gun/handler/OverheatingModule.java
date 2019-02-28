package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.cooldown.CooldownManager;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import net.mutinies.arcadecore.games.paintball.gun.event.ExpUpdater;
import net.mutinies.arcadecore.games.paintball.gun.event.GunListener;
import net.mutinies.arcadecore.games.paintball.gun.event.LaunchHandler;
import net.mutinies.arcadecore.games.paintball.gun.event.ShotRequirement;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OverheatingModule implements LaunchHandler, ExpUpdater, ShotRequirement, GunListener {
    private String cooldownTag;
    private double maxExp;
    private long cooldownDelay;
    private double shotHeatIncrease;
    private double cooldownTickDecrease;
    
    private Map<UUID, Double> expMap;
    
    public OverheatingModule(String cooldownDelayTag, double maxExp, long cooldownDelay, double shotHeatIncrease, double cooldownTickDecrease) {
        this.cooldownTag = cooldownDelayTag;
        this.maxExp = maxExp;
        this.cooldownDelay = cooldownDelay;
        this.shotHeatIncrease = shotHeatIncrease;
        this.cooldownTickDecrease = cooldownTickDecrease;
    }
    
    @Override
    public void register(Gun gun) {
        gun.addShotRequirement(this);
        gun.addLaunchHandler(this);
        gun.addExpUpdater(this);
    }
    
    @Override
    public void enable() {
        expMap = new HashMap<>();
    }
    
    @Override
    public void cleanup() {
        expMap = null;
    }
    
    @Override
    public boolean canShoot(Player player) {
        return expMap.getOrDefault(player.getUniqueId(), 0d) <= maxExp;
    }
    
    @Override
    public void onProjectileLaunch(Gun gun, Player player, ListeningProjectile projectile) {
        if (!expMap.containsKey(player.getUniqueId())) {
            expMap.put(player.getUniqueId(), 0d);
        }
        CooldownManager cooldownManager = ArcadeCorePlugin.getManagerHandler().getManager(CooldownManager.class);
        cooldownManager.setCooldown(player, cooldownTag, cooldownDelay);
        expMap.put(player.getUniqueId(), expMap.get(player.getUniqueId()) + shotHeatIncrease);
    }
    
    @Override
    public int getPriority() {
        return 4;
    }
    
    @Override
    public float getExp(Player player) {
        UUID uuid = player.getUniqueId();
        if (expMap.containsKey(uuid)) {
            CooldownManager cooldownManager = ArcadeCorePlugin.getManagerHandler().getManager(CooldownManager.class);
            if (cooldownManager.isAvailable(player, cooldownTag)) {
                expMap.put(uuid, Math.max(0, Math.min(1, expMap.get(player.getUniqueId()) - cooldownTickDecrease)));
            }
            return (float) expMap.get(uuid).doubleValue();
        }
        return 0;
    }
}
