package net.mutinies.arcadecore.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemBuilder {
    public static ItemBuilder of(ItemStack stack) {
        return new ItemBuilder(stack);
    }
    
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }
    
    private ItemStack stack;
    
    public ItemBuilder(ItemStack stack) {
        this.stack = Objects.requireNonNull(stack);
    }
    
    public ItemBuilder name(String name) {
        Objects.requireNonNull(name);
        String displayName = ChatColor.translateAlternateColorCodes('&', name);
        return applyMetaChange(meta -> meta.setDisplayName(displayName));
    }
    
    public ItemBuilder lore(String... lore) {
        Objects.requireNonNull(lore);
        return lore(Arrays.asList(lore));
    }
    
    public ItemBuilder lore(List<String> lore) {
        Objects.requireNonNull(lore);
        List<String> itemLore = lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        return applyMetaChange(meta -> meta.setLore(itemLore));
    }
    
    public ItemBuilder setFlags(ItemFlag... flags) {
        Objects.requireNonNull(flags);
        return applyMetaChange(meta -> meta.addItemFlags(flags));
    }
    
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        Objects.requireNonNull(enchantment);
        return enchant(enchantment, level, true);
    }
    
    public ItemBuilder enchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        Objects.requireNonNull(enchantment);
        return applyMetaChange(meta -> meta.addEnchant(enchantment, level, ignoreLevelRestriction));
    }
    
    public ItemBuilder unbreakable() {
        return applyMetaChange(itemMeta -> itemMeta.spigot().setUnbreakable(true));
    }
    
    public ItemBuilder glow() {
        return enchant(Enchantment.DURABILITY, 1).setFlags(ItemFlag.HIDE_ENCHANTS);
    }
    
    public ItemBuilder applyMetaChange(Consumer<ItemMeta> metaConsumer) {
        if (metaConsumer == null) {
            return this;
        }
        
        ItemMeta meta = stack.getItemMeta();
        metaConsumer.accept(meta);
        stack.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder applyDataChange(Consumer<MaterialData> dataConsumer) {
        if (dataConsumer == null) {
            return this;
        }
        
        MaterialData data = stack.getData();
        dataConsumer.accept(data);
        stack.setData(data);
        return this;
    }
    
    public ItemBuilder durability(short durability) {
        stack.setDurability(durability);
        return this;
    }
    
    public ItemStack build() {
        return stack;
    }
}
