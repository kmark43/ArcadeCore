package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.cooldown.CooldownManager;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import net.mutinies.arcadecore.games.paintball.gun.event.ExpUpdater;
import net.mutinies.arcadecore.games.paintball.gun.event.LaunchHandler;
import net.mutinies.arcadecore.games.paintball.gun.event.ShotRequirement;
import org.bukkit.entity.Player;

public class CooldownRequirement implements LaunchHandler, ShotRequirement, ExpUpdater {
    private String cooldownTag;
    private long cooldown;
    
    public CooldownRequirement(String cooldownTag, long cooldown) {
        this.cooldownTag = cooldownTag;
        this.cooldown = cooldown;
    }
    
    @Override
    public void onProjectileLaunch(Gun gun, Player player, ListeningProjectile projectile) {
        CooldownManager cooldownManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(CooldownManager.class);
        cooldownManager.checkAvailableOrStartCooldown(player, cooldownTag, cooldown);
    }
    
    @Override
    public boolean canShoot(Player player) {
        CooldownManager cooldownManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(CooldownManager.class);
        return cooldownManager.isAvailable(player, cooldownTag);
    }
    
    @Override
    public int getPriority() {
        return 5;
    }
    
    @Override
    public float getExp(Player player) {
        CooldownManager cooldownManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(CooldownManager.class);
        int timeLeft = cooldownManager.getTimeLeft(player, cooldownTag);
        return Math.max(0, Math.min(1, ((float)timeLeft) / cooldown));
    }
}
