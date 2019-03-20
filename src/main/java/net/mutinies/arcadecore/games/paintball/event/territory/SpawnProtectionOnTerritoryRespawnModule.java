package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.spawn.SpawnProtectionModule;
import org.bukkit.event.EventHandler;

public class SpawnProtectionOnTerritoryRespawnModule implements Module {
    private Game game;
    private SpawnProtectionModule spawnProtectionModule;
    
    public SpawnProtectionOnTerritoryRespawnModule(Game game, int protection) {
        this.game = game;
        spawnProtectionModule = new SpawnProtectionModule();
        game.getModuleManager().addModules(spawnProtectionModule);
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.INT, "spawn_protection", protection));
    }
    
    @EventHandler
    public void onTerritoryRespawn(TerritoryRespawnEvent e) {
        int cooldown = (int)game.getConfigManager().getProperty("spawn_protection").getValue();
        
        if (cooldown > 0) {
            spawnProtectionModule.protect(e.getPlayer(), cooldown);
        }
    }
}
