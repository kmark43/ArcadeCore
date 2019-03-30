package net.mutinies.arcadecore.games;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.game.kit.BasicKit;
import net.mutinies.arcadecore.game.kit.armor.TeamArmorGenerator;
import net.mutinies.arcadecore.games.oitq.GiveArrowOnKillModule;
import net.mutinies.arcadecore.games.oitq.GiveKitOnRespawnModule;
import net.mutinies.arcadecore.games.oitq.ProjectileLaunchTagger;
import net.mutinies.arcadecore.games.oitq.event.ArrowBounceHandler;
import net.mutinies.arcadecore.games.oitq.event.InstantKillHandler;
import net.mutinies.arcadecore.games.paintball.PaintBlockModule;
import net.mutinies.arcadecore.games.paintball.event.spleef.BreakBlockModule;
import net.mutinies.arcadecore.games.paintball.event.territory.GivePotionOnKillModule;
import net.mutinies.arcadecore.games.paintball.event.territory.PaintingTerritoryClaimModule;
import net.mutinies.arcadecore.games.paintball.event.territory.PreventTerritoryPaintModule;
import net.mutinies.arcadecore.games.paintball.event.territory.SpawnProtectionOnTerritoryRespawnModule;
import net.mutinies.arcadecore.modules.DelayedRespawnModule;
import net.mutinies.arcadecore.modules.KillComboModule;
import net.mutinies.arcadecore.modules.gamescore.PlayerEliminationModule;
import net.mutinies.arcadecore.modules.gamescore.PlayerKillTargetModule;
import net.mutinies.arcadecore.modules.kit.DoubleJumpModule;
import net.mutinies.arcadecore.modules.prevent.TeleportToRandomSpawnpointOnReviveModule;
import net.mutinies.arcadecore.modules.territory.TerritoryModule;
import net.mutinies.arcadecore.util.ItemBuilder;
import net.mutinies.arcadecore.util.ModuleUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GameMaker {
    public static void makeDefaultGames() {
        makeTestGame();
        makePaintball();
        makeTerritoryPaintball();
        makeSpleefPaintball();
        makeOITQ();
    }
    
    private static void makeTestGame() {
        Game testGame = new Game("test", "Test", "T", 2, 16);
    
        testGame.getModuleManager().addModules(ModuleUtil.getPvpList());
        testGame.setEndHandler(new PlayerEliminationModule(testGame, true));
    
        ItemStack swordStack = ItemBuilder.of(Material.DIAMOND_SWORD).unbreakable().build();
        ItemStack bowStack = ItemBuilder.of(Material.BOW).unbreakable().build();
        ItemStack arrowStack = new ItemStack(Material.ARROW, 32);
        testGame.getKitManager().addKit(new BasicKit("test", "Test", swordStack, player -> Arrays.asList(swordStack, bowStack, arrowStack), new TeamArmorGenerator()));
    
        ArcadeCorePlugin.getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), testGame);
    }
    
    private static void makePaintball() {
        Game paintball = new Game("paintball", "Paintball", "PB", 2, 16);
        
        PaintballMaker paintballMaker = new PaintballMaker(paintball);
        paintballMaker.applyKitsAndModules();

        ArcadeCorePlugin.getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), paintball);
    }
    
    private static void makeTerritoryPaintball() {
        Game paintball = new Game("territory_paintball", "Territory Paintball", "TPB", 2, 40);
        
        PaintballMaker paintballMaker = new PaintballMaker(paintball);
        TerritoryModule territoryModule = new TerritoryModule(paintball, 1000, 20 * 5);
        paintball.setEndHandler(territoryModule);
        paintballMaker.addModule("territory_claim", new PaintingTerritoryClaimModule(paintball, territoryModule));
        paintballMaker.addModule("prevent_territory_paint", new PreventTerritoryPaintModule(territoryModule));
        paintballMaker.addModule("give_potion_module", new GivePotionOnKillModule(paintballMaker.getReviveModule()));
        paintballMaker.addModule("spawn_protection", new SpawnProtectionOnTerritoryRespawnModule(paintball, 20));
        paintballMaker.applyKitsAndModules();

        ArcadeCorePlugin.getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), paintball);
    }
    
    private static void makeSpleefPaintball() {
        Game paintball = new Game("spleef_paintball", "Spleef Paintball", "SPB", 2, 16);
    
        DoubleJumpModule generalDoubleJumpModule = new DoubleJumpModule(.9, .9, true);
        
        PaintballMaker paintballMaker = new PaintballMaker(paintball);
//        paintballMaker.addModule("paint_block", new PaintBlockModule(paintball, 4.5));
        paintball.getConfigManager().registerProperty(new ConfigProperty(ConfigType.DOUBLE, "radius_scale", 0.9));
        paintballMaker.removeModule("paint_block");
        paintballMaker.setShowNametags(true);
        
        paintballMaker.getGunModule().unregisterGun("medic");
        paintballMaker.removeKit("medic");
        
        paintballMaker.getGunModule().getGun("rifle").addLaunchHandler(((gun, player, projectile) -> projectile.addHitBlockHandler(new BreakBlockModule(paintball, 3d, 4.5))));
        paintballMaker.getKit("rifle").addModule(generalDoubleJumpModule);
    
        paintballMaker.getGunModule().getGun("shotgun").addLaunchHandler(((gun, player, projectile) -> projectile.addHitBlockHandler(new BreakBlockModule(paintball, 2d, 4.5))));
        paintballMaker.getKit("shotgun").addModule(generalDoubleJumpModule);
    
        paintballMaker.getGunModule().getGun("machine_gun").addLaunchHandler(((gun, player, projectile) -> projectile.addHitBlockHandler(new BreakBlockModule(paintball, 2d, 4.5))));
        paintballMaker.getKit("machine_gun").addModule(generalDoubleJumpModule);
    
        paintballMaker.getGunModule().getGun("sniper").addLaunchHandler(((gun, player, projectile) -> projectile.addHitBlockHandler(new BreakBlockModule(paintball, 3d, 4.5))));
        paintballMaker.getKit("sniper").addModule(generalDoubleJumpModule);
    
        paintballMaker.getGunModule().getGun("bazooka").addLaunchHandler(((gun, player, projectile) -> projectile.addHitBlockHandler(new BreakBlockModule(paintball, 4d, 4.5))));
        paintballMaker.getKit("bazooka").addModule(generalDoubleJumpModule);
    
        paintballMaker.getGunModule().getGun("needler").addLaunchHandler(((gun, player, projectile) -> projectile.addHitBlockHandler(new BreakBlockModule(paintball, 3d, 4.5))));
        paintballMaker.getKit("needler").addModule(generalDoubleJumpModule);
        
        paintballMaker.applyKitsAndModules();
        
        ArcadeCorePlugin.getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), paintball);
    }
    
    public static void makeOITQ() {
        Game oitq = new Game("one_in_the_quiver", "One in the Quiver", "OITQ", 2, 16);
        oitq.setEndHandler(new PlayerKillTargetModule((short) 20, oitq, true));
    
        KillComboModule comboModule = new KillComboModule();
        comboModule.addComboType("TRIPLE KILL", 3);
        comboModule.addComboType("GODLIKE", 5);
        comboModule.addComboType("UNSTOPPABLE", 7);
        comboModule.addComboType("ULTRA KILL", 9);
        comboModule.addComboType("MONSTER KILL", 11);
        comboModule.addComboType("MEGA KILL", 13);
        comboModule.addComboType("PERFECT RUN", 20);
    
        ProjectileLaunchTagger launchTagger = new ProjectileLaunchTagger();
        launchTagger.registerLaunchHandler(((player, projectile) -> projectile.addDamageHandler(new InstantKillHandler())));
    
        oitq.getModuleManager().addModules(ModuleUtil.getPvpList());
        
        oitq.getModuleManager().addModules(comboModule,
                launchTagger,
                new GiveArrowOnKillModule(),
                new GiveKitOnRespawnModule(),
                new DelayedRespawnModule(5),
                new TeleportToRandomSpawnpointOnReviveModule());
    
        BasicKit jumperKit = new BasicKit("jumper", "Jumper", new ItemStack(Material.IRON_AXE),
                player -> Arrays.asList(ItemBuilder.of(Material.IRON_AXE).unbreakable().build(),
                        ItemBuilder.of(Material.BOW).unbreakable().build(),
                        new ItemStack(Material.ARROW)));
        
        jumperKit.addModule(new DoubleJumpModule(.9, .9, true));
    
        oitq.getKitManager().addKit(jumperKit);
    
        BasicKit brawlerKit = new BasicKit("brawler", "Brawler", new ItemStack(Material.IRON_SWORD),
                player -> Arrays.asList(ItemBuilder.of(Material.IRON_SWORD).unbreakable().build(),
                        ItemBuilder.of(Material.BOW).unbreakable().build(),
                        new ItemStack(Material.ARROW)));
        
        oitq.getKitManager().addKit(brawlerKit);
    
        BasicKit enchanterKit = new BasicKit("enchanter", "Enchanter", new ItemStack(Material.STONE_SWORD),
                player -> Arrays.asList(ItemBuilder.of(Material.STONE_SWORD).unbreakable().build(),
                        ItemBuilder.of(Material.BOW).unbreakable().build(),
                        new ItemStack(Material.ARROW)));
        
        launchTagger.registerLaunchHandler(((player, projectile) -> {
            if (oitq.getKitManager().getKit(player).getName().equals("enchanter")) {
                ArrowBounceHandler bounceHandler = new ArrowBounceHandler(2, 1.2f);
                bounceHandler.addIgnored(player);
                projectile.addDamageHandler(bounceHandler);
            }
        }));
        
        oitq.getKitManager().addKit(enchanterKit);
        
        ArcadeCorePlugin.getArcadeManager().registerGame(ArcadeCorePlugin.getInstance(), oitq);
    }
}
