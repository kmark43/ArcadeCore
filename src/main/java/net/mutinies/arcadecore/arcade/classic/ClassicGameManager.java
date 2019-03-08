package net.mutinies.arcadecore.arcade.classic;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.api.GameManager;
import net.mutinies.arcadecore.api.StartResult;
import net.mutinies.arcadecore.api.StopResult;
import net.mutinies.arcadecore.arcade.participation.ParticipationManager;
import net.mutinies.arcadecore.event.GameSetEvent;
import net.mutinies.arcadecore.event.PlayerDisableParticipationEvent;
import net.mutinies.arcadecore.event.PlayerEnableParticipationEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.game.map.LobbyMap;
import net.mutinies.arcadecore.game.scoreboard.ScoreboardManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.prevent.*;
import net.mutinies.arcadecore.util.ModuleUtil;
import net.mutinies.arcadecore.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
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
    
    private ParticipationManager participationManager;
    
    private BukkitTask countdownTask;
    private int timeLeft;
    
    private boolean disabling = false;
    
    private List<Module> lobbyModules;
    
    @Override
    public void enable() {
        this.scoreboardManager = new ScoreboardManager();
    
        this.participationManager = new ParticipationManager(true);
        ModuleUtil.enableModules(Arrays.asList(participationManager));
    
        scoreboardManager.setLineFunction(player -> {
            String kit = getGame() != null && getGame().getKitManager().getKit(player) != null ?
                    getGame().getKitManager().getKit(player).getDisplayName() : null;
            String map = getMap() != null ? getMap().getDisplayName() : "None";
        
            List<String> lines = new ArrayList<>();
            
            lines.add("");
            lines.add(ChatColor.BOLD + "Players");
            lines.add(participationManager.getParticipants().size() + "/" + getGame().getMaxPlayers());
            
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
    
        Bukkit.getPluginManager().registerEvents(scoreboardManager, ArcadeCorePlugin.getInstance());
        
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
                new NoHungerChangeModule(),
                new NoInventoryChange(),
                new NoNaturalChangesModule()
        );
        
        ArcadeCorePlugin.getInstance().getCommand("spec").setExecutor(new SpecExecutor());
        ArcadeCorePlugin.getInstance().getCommand("spec").setTabCompleter(new SpecExecutor());
        
        ArcadeCorePlugin.getInstance().getCommand("game").setExecutor(new GameCommandExecutor());
        ArcadeCorePlugin.getInstance().getCommand("game").setTabCompleter(new GameCommandExecutor());
        
        ArcadeCorePlugin.getInstance().getCommand("map").setExecutor(new MapCommandExecutor());
        ArcadeCorePlugin.getInstance().getCommand("map").setTabCompleter(new MapCommandExecutor());
        
        ArcadeCorePlugin.getInstance().getCommand("config").setExecutor(new ConfigCommandExecutor());
        ArcadeCorePlugin.getInstance().getCommand("config").setTabCompleter(new ConfigCommandExecutor());
        
        Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), this::startLobbyState);
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
        participationManager = null;
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
        // todo add spectate and unspectate events/listeners that call this
        if (getGame() == null || isGameRunning()) return;
        
        int numPlayers = ArcadeCorePlugin.getParticipants().size();
        
        if (countdownTask != null && numPlayers <= 1) {
            stopCountdown();
            scoreboardManager.setTitle(ChatColor.BOLD + "Waiting for players");
        } else if (countdownTask == null && numPlayers >= 2) {
            timeLeft = ArcadeCorePlugin.getInstance().getConfig().getInt("countdownTime") + 1;
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
    
    @EventHandler
    public void onPlayerEnableParticipation(PlayerEnableParticipationEvent e) {
        if (!isGameRunning()) {
            Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), this::updateCountdownTask);
        }
    }
    
    @EventHandler
    public void onPlayerDisableParticipation(PlayerDisableParticipationEvent e) {
        if (!isGameRunning()) {
            Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), this::updateCountdownTask);
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!isGameRunning()) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.setCancelled(true);
                e.getEntity().teleport(lobbyMap.getMainSpawn().getLocation());
            }
        }
    }
    
    @Override
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    @Override
    public void setGame(String gameName) {
        Game oldGame = getGame();
        if (!isGameRunning() && (oldGame == null || !oldGame.getName().equals(gameName))) {
            if (getMap() != null) {
                getGame().getMapManager().clearMap();
            }
            activeGame = ArcadeCorePlugin.getArcadeManager().getGame(gameName);
            chooseRandomMap();
            updateCountdownTask();
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                initLobbyPlayer(player);
            }
            Bukkit.getPluginManager().callEvent(new GameSetEvent(oldGame, activeGame));
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
    public ParticipationManager getParticipationManager() {
        return participationManager;
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
