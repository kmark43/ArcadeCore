package net.mutinies.arcadecore.arcade.classic;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.api.StartResult;
import net.mutinies.arcadecore.api.StopResult;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.game.map.LobbyMap;
import net.mutinies.arcadecore.game.scoreboard.ScoreboardManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.prevent.NoBuildModule;
import net.mutinies.arcadecore.modules.prevent.NoDamageModule;
import net.mutinies.arcadecore.modules.prevent.NoHungerChangeModule;
import net.mutinies.arcadecore.util.ModuleUtil;
import net.mutinies.arcadecore.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassicGameManager implements GameManager {
    private LobbyMap lobbyMap;
    
    private ScoreboardManager scoreboardManager;
    private Game activeGame;
    private boolean gameRunning;
    
    private BukkitTask countdownTask;
    private int timeLeft;
    
    private boolean disabling = false;
    
    private List<Module> lobbyModules;
    
    @Override
    public void enable() {
        this.scoreboardManager = new ScoreboardManager();
        Bukkit.getPluginManager().registerEvents(scoreboardManager, ArcadeCorePlugin.getInstance());
    
        scoreboardManager.setLineFunction(player -> {
            String kit = getGame() != null && getGame().getKitManager().getKit(player) != null ?
                    getGame().getKitManager().getKit(player).getDisplayName() : null;
            String map = getMap() != null ? getMap().getDisplayName() : "None";
        
            List<String> lines = new ArrayList<>();
            
            if (kit != null) {
                lines.add("");
                lines.add(ChatColor.BOLD + "Kit");
                lines.add(kit);
            }
            
            lines.add("");
            lines.add(ChatColor.BOLD + "Map");
            lines.add(map);
            return lines;
        });
        
        File lobbyMapFile = new File(ArcadeCorePlugin.getInstance().getDataFolder(), "/lobby.json");
        try {
            lobbyMap = new LobbyMap(lobbyMapFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(ArcadeCorePlugin.getInstance());
            return;
        }
    
        lobbyMap.loadWorld();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getLocation().getWorld().equals(lobbyMap.getWorld())) {
                player.teleport(lobbyMap.getMainSpawn().getLocation());
            }
        }
        
        lobbyModules = Arrays.asList(
                new NoDamageModule(),
                new NoBuildModule(),
                new NoHungerChangeModule()
        );
        
        startLobbyState();
    }
    
    @Override
    public void disable() {
        disabling = true;
        endLobbyState();
        
        if (gameRunning) {
            getGame().getGameStateManager().quickStop();
        } else {
            scoreboardManager.disable();
            HandlerList.unregisterAll(scoreboardManager);
        }
    }
    
    private void startLobbyState() {
        scoreboardManager.enable();
        scoreboardManager.setTitle(ChatColor.BOLD + "Waiting for players");
        updateCountdownTask();
        for (Player player : Bukkit.getOnlinePlayers()) {
            initLobbyPlayer(player);
        }
    
        ModuleUtil.enableModules(lobbyModules);
    }
    
    private void endLobbyState() {
        ModuleUtil.disableModules(lobbyModules);
        stopCountdown();
        scoreboardManager.disable();
    }
    
    private void initLobbyPlayer(Player player) {
        if (!player.getWorld().equals(lobbyMap.getWorld())) {
            player.teleport(lobbyMap.getMainSpawn().getLocation());
        }
        PlayerUtil.setDefaultPlayerState(player);
        if (getGame() != null) {
            getGame().getKitManager().setDefaultKits();
            getGame().getKitManager().giveKitSelectionItems();
        }
    }
    
    @Override
    public void handleGameStop() {
        gameRunning = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(lobbyMap.getWorld())) {
                player.teleport(lobbyMap.getMainSpawn().getLocation());
            }
        }
        if (!disabling) {
            Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), this::startLobbyState);
        }
    }
    
    private void updateCountdownTask() {
        if (getGame() == null || isGameRunning()) return;
        
        int numPlayers = Bukkit.getOnlinePlayers().size();
        
        if (countdownTask != null && numPlayers <= 1) {
            stopCountdown();
            scoreboardManager.setTitle(ChatColor.BOLD + "Waiting for players");
        } else if (countdownTask == null && numPlayers >= 2) {
            timeLeft = 21;
            if (getMap() == null) {
                chooseRandomMap();
            }
            startCountdown();
        }
    }
    
    private void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }
    
    private void startCountdown() {
        if (countdownTask == null) {
            countdownTask = Bukkit.getScheduler().runTaskTimer(ArcadeCorePlugin.getInstance(), this::updateCountdown, 0, 20L);
        }
    }
    
    private void updateCountdown() {
        timeLeft--;
        scoreboardManager.setTitle(ChatColor.BOLD + "" + timeLeft + " Seconds Left");
        if (timeLeft <= 0) {
            stopCountdown();
            startGame();
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (isGameRunning()) {
            e.getPlayer().teleport(getMap().getMainSpawn().getLocation());
            getGame().getSpectateManager().spectatePlayer(e.getPlayer());
        } else {
            initLobbyPlayer(e.getPlayer());
            updateCountdownTask();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!isGameRunning()) {
            Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), this::updateCountdownTask);
        }
    }
    
    @Override
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    @Override
    public void setGame(String gameName) {
        if (!isGameRunning() && (getGame() == null || !getGame().getName().equals(gameName))) {
            if (getMap() != null) {
                getGame().getMapManager().clearMap();
            }
            activeGame = ArcadeCorePlugin.getInstance().getArcadeManager().getGame(gameName);
            chooseRandomMap();
            updateCountdownTask();
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                initLobbyPlayer(player);
            }
        }
    }
    
    @Override
    public Game getGame() {
        return activeGame;
    }
    
    @Override
    public void setMap(String gameMap) {
        if (getGame() == null) return;
        GameMap map = activeGame.getMapManager().getMap(gameMap);
        activeGame.getMapManager().setCurrentMap(map);
    }
    
    @Override
    public void chooseRandomMap() {
        if (getGame() == null) return;
        activeGame.getMapManager().chooseRandomMap();
    }
    
    @Override
    public GameMap getMap() {
        return activeGame != null ? activeGame.getMapManager().getCurrentMap() : null;
    }
    
    @Override
    public StartResult startGame(String gameName, String gameMap) {
        if (isGameRunning()) {
            return StartResult.INVALID_STATE;
        }
        
        if (activeGame == null || activeGame.getName().equals(gameName)) {
            setGame(gameName);
        }
        
        if (getMap() == null || !getMap().getName().equals(gameMap)) {
            setMap(gameMap);
        }
        
        gameRunning = true;
        endLobbyState();
        
        getGame().getGameStateManager().start();
        
        return StartResult.STARTED;
    }
    
    @Override
    public StopResult stopGame() {
        if (!isGameRunning()) {
            return StopResult.INVALID_STATE;
        }
        getGame().getGameStateManager().stop();
        return StopResult.STOPPED;
    }
}