package net.mutinies.arcadecore.games;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.kit.BasicKit;
import net.mutinies.arcadecore.game.kit.armor.TeamArmorGenerator;
import net.mutinies.arcadecore.games.paintball.ArmorPaintingModule;
import net.mutinies.arcadecore.games.paintball.PaintBlockModule;
import net.mutinies.arcadecore.games.paintball.PaintDeathMessageModule;
import net.mutinies.arcadecore.games.paintball.ReviveModule;
import net.mutinies.arcadecore.games.paintball.gun.Gun;
import net.mutinies.arcadecore.games.paintball.gun.GunModule;
import net.mutinies.arcadecore.games.paintball.gun.ProjectileType;
import net.mutinies.arcadecore.games.paintball.gun.handler.*;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.modules.gamescore.PlayerEliminationModule;
import net.mutinies.arcadecore.modules.gamescore.PlayerKillTargetModule;
import net.mutinies.arcadecore.modules.gamescore.TeamEliminationModule;
import net.mutinies.arcadecore.modules.prevent.NoFriendlyFireModule;
import net.mutinies.arcadecore.modules.prevent.NoPearlTeleportModule;
import net.mutinies.arcadecore.modules.prevent.NoRegenModule;
import net.mutinies.arcadecore.util.ItemBuilder;
import net.mutinies.arcadecore.util.ModuleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GameMaker {
    public static void makeDefaultGames() {
        makeTestGame();
        makePaintball();
    }
    
    private static void makeTestGame() {
        Game testGame = new Game("test", "Test", "T", 2, 16);
    
        testGame.getModuleManager().addModules(ModuleUtil.getPvpList());
        testGame.setEndHandler(new PlayerEliminationModule(testGame, true));
    
        ItemStack swordStack = new ItemStack(Material.DIAMOND_SWORD);
        testGame.getKitManager().addKit(new BasicKit("test", "test", swordStack, player -> Arrays.asList(swordStack), new TeamArmorGenerator()));
    
        ArcadeCorePlugin.getInstance().getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), testGame);
    }
    
    private static void makePaintball() {
        // todo add some death/revive observers to GunModule, add a cleanup method to all the modules, get each called once at end of game
        Game paintball = new Game("paintball", "Paintball", "PB", 2, 16);
        paintball.setEndHandler(new TeamEliminationModule(paintball, true));
        
        paintball.getModuleManager().addModules(ModuleUtil.getPvpList());
        paintball.getConfigManager().getProperty("show_enemy_nametags").setDefaultValue(false);
        paintball.getConfigManager().getProperty("show_enemy_nametags").setValue(false);
    
        GunModule gunModule = new GunModule(paintball);
        ReviveModule reviveModule = new ReviveModule(paintball);
        ArmorPaintingModule armorPaintingModule = new ArmorPaintingModule(paintball);
        
        paintball.getModuleManager().addModules(
                new NoRegenModule(),
                new NoPearlTeleportModule(),
                new PaintBlockModule(paintball),
                new PaintDeathMessageModule(),
                new NoFriendlyFireModule(),
                gunModule,
                reviveModule,
                armorPaintingModule);
        
        Gun rifle = new Gun("rifle", "Rifle", "gun_rifle", 500, ProjectileType.PELLET, 1, new StaticInitialVelocityDeterminer(3, .01));
        rifle.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(15))));
        
        Gun shotgun = new Gun("shotgun", "Shotgun", "gun_shotgun", 1400, ProjectileType.PELLET, 8, new StaticInitialVelocityDeterminer(1.5, .4));
        shotgun.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(5))));
        
        Gun machineGun = new Gun("machine_gun", "Machine Gun", "gun_machine_gun", 150, ProjectileType.PELLET, 1, new StaticInitialVelocityDeterminer(2.4, .25));
        machineGun.addListener(new OverheatingModule("machine_gun_heating", .97, 250, 0.025, 0.020));
        machineGun.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(5))));
        
        Gun sniper = new Gun("sniper", "Sniper", "gun_sniper", 1400, ProjectileType.ARROW, 1, new StaticInitialVelocityDeterminer(10));
        sniper.addScopeHandler(new FreezeWhenScopedHandler());
        sniper.addListener(new ChargingScope(1000, 1, 4, 1));
        
        Gun bazooka = new Gun("bazooka", "Bazooka", "gun_bazooka", 5000, ProjectileType.FIREBALL, 1, new StaticInitialVelocityDeterminer(.8));
        bazooka.addLaunchHandler(((gun, player, projectile) -> projectile.addFlightHandler(new AccelerationHandler())));
        bazooka.addLaunchHandler(((gun, player, projectile) -> projectile.addHitHandler(new AreaOfEffectDamage())));
        
        Gun needler = new Gun("needler", "Needler", "gun_needler", 500, ProjectileType.ARROW, 1, new StaticInitialVelocityDeterminer(10));
        ConsecutiveHitCounter needlerHitCounter = new ConsecutiveHitCounter();
        needler.addListener(needlerHitCounter);
        
        needler.addLaunchHandler(((gun, player, projectile) -> {
            int hits = needlerHitCounter.getNumConsectuveHits(player);
            if (hits > 0 && hits % 3 == 0) {
                projectile.addDamageHandler(new StaticDamageHandler(10));
            } else {
                projectile.addDamageHandler(new StaticDamageHandler(5));
            }
        }));
        
        Gun medic = new Gun("medic", "Medic", "gun_medic", 1000, ProjectileType.PELLET, 4, new StaticInitialVelocityDeterminer(1.5, .6));
        medic.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticDamageHandler(5))));
        medic.addLaunchHandler(((gun, player, projectile) -> projectile.addDamageHandler(new StaticHealHandler(5))));
        medic.addLaunchHandler(new TargetChooser(paintball, true, false));
        
        Gun[] guns = {rifle, shotgun, machineGun, sniper, bazooka, needler, medic};
    
        for (Gun gun : guns) {
            gunModule.registerGun(gun);
        }
    
        ItemStack rifleStack = ItemManager.tag(ItemBuilder.of(Material.IRON_BARDING).name("" + ChatColor.WHITE + "Rifle").build(), "gun_rifle");
        ItemStack shotgunStack = ItemManager.tag(ItemBuilder.of(Material.GOLD_BARDING).name("" + ChatColor.WHITE + "Shotgun").build(), "gun_shotgun");
        ItemStack machineGunStack = ItemManager.tag(ItemBuilder.of(Material.DIAMOND_BARDING).name("" + ChatColor.WHITE + "Machine Gun").build(), "gun_machine_gun");
        ItemStack sniperStack = ItemManager.tag(ItemBuilder.of(Material.STONE_HOE).unbreakable().name("" + ChatColor.WHITE + "Sniper").build(), "gun_sniper");
        ItemStack bazookaStack = ItemManager.tag(ItemBuilder.of(Material.BLAZE_ROD).name("" + ChatColor.WHITE + "Bazooka").build(), "gun_bazooka");
        ItemStack needlerStack = ItemManager.tag(ItemBuilder.of(Material.ARROW).name("" + ChatColor.WHITE + "Needler").build(), "gun_needler");
        ItemStack medicStack = ItemManager.tag(ItemBuilder.of(Material.WOOD_SPADE).unbreakable().name("" + ChatColor.WHITE + "Medic").build(), "gun_medic");

        paintball.getKitManager().addKit(new BasicKit("rifle", "Rifle",
                rifleStack,
                player -> Arrays.asList(rifleStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
        
        paintball.getKitManager().addKit(new BasicKit("shotgun", "Shotgun",
                shotgunStack,
                player -> Arrays.asList(shotgunStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
        
        paintball.getKitManager().addKit(new BasicKit("machine_gun", "Machine Gun",
                machineGunStack,
                player -> Arrays.asList(machineGunStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
        
        paintball.getKitManager().addKit(new BasicKit("sniper", "Sniper",
                sniperStack,
                player -> Arrays.asList(sniperStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
        
        paintball.getKitManager().addKit(new BasicKit("bazooka", "Bazooka",
                bazookaStack,
                player -> Arrays.asList(bazookaStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
        
        paintball.getKitManager().addKit(new BasicKit("needler", "Needler",
                needlerStack,
                player -> Arrays.asList(needlerStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
        
        paintball.getKitManager().addKit(new BasicKit("medic", "Medic",
                medicStack,
                player -> Arrays.asList(medicStack, reviveModule.getReviveStack(player, 3)),
                new TeamArmorGenerator()));
    
        ArcadeCorePlugin.getInstance().getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), paintball);
    }
    
    public static void makeOITQ() {
        Game oitq = new Game("one_in_the_quiver", "One in the Quiver", "OITQ", 2, 16);
        oitq.setEndHandler(new PlayerKillTargetModule((short) 20, oitq, true));
        
        oitq.getKitManager().addKit(new BasicKit("jumper", "Jumper", new ItemStack(Material.STONE_SWORD),
                player -> Arrays.asList(new ItemStack(Material.STONE_SWORD), ItemBuilder.of(Material.BOW).unbreakable().build())));
        
        ArcadeCorePlugin.getInstance().getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), oitq);
    }
}
