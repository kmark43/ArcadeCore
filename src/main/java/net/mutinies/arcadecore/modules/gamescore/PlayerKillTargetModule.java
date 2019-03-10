package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.event.GameStateSetEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.state.GameStateManager;
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
                
                List<Player> sorted = getRankedPlayers();
            
                for (int i = 0; i < Math.min(sorted.size(), 15); i++) {
                    Player p = sorted.get(i);
                    if (p == null) continue;
                    
                    int score = scoreMap.getOrDefault(sorted.get(i).getUniqueId(), 0);
                    lines.add("" + ChatColor.WHITE + score + " " + ChatColor.GREEN + p.getName());
                }
                return lines;
            });
        }
    }
    
    @Override
    public void enable() {
        scoreMap = new HashMap<>();
        ArcadeCorePlugin.getGameManager().getParticipationManager().getParticipants().forEach(p -> scoreMap.put(p.getUniqueId(), 0));
    }
    
    @Override
    public void disable() {
        scoreMap = null;
    }
    
    public void incrementScore(Player player) {
        if (!scoreMap.containsKey(player.getUniqueId())) {
            scoreMap.put(player.getUniqueId(), 0);
        }
        
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
        if (ArcadeCorePlugin.getGameManager().getParticipationManager().getParticipants().size() <= 1) {
            game.getGameStateManager().stop();
        }
    }
    
    @EventHandler
    public void onGameStart(GameStateSetEvent e) {
        if (e.getNewState() == GameStateManager.GameState.RUNNING) {
            checkEnoughPlayers();
        }
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
