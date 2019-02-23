package net.mutinies.arcadecore.game.team;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.damage.DamageManager;
import net.mutinies.arcadecore.util.MutiniesColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameTeam {
    private String name;
    private String displayName;
    private MutiniesColor color;
    private Set<UUID> players;
    
    public GameTeam(String name, String displayName, MutiniesColor color) {
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        players = new HashSet<>();
        // todo set up friendly fire and other scoreboard properties
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public MutiniesColor getColor() {
        return color;
    }
    
    void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }
    
    void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }
    
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }
    
    public Set<Player> getLivingPlayers() {
        DamageManager damageManager = ArcadeCorePlugin.getInstance().getGameManager().getGame().getDamageManager();
        return players.stream()
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer)
                .filter(damageManager::isAlive)
                .collect(Collectors.toSet());
    }
}
