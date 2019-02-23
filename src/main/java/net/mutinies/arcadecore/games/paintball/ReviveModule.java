package net.mutinies.arcadecore.games.paintball;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.PotionProjectile;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.item.ClickEvent;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.util.MutiniesColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Collection;
import java.util.UUID;

public class ReviveModule implements Module {
    private Game game;
    private BiMap<UUID, Entity> armorStandMap;
    
    public ReviveModule(Game game) {
        this.game = game;
    }
    
    @Override
    public void enable() {
        ItemManager itemManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(ItemManager.class);
        itemManager.registerTag("revive_bomb", this::throwRevive);
        armorStandMap = HashBiMap.create();
    }
    
    @Override
    public void disable() {
        ItemManager itemManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(ItemManager.class);
        itemManager.unregister("revive_bomb");
        armorStandMap = null;
    }
    
    private void throwRevive(ClickEvent clickEvent) {
        if (clickEvent.getClickType() != ClickEvent.ClickType.RIGHT) return;
        clickEvent.setCancelled(true);
        
        Player player = clickEvent.getPlayer();
        
        int slot = clickEvent.getPlayer().getInventory().getHeldItemSlot();
        int numPotions = getNumPotions(clickEvent.getPlayer(), slot);
        ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
        PotionProjectile projectile = new PotionProjectile(potion);
        projectile.addPotionSplashListener((p, potionSplashEvent) -> {
            potionSplashEvent.setCancelled(true);
            Collection<LivingEntity> entities = potionSplashEvent.getAffectedEntities();
            
            // todo revive code
        });
        game.getProjectileManager().registerProjectile(projectile);
        
        setNumPotions(player, numPotions, slot);
        
        player.updateInventory();
    }
    
    private int getNumPotions(Player player, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        return stack == null || stack.getType() == Material.AIR ? 0 : stack.getAmount();
    }
    
    private void setNumPotions(Player player, int slot, int numPotions) {
        if (numPotions == 0) {
            player.getInventory().setItem(slot, null);
        } else {
            player.getInventory().setItem(slot, getReviveStack(player, numPotions));
        }
    }
    
    public ItemStack getReviveStack(Player player, int count) {
        GameTeam team = game.getTeamManager().getTeam(player);
        MutiniesColor color = team.getColor();
    
        Potion potion = new Potion(getPotionType(color)).splash();
        ItemStack stack = new ItemStack(Material.POTION, count);
        potion.apply(stack);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setDisplayName("Paint Bomb");
        stack.setItemMeta(stackMeta);
        return stack;
    }
    
    private PotionType getPotionType(MutiniesColor color) {
        switch (color) {
            case RED:
                return PotionType.INSTANT_HEAL;
            case BLUE:
                return PotionType.NIGHT_VISION;
            case GREEN:
                return PotionType.JUMP;
            case ORANGE:
                return PotionType.FIRE_RESISTANCE;
            case PINK:
                return PotionType.REGEN;
            case AQUA:
                return PotionType.SPEED;
            case LIME:
                return PotionType.JUMP;
            case YELLOW:
                return PotionType.FIRE_RESISTANCE;
            case WHITE:
                return PotionType.INVISIBILITY;
            case BLACK:
                return PotionType.WATER;
            case PURPLE:
                return PotionType.INSTANT_DAMAGE;
        }
        return null;
    }
}
