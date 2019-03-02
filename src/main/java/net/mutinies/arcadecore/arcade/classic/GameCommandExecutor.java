package net.mutinies.arcadecore.arcade.classic;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.api.StartResult;
import net.mutinies.arcadecore.api.StopResult;
import net.mutinies.arcadecore.arcade.ArcadeManager;
import net.mutinies.arcadecore.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameCommandExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Invalid arguments");
        } else {
            ArcadeManager arcadeManager = ArcadeCorePlugin.getArcadeManager();
            GameManager gameManager = ArcadeCorePlugin.getGameManager();
            switch (args[0].toLowerCase()) {
                case "start":
                    StartResult startResult = gameManager.startGame();
                    switch (startResult) {
                        case STARTED:
                            sender.sendMessage("Started the game");
                            break;
                        case INVALID_STATE:
                            sender.sendMessage("Invalid state");
                            break;
                        case NO_GAME_DEFINED:
                            sender.sendMessage("No game defined");
                            break;
                        case NO_MAP_DEFINED:
                            sender.sendMessage("No map defined");
                            break;
                    }
                    break;
                case "stop":
                    StopResult stopResult = gameManager.stopGame();
                    switch (stopResult) {
                        case STOPPED:
                            sender.sendMessage("Stopped the game");
                            break;
                        case INVALID_STATE:
                            sender.sendMessage("Invalid state");
                            break;
                    }
                    break;
                case "set":
                    if (args.length == 1) {
                        sender.sendMessage("Please specify a game");
                    } else {
                        if (arcadeManager.hasGame(args[1])) {
                            gameManager.setGame(args[1]);
                            sender.sendMessage("Set game to " + args[1]);
                        } else {
                            sender.sendMessage("Game not found");
                        }
                    }
                    break;
                default:
                    sender.sendMessage("Invalid arguments");
                    break;
            }
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("start", "stop", "set").filter(v -> v.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].toLowerCase().equals("set")) {
            List<Game> games = new ArrayList<>(ArcadeCorePlugin.getArcadeManager().getGames());
            List<String> results = new ArrayList<>();
            for (Game game : games) {
                if (game.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    results.add(game.getName());
                }
            }
            return results;
        }
        return null;
    }
}
