package net.mutinies.arcadecore.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public enum MutiniesColor {
    RED(ChatColor.DARK_RED, DyeColor.RED, Color.RED),
    BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, Color.BLUE),
    GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, Color.GREEN),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, Color.ORANGE),
    PINK(ChatColor.RED, DyeColor.PINK, Color.fromRGB(0xFF6EC7)),
    AQUA(ChatColor.BLUE, DyeColor.LIGHT_BLUE, Color.AQUA),
    LIME(ChatColor.GREEN, DyeColor.LIME, Color.LIME),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, Color.YELLOW),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, Color.WHITE),
    BLACK(ChatColor.BLACK, DyeColor.BLACK, Color.BLACK),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, Color.fromRGB(0x9900FF)),
    ;
    
    private ChatColor chatColor;
    private DyeColor dyeColor;
    private Color color;
    
    MutiniesColor(ChatColor chatColor, DyeColor dyeColor, Color color) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.color = color;
    }
    
    public ChatColor getChatColor() {
        return chatColor;
    }
    
    public DyeColor getDyeColor() {
        return dyeColor;
    }
    
    public Color getColor() {
        return color;
    }
}
