package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import net.mutinies.arcadecore.games.paintball.gun.event.ExpUpdater;
import net.mutinies.arcadecore.games.paintball.gun.event.LaunchHandler;
import net.mutinies.arcadecore.games.paintball.gun.event.ScopeHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChargingScope implements ScopeHandler, LaunchHandler, ExpUpdater {
    private long chargeTime;
    private int minScopeDamage;
    private int maxScopeDamage;
    private int unscopedDamage;
    
    private Map<UUID, Long> scopeMap;
    
    public ChargingScope(long chargeTime, int minScopeDamage, int maxScopeDamage, int unscopedDamage) {
        this.chargeTime = chargeTime;
        this.minScopeDamage = minScopeDamage;
        this.maxScopeDamage = maxScopeDamage;
        this.unscopedDamage = unscopedDamage;
        scopeMap = new HashMap<>();
    }
    
    @Override
    public void onProjectileLaunch(Gun gun, Player player, ListeningProjectile projectile) {
        double damage;
        if (scopeMap.containsKey(player.getUniqueId())) {
            long currentTime = System.currentTimeMillis();
            long startTime = scopeMap.get(player.getUniqueId());
            long difference = currentTime - startTime;
            damage = 5 * Math.min(maxScopeDamage, Math.max(unscopedDamage, (int)((difference * maxScopeDamage - minScopeDamage + 1) / chargeTime)));
            gun.unscope(player);
        } else {
            damage = unscopedDamage * 5;
        }
        projectile.addDamageHandler(new StaticDamageHandler(damage));
    }
    
    @Override
    public void onScope(Player player) {
        scopeMap.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    @Override
    public void onUnscope(Player player) {
        scopeMap.remove(player.getUniqueId());
    }
    
    @Override
    public int getPriority() {
        return 3;
    }
    
    @Override
    public float getExp(Player player) {
        if (scopeMap.containsKey(player.getUniqueId())) {
            long scopeTime = scopeMap.get(player.getUniqueId());
            long currentTime = System.currentTimeMillis();
            return Math.max(0, Math.min(1, (float)(currentTime - scopeTime) / chargeTime));
        } else {
            return 0;
        }
    }
}
