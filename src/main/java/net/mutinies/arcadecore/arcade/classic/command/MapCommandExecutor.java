package net.mutinies.arcadecore.arcade.classic.command;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MapCommandExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GameManager gameManager = ArcadeCorePlugin.getGameManager();
        if (args.length == 0) {
            MessageUtil.sendError(sender,"Invalid args");
        } else {
            if (!gameManager.isGameRunning()) {
                if (gameManager.getGame().getMapManager().getMap(args[0]) != null) {
                    gameManager.setMap(args[0]);
                    MessageUtil.send(sender,"Set map to " + MessageUtil.VARIABLE + args[0]);
                } else {
                    MessageUtil.sendError(sender,"Map not found");
                }
            } else {
                MessageUtil.sendError(sender,"Invalid game state");
            }
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<GameMap> maps = ArcadeCorePlugin.getGame().getMapManager().getMaps();
            List<String> results = new ArrayList<>();
            for (GameMap map : maps) {
                if (map.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(map.getName());
                }
            }
            return results;
        }
        return null;
    }
}
