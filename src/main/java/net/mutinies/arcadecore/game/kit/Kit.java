package net.mutinies.arcadecore.game.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Kit {
    private String name;
    private String displayName;
    private ItemStack representingStack;
    
    public Kit(String name, String displayName, ItemStack representingStack) {
        this.name = name;
        this.displayName = displayName;
        this.representingStack = representingStack;
    }
    
    public abstract void giveItems(Player player);
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ItemStack getRepresentingStack() {
        return representingStack;
    }
}
