package net.mutinies.arcadecore.modules.spawn;

import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.event.EventHandler;

public class SpawnProtectionOnReviveModule implements Module {
    private Game game;
    private SpawnProtectionModule spawnProtectionModule;
    
    public SpawnProtectionOnReviveModule(Game game, int protection) {
        this.game = game;
        this.spawnProtectionModule = new SpawnProtectionModule();
        game.getModuleManager().addModules(spawnProtectionModule);
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.INT, "spawn_protection", protection));
    }
    
    @EventHandler
    public void onGameRespawn(GameRespawnEvent e) {
        int cooldown = (int)game.getConfigManager().getProperty("spawn_protection").getValue();
        
        if (cooldown > 0) {
            spawnProtectionModule.protect(e.getPlayer(), cooldown);
        }
    }
}
