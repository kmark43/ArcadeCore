package net.mutinies.arcadecore.games.paintball;

import net.mutinies.arcadecore.event.PlayerHealthChangeEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorPaintingModule implements Module {
    private Game game;
    
    public ArmorPaintingModule(Game game) {
        this.game = game;
    }
    
    public void updateArmor(Player player) {
        GameTeam playerTeam = game.getTeamManager().getTeam(player);
        int expectedNumPainted = getExpectedArmorPiecesPainted(player);
        setPaintedArmor(player, expectedNumPainted, playerTeam.getColor().getColor(), Color.BLACK);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileDamage(PlayerHealthChangeEvent e) {
        updateArmor(e.getPlayer());
    }
    
    private int getExpectedArmorPiecesPainted(Player player) {
        int finalHealth = (int)Math.max(0, Math.min(20, player.getHealth()));
        return 4 - finalHealth / 5;
    }
    
    private void setPaintedArmor(Player player, int expectedNumPainted, Color color, Color paintingColor) {
        int numArmorPiecesToPaint = expectedNumPainted - getNumArmorPiecesPainted(player, color);
        paintArmorPieces(player, numArmorPiecesToPaint, color, paintingColor);
    }
    
    private int getNumArmorPiecesPainted(Player player, Color expectedColor) {
        int numPainted = 0;
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack piece : armor) {
            Color pieceColor = getColor(piece);
            if (!pieceColor.equals(expectedColor)) {
                numPainted++;
            }
        }
        return numPainted;
    }
    
    private void paintArmorPieces(Player player, int numToPaint, Color colorToUnpaint, Color colorToPaint) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (numToPaint < 0) {
            numToPaint = -numToPaint;
            for (int i = 0; i < numToPaint; i++) {
                int index;
                do {
                    index = (int)(Math.random() * 4);
                } while (getColor(armor[index]).equals(colorToUnpaint));
                setColor(armor[index], colorToUnpaint);
            }
        } else if (numToPaint > 0) {
            for (int i = 0; i < numToPaint; i++) {
                int index;
                do {
                    index = (int)(Math.random() * 4);
                } while (!getColor(armor[index]).equals(colorToUnpaint));
                setColor(armor[index], colorToPaint);
            }
        }
    }
    
    private Color getColor(ItemStack armorPiece) {
        return ((LeatherArmorMeta) armorPiece.getItemMeta()).getColor();
    }
    
    private ItemStack setColor(ItemStack armorPiece, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta)armorPiece.getItemMeta();
        meta.setColor(color);
        armorPiece.setItemMeta(meta);
        return armorPiece;
    }
}
