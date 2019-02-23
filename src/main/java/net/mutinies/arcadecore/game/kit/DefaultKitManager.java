package net.mutinies.arcadecore.game.kit;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.graphics.inventory.InventoryWindow;
import net.mutinies.arcadecore.graphics.inventory.WindowButton;
import net.mutinies.arcadecore.item.ClickEvent;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
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
        ItemManager itemManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(ItemManager.class);
        itemManager.registerTag("select_kit", e -> {
            e.setCancelled(true);
            if (e.getClickType() == ClickEvent.ClickType.RIGHT) {
                showGameKitGui(e.getPlayer());
            }
        });
    }
    
    @Override
    public void disable() {
        ItemManager itemManager = ArcadeCorePlugin.getInstance().getManagerHandler().getManager(ItemManager.class);
        itemManager.unregister("select_kit");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKits.remove(e.getPlayer().getUniqueId());
    }
    
    @Override
    public void showGameKitGui(Player player) {
        InventoryWindow window = new InventoryWindow(ChatColor.GRAY + "Kits");
        for (int i = 0; i < kits.size(); i++) {
            int j = i;
            window.set(i, new WindowButton(ItemBuilder.of(kits.get(j).getRepresentingStack()).name(kits.get(j).getDisplayName()).build(), clickEvent -> {
                setKit(player, kits.get(j));
                game.getGameStateManager().giveKitSelectionItem(player);
            }));
        }
        window.show(player);
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
                break;
        }
        
        playerKits.put(player.getUniqueId(), kit);
        // todo implement per state, make event
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
