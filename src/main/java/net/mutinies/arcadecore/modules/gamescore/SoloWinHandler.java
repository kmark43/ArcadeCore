package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.util.MessageUtil;
import net.mutinies.arcadecore.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class SoloWinHandler implements EndHandler {
    public abstract List<Player> getRankedPlayers();

    @Override
    public void onWin(Game game) {
        List<Player> ranked = getRankedPlayers();
        String winnerName;
        if (ranked != null && !ranked.isEmpty()) {
            Player winner = ranked.get(0);
            winnerName = MessageUtil.getColoredName(winner);
            
        } else {
            winnerName = ChatColor.YELLOW + "Nobody";
        }
        MessageUtil.broadcast("Game", winnerName + MessageUtil.DEFAULT + " has won the game.");
        Bukkit.broadcastMessage("");
        TitleUtil.broadcastTitle(winnerName, "won the game");
    }

    @Override
    public List<Player> getWinningPlayers(Game game) {
        List<Player> ranked = getRankedPlayers();
        if (!ranked.isEmpty()) {
           return Collections.singletonList(ranked.get(0));
        } else {
            return null;
        }
    }
}
