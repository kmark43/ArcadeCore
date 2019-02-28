package net.mutinies.arcadecore.arcade.lobbyless;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.arcade.ArcadeManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameCommandExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GameManager gameManager = ArcadeCorePlugin.getGameManager();
        
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("start")) {
                    sender.sendMessage("Please specify a game");
                } else {
                    switch (gameManager.stopGame()) {
                        case STOPPED:
                            sender.sendMessage("Stopped the game");
                            break;
                        case INVALID_STATE:
                            sender.sendMessage("Invalid state");
                            break;
                    }
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("start")) {
                    switch (gameManager.startGame(args[1], null)) {
                        case STARTED:
                            sender.sendMessage("Game started");
                            break;
                        case INVALID_STATE:
                            sender.sendMessage("Invalid state");
                            break;
                        case NO_GAME_DEFINED:
                            sender.sendMessage("Game not found");
                            break;
                        case NO_MAP_DEFINED:
                            sender.sendMessage("There was an error loading the map");
                            break;
                    }
                } else {
                    invalidArgs(sender);
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("start")) {
                    switch (gameManager.startGame(args[1], args[2])) {
                        case STARTED:
                            sender.sendMessage("Game started");
                            break;
                        case INVALID_STATE:
                            sender.sendMessage("Invalid state");
                            break;
                        case NO_GAME_DEFINED:
                            sender.sendMessage("Game not found");
                            break;
                        case NO_MAP_DEFINED:
                            sender.sendMessage("Map not found");
                            break;
                    }
                } else {
                    invalidArgs(sender);
                }
                break;
            default:
                invalidArgs(sender);
                break;
        }
        return true;
    }
    
    private void invalidArgs(CommandSender sender) {
        sender.sendMessage("Invalid Arguments");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArcadeManager arcadeManager = ArcadeCorePlugin.getArcadeManager();
        
        List<String> possibilities = new ArrayList<>();
        switch (args.length) {
            case 1:
                // start, stop
                possibilities.addAll(Arrays.asList("start", "stop"));
                break;
            case 2:
                // if start, games
                if (args[0].equalsIgnoreCase("start")) {
                    possibilities.addAll(arcadeManager.getGames().stream()
                            .map(Game::getName)
                            .collect(Collectors.toList()));
                }
                break;
            case 3:
                // if start/game defined, maps
                if (args[0].equalsIgnoreCase("start") &&
                        arcadeManager.getGame(args[1]) != null) {
                    
                    possibilities.addAll(arcadeManager.getGame(args[1]).getMapManager().getMaps().stream()
                            .map(GameMap::getName)
                            .collect(Collectors.toList()));
                }
                break;
        }
        
        return possibilities.stream()
                .filter(possibility -> possibility.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
