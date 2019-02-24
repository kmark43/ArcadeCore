package net.mutinies.arcadecore.util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class PlayerUtil {
    public static void setDefaultPlayerState(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        clearInventory(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.setLastDamageCause(null);
        player.setLastDamage(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(effect.getType());
        }
        player.setExp(0);
    }
    
    public static void clearInventory(Player player) {
        player.getInventory().setContents(new ItemStack[36]);
        player.getInventory().setArmorContents(new ItemStack[4]);
    }
}
