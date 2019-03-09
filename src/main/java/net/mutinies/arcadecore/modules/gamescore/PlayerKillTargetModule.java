package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerKillTargetModule extends SoloWinHandler implements Module {
    private Game game;
    private short target;

    private Map<UUID, Integer> scoreMap;

    public PlayerKillTargetModule(short target, Game game, boolean displayScoreboard) {
        this.game = game;
        this.target = target;
        if (displayScoreboard) {
            game.getScoreboardManager().setTitle("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + game.getDisplayName());
            game.getScoreboardManager().setLineFunction(player -> {
                List<String> lines = new ArrayList<>();
            
                List<UUID> sorted = scoreMap.keySet().stream()
                        .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                        .sorted(Comparator.comparingInt(scoreMap::get).reversed())
                        .collect(Collectors.toList());
            
                for (int i = 0; i < Math.min(sorted.size(), 15); i++) {
                    Player p = Bukkit.getPlayer(sorted.get(i));
                    int score = scoreMap.get(sorted.get(i));
                    lines.add("" + ChatColor.WHITE + score + " " + ChatColor.GREEN + p.getName());
                }
                return lines;
            });
        }
    }
    
    public void incrementScore(Player player) {
        int score = scoreMap.get(player.getUniqueId()) + 1;
        scoreMap.put(player.getUniqueId(), score);

        if (score >= target) {
            game.getGameStateManager().stop();
        }
    }

    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        if (e.getKiller() != null && e.getKiller() instanceof Player) {
            incrementScore((Player) e.getKiller());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(GameDeathEvent e) {
        checkEnoughPlayers();
    }
    
    private void checkEnoughPlayers() {
        if (game.getTeamManager().getLivingPlayers().size() <= 1) {
            game.getGameStateManager().stop();
        }
    }
    
    @Override
    public void enable() {
        scoreMap = new HashMap<>();
        game.getTeamManager().getLivingPlayers().forEach(player -> scoreMap.put(player.getUniqueId(), 0));
        checkEnoughPlayers();
    }

    @Override
    public void disable() {
        scoreMap = null;
    }

    @Override
    public List<Player> getRankedPlayers() {
        return scoreMap.keySet().stream()
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .sorted(Comparator.comparingInt(scoreMap::get).reversed())
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }
}
