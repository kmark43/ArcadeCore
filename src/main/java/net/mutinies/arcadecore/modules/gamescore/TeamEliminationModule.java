package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.event.GameEndCheckEvent;
import net.mutinies.arcadecore.event.GameStateSetEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.state.GameStateManager;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class TeamEliminationModule extends TeamWinHandler implements Module {
    private Game game;

    public TeamEliminationModule(Game game, boolean displayScoreboard) {
        this.game = game;
        if (displayScoreboard) {
            game.getScoreboardManager().setTitle("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + game.getDisplayName());
        
            game.getScoreboardManager().setLineFunction(player -> {
                List<String> lines = new ArrayList<>();
            
                for (GameTeam team : game.getTeamManager().getLivingTeams()) {
                    lines.add("");
                    lines.add("" + team.getColor().getChatColor() + ChatColor.BOLD + team.getDisplayName());
                    lines.add("" + team.getLivingPlayers().size() + " Alive");
                }
                return lines;
            });
        }
    }
    
    @Override
    public void enable() {
    
    }
    
    @EventHandler
    public void onGameStart(GameStateSetEvent e) {
        if (e.getNewState() == GameStateManager.GameState.RUNNING) {
            checkShouldEnd(game);
        }
    }
    
    @Override
    public void checkShouldEnd(Game game) {
        int numWithPlayers = game.getTeamManager().getLivingTeams().size();

        if (numWithPlayers <= 1) {
            GameEndCheckEvent event = new GameEndCheckEvent(game, GameEndCheckEvent.CheckReason.TOO_FEW_ALIVE);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                game.getGameStateManager().stop();
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        checkShouldEnd(game);
    }

    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        GameTeam team = game.getTeamManager().getTeam(e.getKilled());
        if (team.getLivingPlayers().size() == 0) {
            checkShouldEnd(game);
        }
    }

    @Override
    public GameTeam getWinningTeam(Game game) {
        List<GameTeam> livingTeams = game.getTeamManager().getLivingTeams();
        return livingTeams.size() == 1 ? livingTeams.get(0) : null;
    }
}
