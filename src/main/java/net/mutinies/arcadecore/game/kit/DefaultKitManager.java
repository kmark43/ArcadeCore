package net.mutinies.arcadecore.game.kit;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.event.KitSetEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.graphics.inventory.InventoryWindow;
import net.mutinies.arcadecore.graphics.inventory.WindowButton;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DefaultKitManager implements KitManager {
    private Game game;
    private List<Kit> kits;
    private Map<String, Kit> kitMap;
    private Map<UUID, Kit> playerKits;
    
    public DefaultKitManager(Game game) {
        this.game = Objects.requireNonNull(game);
        kits = new ArrayList<>();
        kitMap = new HashMap<>();
        playerKits = new HashMap<>();
    }
    
    @Override
    public void enable() {
    }
    
    @Override
    public void disable() {
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKits.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onPlayerRevive(GameRespawnEvent e) {
        getKit(e.getPlayer()).giveEffects(e.getPlayer());
    }
    
    @Override
    public void showGameKitGui(Player player) {
        InventoryWindow window = new InventoryWindow(ChatColor.DARK_GRAY + "Kits");
        int r = 1;
        int c = 1;
        for (Kit kit : kits) {
            ItemBuilder builder = ItemBuilder.of(kit.getRepresentingStack());
            if (getKit(player).equals(kit)) {
                builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Selected Kit"  + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + kit.getDisplayName());
                builder.glow();
            } else {
                builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Select " + ChatColor.WHITE + kit.getDisplayName());
            }
            builder.setFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
            window.set(r * 9 + c, new WindowButton(builder.build(), clickEvent -> {
                setKit(player, kit);
                giveKitSelectionItem(player);
                showGameKitGui(player);
            }));
            c++;
            if (c >= 8) {
                c = 1;
                r++;
            }
        }
        window.show(player);
    }
    
    @Override
    public void setDefaultKits() {
        for (Player player : ArcadeCorePlugin.getParticipants()) {
            setDefaultKit(player);
        }
    }
    
    @Override
    public void setDefaultKit(Player player) {
        Kit kit = getKit(player);
        if (kit == null) {
            setKit(player, getKits().get(0));
        }
    }
    
    @Override
    public void giveKitSelectionItems() {
        for (Player player : ArcadeCorePlugin.getParticipants()) {
            giveKitSelectionItem(player);
        }
    }
    
    @Override
    public void giveKitSelectionItem(Player player) {
        Kit kit = game.getKitManager().getKit(player);
        ItemBuilder builder = ItemBuilder.of(kit.getRepresentingStack());
        builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Selected Kit"  + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + kit.getDisplayName());
        builder.unbreakable();
        builder.setFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        
        ItemStack kitStack = ItemManager.tag(builder.build(), "select_kit");
        player.getInventory().setItem(0, kitStack);
    }
    
    @Override
    public void setKit(Player player, Kit kit) {
        Objects.requireNonNull(kit);
        
        switch (game.getGameStateManager().getState()) {
            case NOT_ACTIVE:
            case STARTING:
//                playerKits.put(player.getUniqueId(), kit);
                break;
            case RUNNING:
            case ENDING:
                player.getInventory().setContents(new ItemStack[36]);
                player.getInventory().setArmorContents(new ItemStack[4]);
                kit.giveItems(player);
                kit.giveEffects(player);
                break;
        }
        
        playerKits.put(player.getUniqueId(), kit);
        Bukkit.getPluginManager().callEvent(new KitSetEvent(player, kit));
    }
    
    @Override
    public Kit getKit(Player player) {
        return playerKits.get(player.getUniqueId());
    }
    
    @Override
    public void addKit(Kit kit) {
        kits.add(kit);
        kitMap.put(kit.getName().toLowerCase(), kit);
    }
    
    @Override
    public Kit getKit(String name) {
        return kitMap.get(name.toLowerCase());
    }
    
    @Override
    public List<Kit> getKits() {
        return Collections.unmodifiableList(kits);
    }
}
