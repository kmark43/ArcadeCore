package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.util.MessageUtil;
import net.mutinies.arcadecore.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public abstract class TeamWinHandler implements EndHandler {
    public abstract GameTeam getWinningTeam(Game game);

    @Override
    public void onWin(Game game) {
        GameTeam winner = getWinningTeam(game);
        String winnerName;
        if (winner != null) {
            winnerName = winner.getColor().getChatColor() + winner.getDisplayName() + " Team";
        } else {
            winnerName = ChatColor.YELLOW + "Nobody";
        }
        MessageUtil.broadcast("Game", winnerName + MessageUtil.DEFAULT + " has won the game.");
        Bukkit.broadcastMessage("");
        TitleUtil.broadcastTitle(winnerName, "won the game");
    }

    @Override
    public List<Player> getWinningPlayers(Game game) {
        GameTeam winningTeam = getWinningTeam(game);

        if (winningTeam != null) {
            return winningTeam.getPlayers().stream().map(Bukkit::getPlayer).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
