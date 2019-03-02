package net.mutinies.arcadecore.arcade.classic;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.game.map.GameMap;
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
            sender.sendMessage("Invalid args");
        } else {
            if (!gameManager.isGameRunning()) {
                if (gameManager.getGame().getMapManager().getMap(args[0]) != null) {
                    gameManager.setMap(args[0]);
                    sender.sendMessage("Set map to " + args[0]);
                } else {
                    sender.sendMessage("Map not found");
                }
            } else {
                sender.sendMessage("Invalid game state");
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
