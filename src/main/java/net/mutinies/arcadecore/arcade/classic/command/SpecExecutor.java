package net.mutinies.arcadecore.arcade.classic.command;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.arcade.participation.ParticipationManager;
import net.mutinies.arcadecore.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Must be a player");
            return true;
        }
    
        ParticipationManager participationManager = ArcadeCorePlugin.getGameManager().getParticipationManager();
    
        Player player = (Player) sender;
        if (args.length == 0) {
            participationManager.setParticipating(player, !participationManager.isParticipating(player));
        } else if (args.length == 1) {
            if (args[0].equals("enable")) {
                participationManager.setParticipating(player, false);
            } else if (args[0].equals("disable")) {
                participationManager.setParticipating(player, true);
            } else {
                MessageUtil.sendError(sender, "Invalid args");
                return true;
            }
        } else {
            MessageUtil.sendError(sender, "Invalid args");
            return true;
        }
        
        if (participationManager.isParticipating(player)) {
            MessageUtil.send(sender, "You will participate in the next game");
        } else {
            MessageUtil.send(sender, "You will no longer participate in the next game");
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("enable", "disable").filter(arg -> arg.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }
}
