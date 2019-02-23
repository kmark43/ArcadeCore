package net.mutinies.arcadecore.game.kit.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ArmorGenerator {
    ItemStack[] getArmor(Player player);
}
