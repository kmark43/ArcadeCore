package net.mutinies.arcadecore.game.kit;

import net.mutinies.arcadecore.game.kit.armor.ArmorGenerator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class BasicKit extends Kit {
    private Function<Player, List<ItemStack>> itemsFunction;
    private ArmorGenerator armorGenerator;
    
    public BasicKit(String name, String displayName, ItemStack representingStack, Function<Player, List<ItemStack>> items) {
        super(name, displayName, representingStack);
        this.itemsFunction = items;
    }
    
    public BasicKit(String name, String displayName, ItemStack representingStack, Function<Player, List<ItemStack>> items, ArmorGenerator armorGenerator) {
        super(name, displayName, representingStack);
        this.itemsFunction = items;
        this.armorGenerator = armorGenerator;
    }
    
    @Override
    public void giveItems(Player player) {
        player.getInventory().setContents(new ItemStack[36]);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().addItem(itemsFunction.apply(player).toArray(new ItemStack[0]));
        
        if (armorGenerator != null) {
            player.getInventory().setArmorContents(armorGenerator.getArmor(player));
        }
    }
}
