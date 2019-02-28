package net.mutinies.arcadecore;

import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.arcade.ArcadeManager;
import net.mutinies.arcadecore.arcade.CompetitiveGameManager;
import net.mutinies.arcadecore.arcade.classic.ClassicGameManager;
import net.mutinies.arcadecore.arcade.lobbyless.LobbylessGameManager;
import net.mutinies.arcadecore.cooldown.CooldownManager;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.games.GameMaker;
import net.mutinies.arcadecore.graphics.inventory.GuiManager;
import net.mutinies.arcadecore.item.ClickEvent;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.manager.ManagerHandler;
import net.mutinies.arcadecore.weather.WorldWeatherPreventer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ArcadeCorePlugin extends JavaPlugin implements Listener {
    private static ArcadeCorePlugin instance;
    
    public static ArcadeCorePlugin getInstance() {
        return instance;
    }
    
    public static ArcadeManager getArcadeManager() {
        return instance.arcadeManager;
    }
    
    public static GameManager getGameManager() {
        return instance.gameManager;
    }
    
    public static Game getGame() {
        return instance.gameManager.getGame();
    }
    
    public static List<Player> getParticipants() {
        return instance.gameManager.getParticipationManager().getParticipants();
    }
    
    public static List<Player> getLivingPlayers() {
        return instance.gameManager.getGame().getTeamManager().getLivingPlayers();
    }
    
    public static List<GameTeam> getLivingTeams() {
        return instance.gameManager.getGame().getTeamManager().getLivingTeams();
    }
    
    public static ManagerHandler getManagerHandler() {
        return instance.managerHandler;
    }
    
    private ManagerHandler managerHandler;
    private ArcadeManager arcadeManager;
    private GameManager gameManager;
    
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
                String defaultGame = getConfig().getString("defaultGame");
                Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> gameManager.setGame(defaultGame));
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
        
        ItemManager itemManager = ArcadeCorePlugin.getManagerHandler().getManager(ItemManager.class);
        itemManager.registerTag("select_kit", e -> {
            e.setCancelled(true);
            if (e.getClickType() == ClickEvent.ClickType.RIGHT) {
                gameManager.getGame().getKitManager().showGameKitGui(e.getPlayer());
            }
        });
        
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
