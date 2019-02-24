package net.mutinies.arcadecore.game.kit;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;

import java.util.List;

public interface KitManager extends Module  {
    void showGameKitGui(Player player);
    void giveKitSelectionItems();
    void giveKitSelectionItem(Player player);
    void setDefaultKits();
    void setDefaultKit(Player player);
    void setKit(Player player, Kit kit);
    Kit getKit(Player player);
    void addKit(Kit kit);
    Kit getKit(String name);
    List<Kit> getKits();
}
