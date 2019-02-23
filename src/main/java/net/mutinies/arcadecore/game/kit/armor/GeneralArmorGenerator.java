package net.mutinies.arcadecore.game.kit.armor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeneralArmorGenerator implements ArmorGenerator {
    private ArmorType type;
    
    public GeneralArmorGenerator(ArmorType type) {
        this.type = type;
    }
    
    @Override
    public ItemStack[] getArmor(Player player) {
        switch (type) {
            case LEATHER:
                return new ItemStack[]{new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)};
            case GOLDEN:
                return new ItemStack[]{new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.GOLD_HELMET)};
            case IRON:
                return new ItemStack[]{new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)};
            case DIAMOND:
                return new ItemStack[]{new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_HELMET)};
            default:
                return null;
        }
    }
}
