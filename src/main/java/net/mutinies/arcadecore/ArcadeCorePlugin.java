package net.mutinies.arcadecore;

import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.arcade.ArcadeManager;
import net.mutinies.arcadecore.arcade.ClassicGameManager;
import net.mutinies.arcadecore.arcade.CompetitiveGameManager;
import net.mutinies.arcadecore.arcade.lobbyless.LobbylessGameManager;
import net.mutinies.arcadecore.cooldown.CooldownManager;
import net.mutinies.arcadecore.games.GameMaker;
import net.mutinies.arcadecore.graphics.inventory.GuiManager;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.manager.ManagerHandler;
import net.mutinies.arcadecore.weather.WorldWeatherPreventer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ArcadeCorePlugin extends JavaPlugin implements Listener {
    private static ArcadeCorePlugin instance;
    
    public static ArcadeCorePlugin getInstance() {
        return instance;
    }
    
    private ManagerHandler managerHandler;
    private ArcadeManager arcadeManager;
    private GameManager gameManager;
    
    public ManagerHandler getManagerHandler() {
        return managerHandler;
    }
    
    public ArcadeManager getArcadeManager() {
        return arcadeManager;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        instance = this;
        managerHandler = new ManagerHandler();
        managerHandler.registerAndEnable(this, new GuiManager(), new WorldWeatherPreventer(), new ItemManager(), new CooldownManager());
        
        Bukkit.getPluginManager().registerEvents(this, this);
        
        String lobbyMode = super.getConfig().getString("arcadeMode");
        
        switch (lobbyMode.toLowerCase()) {
            case "classic":
                Bukkit.getLogger().info("Loading classic arcade");
                gameManager = new ClassicGameManager();
                break;
            case "nolobby":
                Bukkit.getLogger().info("Loading lobbyless arcade");
                gameManager = new LobbylessGameManager();
                break;
            case "competitive":
                Bukkit.getLogger().info("Loading competitive lobby");
                gameManager = new CompetitiveGameManager();
                break;
            default:
                Bukkit.getLogger().severe("Invalid game manager defined, disabling");
                Bukkit.getPluginManager().disablePlugin(this);
        }
    
        gameManager.enable();
        Bukkit.getPluginManager().registerEvents(gameManager, this);
        
        this.arcadeManager = new ArcadeManager();
        
        createSampleGame();
    }
    
    private void createSampleGame() {
        GameMaker.makeDefaultGames();
    }
    
    @Override
    public void onDisable() {
        arcadeManager = null;
        gameManager.disable();
        gameManager = null;
        managerHandler.disable();
        managerHandler = null;
        instance = null;
    }
}
