package net.mutinies.arcadecore.game.scoreboard;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.state.GameStateManager;
import net.mutinies.arcadecore.manager.Manager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.scoreboard.ProtocolScoreboardDisplay;
import net.mutinies.arcadecore.scoreboard.ScoreboardDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Function;

public class ScoreboardManager implements Manager, Module {
    private Game game;
    private Function<Player, List<String>> lineFunction;
    private Map<UUID, ScoreboardDisplay> displayMap;
    
    private String currentTitle;
    
    private BukkitTask scoreboardUpdater;
    private boolean enabled = false;
    
    public ScoreboardManager(Game game) {
        this.game = game;
        currentTitle = "";
    }
    
    @Override
    public void enable() {
        enabled = true;
        startUpdater();
    }
    
    @Override
    public void disable() {
        stopUpdater();
        enabled = false;
    }
    
    public void setTitle(String title) {
        this.currentTitle = Objects.requireNonNull(title);
        if (scoreboardUpdater != null) {
            for (ScoreboardDisplay value : displayMap.values()) {
                value.setTitle(currentTitle);
            }
        }
    }
    
    public void setLineFunction(Function<Player, List<String>> lineFunction) {
        this.lineFunction = lineFunction;
    }
    
    private void startUpdater() {
        if (scoreboardUpdater == null && shouldDisplay()) {
            displayMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                createDisplay(player);
            }
            scoreboardUpdater = Bukkit.getScheduler().runTaskTimer(ArcadeCorePlugin.getInstance(), this::updateDisplays, 2, 2);
        }
    }
    
    private void stopUpdater() {
        if (scoreboardUpdater != null) {
            scoreboardUpdater.cancel();
            scoreboardUpdater = null;
            
            for (UUID uuid : displayMap.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    displayMap.get(uuid).clear(player);
                }
            }
            displayMap = null;
        }
    }
    
    private boolean shouldDisplay() {
        return enabled &&
                game.getGameStateManager().getState() != GameStateManager.GameState.NOT_ACTIVE &&
                lineFunction != null;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (scoreboardUpdater != null) {
            Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> createDisplay(e.getPlayer()), 1);
        }
    }
    
    private void createDisplay(Player player) {
        ScoreboardDisplay display = new ProtocolScoreboardDisplay(currentTitle);
        display.show(player);
        displayMap.put(player.getUniqueId(), display);
        refreshDisplay(player);
    }
    
    public void updateDisplays() {
        for (UUID uuid : displayMap.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                refreshDisplay(player);
            }
        }
    }
    
    public void refreshDisplay(Player player) {
        Objects.requireNonNull(player);

        ScoreboardDisplay display = displayMap.get(player.getUniqueId());
        if (display != null && lineFunction != null) {
            display.update(lineFunction.apply(player));
        }
    }
}
