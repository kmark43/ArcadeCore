package net.mutinies.arcadecore.game.kit.armor;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class TeamArmorGenerator implements ArmorGenerator {
    @Override
    public ItemStack[] getArmor(Player player) {
        GameTeam team = ArcadeCorePlugin.getInstance().getGameManager().getGame().getTeamManager().getTeam(player);
        Color color = team.getColor().getColor();
    
        ItemStack[] armor = {
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_HELMET)
        };
    
        for (ItemStack stack : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
            meta.setColor(color);
            stack.setItemMeta(meta);
        }
    
        return armor;
    }
}
