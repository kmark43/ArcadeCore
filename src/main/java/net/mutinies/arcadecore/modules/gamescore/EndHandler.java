package net.mutinies.arcadecore.modules.gamescore;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;

import java.util.List;

public interface EndHandler extends Module {
    void onWin(Game game);
    List<Player> getWinningPlayers(Game game);
}
