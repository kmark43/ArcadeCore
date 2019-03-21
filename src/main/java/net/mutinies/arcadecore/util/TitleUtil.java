package net.mutinies.arcadecore.util;

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class TitleUtil {
    public static void broadcastTitle(String title, String subtitle) {
        broadcastTitle(title, subtitle, 5, 40, 5);
    }
    public static void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
    public static void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 5, 40, 5);
    }
    
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer p = ((CraftPlayer) player);
        
        PacketPlayOutTitle packetSubtitle;
        if (title != null) {
            packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CraftChatMessage.fromString(title)[0]);
            p.getHandle().playerConnection.sendPacket(packetSubtitle);
        }
    
        if (subtitle != null) {
            packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(subtitle)[0]);
            p.getHandle().playerConnection.sendPacket(packetSubtitle);
        }
        
        packetSubtitle = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        p.getHandle().playerConnection.sendPacket(packetSubtitle);
    }
}
