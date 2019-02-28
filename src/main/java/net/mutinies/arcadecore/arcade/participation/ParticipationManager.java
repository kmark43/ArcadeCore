package net.mutinies.arcadecore.arcade.participation;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ParticipationManager implements Module {
    private Set<UUID> participants;
    private boolean joinByDefault;
    
    public ParticipationManager(boolean joinByDefault) {
        this.joinByDefault = joinByDefault;
    }
    
    @Override
    public void enable() {
        participants = new HashSet<>();
        if (joinByDefault) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                participants.add(player.getUniqueId());
            }
        }
    }
    
    @Override
    public void disable() {
        participants = null;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (joinByDefault) {
            participants.add(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        participants.remove(e.getPlayer().getUniqueId());
    }
    
    public boolean isParticipating(Player player) {
        return participants.contains(player.getUniqueId());
    }
    
    public void setParticipating(Player player, boolean participating) {
        if (participating) {
            participants.add(player.getUniqueId());
        } else {
            participants.remove(player.getUniqueId());
        }
    }
    
    public List<Player> getParticipants() {
        List<Player> players = new ArrayList<>();
        for (UUID participant : participants) {
            players.add(Bukkit.getPlayer(participant));
        }
        return players;
    }
}
