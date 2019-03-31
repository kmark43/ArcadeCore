package net.mutinies.arcadecore.arcade.classic.command;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommandExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Game game = ArcadeCorePlugin.getGame();
        if (args.length == 0) {
            List<ConfigProperty> properties = game.getConfigManager().getProperties();
            for (ConfigProperty property : properties) {
                MessageUtil.send(sender,property.getName() + MessageUtil.SEPARATOR + " - " + MessageUtil.VARIABLE + property.getValue());
            }
        } else if (args.length == 1) {
            if (game.getConfigManager().hasProperty(args[0])) {
                ConfigProperty property = game.getConfigManager().getProperty(args[0]);
                MessageUtil.send(sender,property.getName() + MessageUtil.SEPARATOR + " - " + MessageUtil.VARIABLE + property.getValue());
            } else if (args[0].equals("setdefaults")) {
                for (ConfigProperty property : game.getConfigManager().getProperties()) {
                    property.setValue(property.getDefaultValue());
                }
            } else {
                MessageUtil.sendError(sender,"Invalid arguments");
            }
        } else if (args.length == 2) {
            if (game.getConfigManager().hasProperty(args[0])) {
                ConfigProperty property = game.getConfigManager().getProperty(args[0]);
                if (property.setValue(args[1])) {
                    MessageUtil.send(sender,"Set " + property.getName() + " to " + MessageUtil.VARIABLE + property.getValue());
                } else {
                    MessageUtil.sendError(sender,"Your input does not satisfy the property's constraints");
                }
            }
        } else {
            MessageUtil.sendError(sender,"Invalid arguments");
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Game game = ArcadeCorePlugin.getGame();
        if (args.length == 1) {
            List<ConfigProperty> properties = game.getConfigManager().getProperties();
            List<String> ret = new ArrayList<>();
            ret.add("setdefaults");
            for (ConfigProperty property : properties) {
                if (property.getName().startsWith(args[0])) {
                    ret.add(property.getName());
                }
            }
            return ret;
        } else if (args.length == 2) {
            // todo add setting thing
        }
        return null;
    }
}
