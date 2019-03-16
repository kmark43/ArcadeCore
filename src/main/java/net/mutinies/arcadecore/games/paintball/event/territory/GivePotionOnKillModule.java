package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.games.paintball.ReviveModule;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class GivePotionOnKillModule implements Module {
    private ReviveModule reviveModule;
    
    public GivePotionOnKillModule(ReviveModule reviveModule) {
        this.reviveModule = reviveModule;
    }
    
    @EventHandler
    public void givePotion(GameDeathEvent e) {
        if (e.getKiller() instanceof Player) {
            Player player = (Player)e.getKiller();
            int numPotions = reviveModule.getNumPotions(player, 1);
            if (numPotions < 3) {
//                player.getInventory().addItem(reviveModule.getReviveStack(player, 1));
                reviveModule.setNumPotions(player, 1, numPotions + 1);
            }
        }
    }
}
