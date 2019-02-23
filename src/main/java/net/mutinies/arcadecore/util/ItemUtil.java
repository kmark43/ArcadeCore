package net.mutinies.arcadecore.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemUtil {
    public static ItemStack getNamedItem(ItemStack stack, String name, String... lore) {
        Objects.requireNonNull(stack);
        Objects.requireNonNull(name);
        Objects.requireNonNull(lore);
        
        return ItemBuilder.of(stack).name(name).lore(lore).build();
    }
    
    public static ItemStack getNamedItem(Material material, String name, String... lore) {
        return getNamedItem(new ItemStack(material), name, lore);
    }
}
