package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.event.GameEndCheckEvent;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.event.GameStateSetEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.state.GameStateManager;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;

public class PlayerEliminationModule extends SoloWinHandler implements Module {
    private Set<UUID> orderedDeaths;

    private Game game;

    public PlayerEliminationModule(Game game, boolean displayScoreboard) {
        this.game = game;
        if (displayScoreboard) {
            game.getScoreboardManager().setTitle("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + game.getDisplayName());
            game.getScoreboardManager().setLineFunction(player -> {
                List<String> lines = new ArrayList<>();
            
                lines.add("");
                lines.add("" + game.getTeamManager().getLivingPlayers().size() + ChatColor.GREEN + " Alive");
                return lines;
            });
        }
    }
    
    @Override
    public void enable() {
        orderedDeaths = new LinkedHashSet<>();
    }

    @EventHandler
    public void onGameStart(GameStateSetEvent e) {
        if (e.getNewState() == GameStateManager.GameState.RUNNING) {
            checkShouldEnd(game);
        }
    }

    @Override
    public void checkShouldEnd(Game game) {
        if (game.getTeamManager().getLivingPlayers().size() <= 1) {
            GameEndCheckEvent event = new GameEndCheckEvent(game, GameEndCheckEvent.CheckReason.TOO_FEW_ALIVE);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                game.getGameStateManager().stop();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        orderedDeaths.add(e.getKilled().getUniqueId());
        checkShouldEnd(game);
    }

    @EventHandler
    public void onPlayerQuit(GameDeathEvent e) {
        checkShouldEnd(game);
    }

    @EventHandler
    public void onPlayerSpawn(GameRespawnEvent e) {
        orderedDeaths.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public List<Player> getRankedPlayers() {
        Stack<Player> players = new Stack<>();
        for (UUID uuid : orderedDeaths) {
            players.push(Bukkit.getPlayer(uuid));
        }
        List<Player> rankedPlayers = new ArrayList<>();
    
        List<Player> living = game.getTeamManager().getLivingPlayers();
        if (!living.isEmpty()) {
            rankedPlayers.add(living.get(0));
        }
    
        while (!players.isEmpty()) {
            rankedPlayers.add(players.pop());
        }
        return rankedPlayers;
    }
}
