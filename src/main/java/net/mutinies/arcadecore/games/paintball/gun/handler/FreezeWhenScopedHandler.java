package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.games.paintball.gun.event.ScopeHandler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezeWhenScopedHandler implements ScopeHandler {
    @Override
    public void onScope(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 123, false, false));
    }
    
    @Override
    public void onUnscope(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW);
    }
}
