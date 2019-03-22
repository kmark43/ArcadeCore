package net.mutinies.arcadecore.games.paintball;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.event.GameEndCheckEvent;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.PotionProjectile;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.item.ClickEvent;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.util.EntityUtil;
import net.mutinies.arcadecore.util.MutiniesColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Stream;

public class ReviveModule implements Module {
    private Game game;
    private BiMap<Entity, Player> armorStandMap;
    
    private BukkitTask endCheckTask;
    
    private Set<Projectile> thrownPotions;
    
    public ReviveModule(Game game) {
        this.game = game;
    }
    
    @Override
    public void enable() {
        ItemManager itemManager = ArcadeCorePlugin.getManagerHandler().getManager(ItemManager.class);
        itemManager.registerTag("revive_bomb", this::throwRevive);
        armorStandMap = HashBiMap.create();
        thrownPotions = new HashSet<>();
    }
    
    @Override
    public void disable() {
        cancelEndCheck();
        ItemManager itemManager = ArcadeCorePlugin.getManagerHandler().getManager(ItemManager.class);
        itemManager.unregister("revive_bomb");
        for (Entity entity : new ArrayList<>(armorStandMap.keySet())) {
            entity.remove();
        }
        armorStandMap = null;
        thrownPotions = null;
    }
    
    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        Player player = e.getKilled();
        Location location = e.getDeathLocation();
        
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        EntityUtil.preventCollisions(armorStand);
        armorStand.setVelocity(new Vector(0, 0, 0));
    
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        Stream.of(chestplate, leggings, boots).forEach(itemStack -> {
            LeatherArmorMeta itemMeta = ((LeatherArmorMeta) itemStack.getItemMeta());
            itemMeta.setColor(Color.BLACK);
            itemStack.setItemMeta(itemMeta);
        });
    
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)0, ((byte) SkullType.PLAYER.ordinal()));
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(player.getName());
        skull.setItemMeta(meta);
        ItemStack handStack = player.getItemInHand();
    
        armorStand.setHelmet(skull);
        armorStand.setChestplate(chestplate);
        armorStand.setLeggings(leggings);
        armorStand.setBoots(boots);
        armorStand.setItemInHand(handStack);
        ChatColor shotPlayerColor = game.getTeamManager().getTeam(player).getColor().getChatColor();
        armorStand.setCustomName("" + shotPlayerColor + ChatColor.getByChar('k') + "xx" + shotPlayerColor +
                player.getName() + ChatColor.getByChar('k') + "xx");
        armorStand.setCustomNameVisible(true);
        
        armorStandMap.put(armorStand, player);
    }
    
    private void throwRevive(ClickEvent clickEvent) {
        if (clickEvent.getClickType() != ClickEvent.ClickType.RIGHT) return;
        clickEvent.setCancelled(true);
        
        Player player = clickEvent.getPlayer();
        
        int slot = clickEvent.getPlayer().getInventory().getHeldItemSlot();
        int numPotions = getNumPotions(clickEvent.getPlayer(), slot);
        ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
        PotionProjectile projectile = new PotionProjectile(potion);
        projectile.addPotionSplashListener(this::handleSplash);
        thrownPotions.add(potion);
        Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> {
            if (thrownPotions != null) {
                thrownPotions.remove(potion);
            }
        }, 5 * 20);
        
        game.getProjectileManager().registerProjectile(projectile);
        
        setNumPotions(player, slot, numPotions - 1);
        
        player.updateInventory();
    }
    
    @EventHandler
    public void onPlayerThrowPotion(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof ThrownPotion)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player)e.getEntity().getShooter();
        if (!game.getSpectateManager().isSpectator(player)) return;
        
        e.getEntity().remove();
        int slot = player.getInventory().getHeldItemSlot();
        int numPotions = getNumPotions(player, slot);
        Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> setNumPotions(player, slot, numPotions));
    }
    
    private void handleSplash(PotionProjectile potion, PotionSplashEvent potionSplashEvent) {
        if (!(potion.getProjectile().getShooter() instanceof Player)) return;
        Player player = (Player)potion.getProjectile().getShooter();
    
        potionSplashEvent.setCancelled(true);
        
        thrownPotions.remove(potion.getProjectile());
        cancelEndCheck();
        
        Collection<LivingEntity> entities = potionSplashEvent.getAffectedEntities();
        for (LivingEntity entity : entities) {
            Player target;
            if (entity instanceof Player) {
                target = (Player) entity;
                if (!game.getSpectateManager().isSpectator(target) &&
                        game.getTeamManager().getTeam(player).equals(game.getTeamManager().getTeam(target))) {
                    game.getDamageManager().respawn(target);
                    Bukkit.getPluginManager().callEvent(new GameReviveEvent(target));
                }
            } else if (armorStandMap.containsKey(entity)) {
                target = armorStandMap.get(entity);
                if (game.getTeamManager().getTeam(player).equals(game.getTeamManager().getTeam(target))) {
                    entity.remove();
                    game.getDamageManager().respawn(target);
                    target.setVelocity(new Vector(0, 0, 0));
                    target.teleport(entity);
                    armorStandMap.remove(entity);
                    Bukkit.getPluginManager().callEvent(new GameReviveEvent(target));
                }
            }
        }
    
        game.getEndHandler().checkShouldEnd(game);
    }
    
    private void scheduleEndCheck() {
        cancelEndCheck();
        endCheckTask = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> {
            game.getEndHandler().checkShouldEnd(game);
            cancelEndCheck();
        }, 5 * 20);
    }
    
    private void cancelEndCheck() {
        if (endCheckTask != null) {
            endCheckTask.cancel();
            endCheckTask = null;
        }
    }
    
    @EventHandler
    public void onGameEndCheck(GameEndCheckEvent e) {
        if (e.getCheckReason() == GameEndCheckEvent.CheckReason.TOO_FEW_ALIVE) {
            
            for (Projectile proj : new ArrayList<>(thrownPotions)) {
                if (!(proj.getShooter() instanceof Player)) continue;
                Player thrower = (Player) proj.getShooter();
                GameTeam team = game.getTeamManager().getTeam(thrower);
                
                if (team.getLivingPlayers().size() == 0) {
                    e.setCancelled(true);
                    scheduleEndCheck();
                }
            }
        }
    }
    
    @EventHandler
    public void onRespawn(GameRespawnEvent e) {
        Entity entity = armorStandMap.inverse().get(e.getPlayer());
        if (entity != null) {
            armorStandMap.inverse().remove(e.getPlayer());
            entity.remove();
        }
    }
    
    public int getNumPotions(Player player, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        return stack == null || stack.getType() == Material.AIR ? 0 : stack.getAmount();
    }
    
    public void setNumPotions(Player player, int slot, int numPotions) {
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
        return ItemManager.tag(stack, "revive_bomb");
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
