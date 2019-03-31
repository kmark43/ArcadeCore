package net.mutinies.arcadecore.arcade.classic.command;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamCommandExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Game", "Must be a player to execute this command");
            return true;
        }
        
        GameManager gameManager = ArcadeCorePlugin.getGameManager();
        Game game = ArcadeCorePlugin.getGame();
        Player player = (Player)sender;
        
        if (game == null) {
            MessageUtil.sendError(sender, "Game", "No game defined");
            return true;
        }
    
        if (!gameManager.isGameRunning()) {
            // todo fill in
        } else {
            game.getTeamManager().setTeam(player, game.getTeamManager().getTeam(args[0]));
            game.getKitManager().getKit(player).giveItems(player);
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        return null;
    }
}
