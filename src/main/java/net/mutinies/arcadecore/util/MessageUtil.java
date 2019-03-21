package net.mutinies.arcadecore.util;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MessageUtil {
    public static String DEFAULT = "" + ChatColor.GRAY;
    public static String ERROR = "" + ChatColor.DARK_BLUE;
    public static String CATEGORY = "" + ChatColor.WHITE;
    public static String VARIABLE = "" + ChatColor.LIGHT_PURPLE;
    public static String SEPARATOR = "" + ChatColor.DARK_GRAY;
    public static String NUMBER = "" + ChatColor.DARK_GRAY;
    
    public static String getColoredName(Entity entity) {
        ChatColor color = ChatColor.YELLOW;
        Game game = ArcadeCorePlugin.getGame();
        
        if (entity instanceof Player) {
            GameTeam team = game.getTeamManager().getTeam((Player) entity);
            color = team.getColor().getChatColor();
        }
        
        return "" + color + entity.getName();
    }
    
    public static void send(CommandSender sender, String message) {
        send(sender, "Game", message);
    }
    
    public static void send(CommandSender sender, String header, String message) {
        sender.sendMessage(formatMessage(header, message));
    }
    
    public static void broadcast(String message) {
        broadcast("Game", message);
    }
    
    public static void broadcast(String header, String message) {
        Bukkit.broadcastMessage(formatMessage(header, message));
    }
    
    public static void sendError(CommandSender sender, String header, String message) {
        sender.sendMessage(formatError(header, message));
    }
    
    public static String formatMessage(String header, String message) {
        return CATEGORY + "[" + VARIABLE + header + CATEGORY + "] " + DEFAULT + message;
    }
    
    public static String formatError(String header, String message) {
        return CATEGORY + "[" + VARIABLE + header + CATEGORY + "] " + ERROR + message;
    }
}
