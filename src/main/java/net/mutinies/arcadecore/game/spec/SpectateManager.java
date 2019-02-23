package net.mutinies.arcadecore.game.spec;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.kit.Kit;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpectateManager implements Module {
    private Game game;
    private Set<UUID> spectators;
    
    public SpectateManager(Game game) {
        this.game = game;
    }
    
    @Override
    public void enable() {
        spectators = new HashSet<>();
    }
    
    @Override
    public void disable() {
        for (UUID spectator : spectators) {
            Player player = Bukkit.getPlayer(spectator);
            if (player != null) {
                showPlayer(player);
            }
        }
        spectators = null;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        for (UUID spectatorId : spectators) {
            Player spectator = Bukkit.getPlayer(spectatorId);
            e.getPlayer().hidePlayer(spectator);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        spectators.remove(e.getPlayer().getUniqueId());
    }
    
    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }
    
    public void spectatePlayer(Player player) {
        spectators.add(player.getUniqueId());
        hidePlayer(player);
        setSpectateState(player);
    }
    
    public void unspectatePlayer(Player player) {
        setRunningState(player);
        showPlayer(player);
        spectators.remove(player.getUniqueId());
    }
    
    private void showPlayer(Player player) {
        player.spigot().setCollidesWithEntities(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
    }
    
    private void hidePlayer(Player player) {
        player.spigot().setCollidesWithEntities(false);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);
        }
    }
    
    private void setSpectateState(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setLevel(0);
        player.setLastDamageCause(null);
        player.setLastDamage(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.getInventory().setContents(new ItemStack[36]);
        player.teleport(game.getMapManager().getCurrentMap().getMainSpawn().getLocation());
        player.setVelocity(new Vector(0, 0, 0));
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(effect.getType());
        }
        player.setExp(0);
    }
    
    private void setRunningState(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.setLastDamageCause(null);
        player.setLastDamage(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setExp(0);
        Kit kit = game.getKitManager().getKit(player);
        kit.giveItems(player);
    }
}
