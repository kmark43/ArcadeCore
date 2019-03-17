package net.mutinies.arcadecore.games;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.kit.BasicKit;
import net.mutinies.arcadecore.game.kit.Kit;
import net.mutinies.arcadecore.game.kit.armor.TeamArmorGenerator;
import net.mutinies.arcadecore.games.paintball.*;
import net.mutinies.arcadecore.games.paintball.gun.GunModule;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.KillComboModule;
import net.mutinies.arcadecore.modules.gamescore.TeamEliminationModule;
import net.mutinies.arcadecore.modules.prevent.NoFriendlyFireModule;
import net.mutinies.arcadecore.modules.prevent.NoPearlTeleportModule;
import net.mutinies.arcadecore.modules.prevent.NoRegenModule;
import net.mutinies.arcadecore.util.ItemBuilder;
import net.mutinies.arcadecore.util.ModuleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaintballMaker {
    private Game paintball;
    private GunModule gunModule;
    private ReviveModule reviveModule;
    private ArmorPaintingModule armorPaintingModule;
    
    private Map<String, Kit> kitMap;
    private Map<String, Module> moduleMap;
    
    public PaintballMaker(Game paintballGame) {
        this.paintball = paintballGame;
        paintball.setEndHandler(new TeamEliminationModule(paintball, true));
    
        paintball.getModuleManager().addModules(ModuleUtil.getPvpList());
        paintball.getConfigManager().getProperty("show_enemy_nametags").setDefaultValue(false);
        paintball.getConfigManager().getProperty("show_enemy_nametags").setValue(false);
        
        kitMap = new LinkedHashMap<>();
        moduleMap = new LinkedHashMap<>();
        
        gunModule = new GunModule(paintball);
        reviveModule = new ReviveModule(paintball);
        armorPaintingModule = new ArmorPaintingModule(paintball);
    
        KillComboModule comboModule = new KillComboModule(4 * 20);
        comboModule.addComboType("TRIPLE KILL", 3);
        comboModule.addComboType("PENTA KILL", 5);
        
        addModule("no_regen", new NoRegenModule());
        addModule("no_pearl_teleport", new NoPearlTeleportModule());
        addModule("paint_blocks", new PaintBlockModule(paintball, 2.5));
        addModule("paint_death_messages", new PaintDeathMessageModule());
        addModule("projectile_only_damage", new ProjectileOnlyDamage());
        addModule("no_friendly_fire", new NoFriendlyFireModule());
        addModule("gun", gunModule);
        addModule("revive", reviveModule);
        addModule("armor_painting", armorPaintingModule);
        addModule("combo", comboModule);
    
        // Rifle
        ItemStack rifleStack = ItemManager.tag(ItemBuilder.of(Material.IRON_BARDING).name("" + ChatColor.WHITE + "Rifle").build(), "gun_rifle");
    
        BasicKit rifleKit = new BasicKit("rifle", "Rifle",
                rifleStack,
                player -> Arrays.asList(rifleStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        rifleKit.addEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
        
        addKit(rifleKit);
    
        // Shotgun
        ItemStack shotgunStack = ItemManager.tag(ItemBuilder.of(Material.GOLD_BARDING).name("" + ChatColor.WHITE + "Shotgun").build(), "gun_shotgun");
    
        BasicKit shotgunKit = new BasicKit("shotgun", "Shotgun",
                shotgunStack,
                player -> Arrays.asList(shotgunStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        shotgunKit.addEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
        addKit(shotgunKit);
    
        // Machine gun
        ItemStack machineGunStack = ItemManager.tag(ItemBuilder.of(Material.DIAMOND_BARDING).name("" + ChatColor.WHITE + "Machine Gun").build(), "gun_machine_gun");
    
        BasicKit machineGunKit = new BasicKit("machine_gun", "Machine Gun",
                machineGunStack,
                player -> Arrays.asList(machineGunStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        addKit(machineGunKit);
    
        // Sniper
        ItemStack sniperStack = ItemManager.tag(ItemBuilder.of(Material.STONE_HOE).unbreakable().name("" + ChatColor.WHITE + "Sniper").build(), "gun_sniper");
    
        BasicKit sniperKit = new BasicKit("sniper", "Sniper",
                sniperStack,
                player -> Arrays.asList(sniperStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        addKit(sniperKit);
        
        // Bazooka
        ItemStack bazookaStack = ItemManager.tag(ItemBuilder.of(Material.BLAZE_ROD).name("" + ChatColor.WHITE + "Bazooka").build(), "gun_bazooka");
    
        BasicKit bazookaKit = new BasicKit("bazooka", "Bazooka",
                bazookaStack,
                player -> Arrays.asList(bazookaStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        bazookaKit.addEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1, false, false));
    
        addKit(bazookaKit);
        
        // Needler
        ItemStack needlerStack = ItemManager.tag(ItemBuilder.of(Material.ARROW).name("" + ChatColor.WHITE + "Needler").build(), "gun_needler");
    
        BasicKit needlerKit = new BasicKit("needler", "Needler",
                needlerStack,
                player -> Arrays.asList(needlerStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        needlerKit.addEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, false, false));
    
        addKit(needlerKit);
        
        //Medic
        ItemStack medicStack = ItemManager.tag(ItemBuilder.of(Material.WOOD_SPADE).unbreakable().name("" + ChatColor.WHITE + "Medic").build(), "gun_medic");
    
        BasicKit medicKit = new BasicKit("medic", "Medic",
                medicStack,
                player -> Arrays.asList(medicStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator());
    
        medicKit.addEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
    
        addKit(medicKit);
    }
    
    public Game getPaintballGame() {
        return paintball;
    }
    
    public void applyKitsAndModules() {
        paintball.getModuleManager().addModules(new ArrayList<>(moduleMap.values()));
        
        for (Kit kit : kitMap.values()) {
            paintball.getKitManager().addKit(kit);
        }
    }
    
    public GunModule getGunModule() {
        return gunModule;
    }
    
    public ReviveModule getReviveModule() {
        return reviveModule;
    }
    
    public ArmorPaintingModule getArmorPaintingModule() {
        return armorPaintingModule;
    }
    
    public Kit getKit(String kitName) {
        return kitMap.get(kitName);
    }
    
    public void addKit(Kit kit) {
        kitMap.put(kit.getName(), kit);
    }
    
    public void removeKit(String kitName) {
        kitMap.remove(kitName);
    }
    
    public Module getModule(String name) {
        return moduleMap.get(name);
    }
    
    public void addModule(String name, Module module) {
        moduleMap.put(name, module);
    }
    
    public void removeModule(String name) {
        moduleMap.remove(name);
    }
    
    public void setShowNametags(boolean showNametags) {
        paintball.getConfigManager().getProperty("show_enemy_nametags").setDefaultValue(false);
        paintball.getConfigManager().getProperty("show_enemy_nametags").setValue(false);
    }
}
