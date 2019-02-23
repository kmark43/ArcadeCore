package net.mutinies.arcadecore.item;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.mutinies.arcadecore.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemManager implements Manager {
    private Map<String, ClickHandler> clickHandlerMap;
    
    private Set<UUID> justCalled;
    
    @Override
    public void enable() {
        justCalled = new HashSet<>();
        clickHandlerMap = new HashMap<>();
    }
    
    @Override
    public void disable() {
        justCalled = null;
        clickHandlerMap = null;
    }
    
    public void registerTag(String tag, ClickHandler clickHandler) {
        clickHandlerMap.put(tag, clickHandler);
    }
    
    public void unregister(String tag) {
        clickHandlerMap.remove(tag);
    }
    
    public static ItemStack tag(ItemStack stack, String value) {
        return tag(stack, "clickHandlerTag", value);
    }
    
    public static ItemStack tag(ItemStack stack, String key, String value) {
        if (stack == null) return null;
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound nmsTag = nmsStack.getTag();
        if (nmsTag == null) {
            nmsTag = new NBTTagCompound();
            nmsStack.setTag(nmsTag);
        }
        nmsTag.set(key, new NBTTagString(value));
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    public static ItemStack untag(ItemStack stack) {
        return untag(stack, "clickHandlerTag");
    }
    
    public static ItemStack untag(ItemStack stack, String key) {
        if (stack == null) return null;
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound nmsTag = nmsStack.getTag();
    
        if (nmsTag == null) {
            return stack;
        }
    
        if (nmsTag.hasKey(key)) {
            nmsTag.remove(key);
        }
    
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    public static String getTag(ItemStack stack) {
        return getTag(stack, "clickHandlerTag");
    }
    
    public static String getTag(ItemStack stack, String key) {
        if (stack == null || stack.getType() == Material.AIR) return null;
    
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound nmsTag = nmsStack.getTag();
    
        if (nmsTag == null) {
            return null;
        }
    
        if (nmsTag.hasKey(key)) {
            return nmsTag.getString(key);
        } else {
            return null;
        }
    }
    
    @EventHandler
    public void onItemClick(ClickEvent e) {
        String tag = getTag(e.getItem());
        if (tag != null && clickHandlerMap.containsKey(tag)) {
            clickHandlerMap.get(tag).onClickEvent(e);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ClickEvent clickEvent = null;
        switch (e.getAction()) {
            case RIGHT_CLICK_AIR:
                clickEvent = new ClickEvent(e.getPlayer(), ClickEvent.ClickType.RIGHT, ClickEvent.InteractType.AIR);
                break;
            case RIGHT_CLICK_BLOCK:
                clickEvent = new ClickEvent(e.getPlayer(), ClickEvent.ClickType.RIGHT, ClickEvent.InteractType.BLOCK);
                break;
            case LEFT_CLICK_AIR:
                clickEvent = new ClickEvent(e.getPlayer(), ClickEvent.ClickType.LEFT, ClickEvent.InteractType.AIR);
                justCalled.add(e.getPlayer().getUniqueId());
                break;
            case LEFT_CLICK_BLOCK:
                clickEvent = new ClickEvent(e.getPlayer(), ClickEvent.ClickType.LEFT, ClickEvent.InteractType.BLOCK);
                break;
        }
        if (clickEvent != null) {
            Bukkit.getPluginManager().callEvent(clickEvent);
            if (clickEvent.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
//        ClickEvent clickEvent = new ClickEvent(e.getPlayer(), ClickEvent.ClickType.RIGHT, ClickEvent.InteractType.ENTITY);
//        Bukkit.getPluginManager().callEvent(clickEvent);
//        if (clickEvent.isCancelled()) {
//            e.setCancelled(true);
//        }
    }
    
    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) ||
                e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        
        Player player = ((Player) e.getDamager());
        ClickEvent clickEvent = new ClickEvent(player, ClickEvent.ClickType.LEFT, ClickEvent.InteractType.ENTITY);
        Bukkit.getPluginManager().callEvent(clickEvent);
        if (clickEvent.isCancelled()) {
            e.setCancelled(true);
        }
        justCalled.add(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerArmSwing(PlayerAnimationEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.ADVENTURE &&
                e.getAnimationType() == PlayerAnimationType.ARM_SWING &&
                !justCalled.contains(e.getPlayer().getUniqueId())) {
            ClickEvent clickEvent = new ClickEvent(e.getPlayer(), ClickEvent.ClickType.LEFT, ClickEvent.InteractType.BLOCK);
            Bukkit.getPluginManager().callEvent(clickEvent);
        }
        justCalled.remove(e.getPlayer().getUniqueId());
    }
}
