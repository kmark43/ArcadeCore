package net.mutinies.arcadecore.game.kit;

import net.mutinies.arcadecore.modules.kit.KitModule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit {
    private String name;
    private String displayName;
    private ItemStack representingStack;
    private List<KitModule> modules;
    private List<PotionEffect> effects;
    
    public Kit(String name, String displayName, ItemStack representingStack) {
        this.name = name;
        this.displayName = displayName;
        this.representingStack = representingStack;
        effects = new ArrayList<>();
        modules = new ArrayList<>();
    }
    
    public abstract void giveItems(Player player);
    
    public void giveEffects(Player player) {
        player.addPotionEffects(effects);
    }
    
    public void addEffect(PotionEffect effect) {
        this.effects.add(effect);
    }
    
    public void addModule(KitModule module) {
        modules.add(module);
        module.addKit(this);
    }
    
    public List<PotionEffect> getEffects() {
        return new ArrayList<>(effects);
    }
    
    public List<KitModule> getModules() {
        return new ArrayList<>(modules);
    }
    
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
