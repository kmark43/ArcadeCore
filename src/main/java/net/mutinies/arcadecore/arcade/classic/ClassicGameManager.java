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
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.graphics.inventory.InventoryWindow;
import net.mutinies.arcadecore.graphics.inventory.WindowButton;
import net.mutinies.arcadecore.graphics.inventory.event.ClickHandler;
import net.mutinies.arcadecore.item.ClickEvent;
import net.mutinies.arcadecore.item.ItemManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.prevent.*;
import net.mutinies.arcadecore.util.ItemBuilder;
import net.mutinies.arcadecore.util.MessageUtil;
import net.mutinies.arcadecore.util.ModuleUtil;
import net.mutinies.arcadecore.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
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
    private boolean paused = false;
    
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
            String game = getGame() != null ? getGame().getDisplayName() : "None";
        
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
            
            lines.add("");
            lines.add(ChatColor.BOLD + "Game");
            lines.add(game);
            return lines;
        });
    
        ItemManager itemManager = ArcadeCorePlugin.getManagerHandler().getManager(ItemManager.class);
        itemManager.registerTag("queue_team", clickEvent -> {
            if (clickEvent.getClickType() == ClickEvent.ClickType.RIGHT) {
                clickEvent.setCancelled(true);
                clickEvent.getPlayer().updateInventory();
                Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), () -> showTeamQueueingGui(clickEvent.getPlayer()));
            }
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
    
    
        ArcadeCorePlugin.getInstance().getCommand("pause").setExecutor(new PauseExecutor(this));
        
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
    
        ItemManager itemManager = ArcadeCorePlugin.getManagerHandler().getManager(ItemManager.class);
        itemManager.unregister("queue_team");
        participationManager = null;
    }
    
    private void startLobbyState() {
        scoreboardManager.enable();
        scoreboardManager.setTitle(ChatColor.BOLD + "Waiting for players");
        updateCountdownTask();
        if (activeGame != null) {
            activeGame.getKitManager().setDefaultKits();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            initLobbyPlayer(player, false);
        }
    
        ModuleUtil.enableModules(lobbyModules);
    }
    
    private void endLobbyState() {
        ModuleUtil.disableModules(lobbyModules);
        stopCountdown();
        scoreboardManager.disable();
    }
    
    private void initLobbyPlayer(Player player, boolean teleport) {
        PlayerUtil.setDefaultPlayerState(player);
        if (getGame() != null) {
            getGame().getKitManager().giveKitSelectionItem(player);
            giveTeamQueuingItem(player);
        }
        if (!isGameRunning()) {
            if (teleport) {
                player.teleport(lobbyMap.getMainSpawn().getLocation());
            }
        } else {
        
        }
    }
    
    public void pause() {
        if (!isGameRunning()) {
            paused = true;
            updateCountdownTask();
        }
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void unpause() {
        if (!isGameRunning()) {
            paused = false;
            updateCountdownTask();
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
        
        if (countdownTask != null && numPlayers <= 1 || paused) {
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
        scoreboardManager.setTitle(ChatColor.BOLD + "Starting in " + timeLeft + " seconds");
        if (timeLeft <= 0) {
            stopCountdown();
            startGame();
        }
    }
    
    private void giveTeamQueuingItem(Player player) {
        GameTeam queuedTeam = getGame().getTeamManager().getQueuedTeam(player);
        String teamName = queuedTeam != null ? queuedTeam.getColor().getChatColor() + queuedTeam.getDisplayName() : ChatColor.WHITE + "None";
        Color armorColor = queuedTeam != null ? queuedTeam.getColor().getColor() : Color.BLACK;
        
        ItemStack stack = ItemBuilder.of(Material.LEATHER_CHESTPLATE)
                .name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Queued Team" + ChatColor.DARK_GRAY + ": " + teamName)
                .applyMetaChange(meta -> ((LeatherArmorMeta)meta).setColor(armorColor))
                .build();
        
        stack = ItemManager.tag(stack, "queue_team");
        
        player.getInventory().setItem(8, stack);
    }
    
    private void showTeamQueueingGui(Player player) {
        if (getMap() != null) {
            GameTeam queuedTeam = getGame().getTeamManager().getQueuedTeam(player);
            List<String> teamNames = getMap().getParsedTeams();
            InventoryWindow window = new InventoryWindow(ChatColor.DARK_GRAY + "Queue Team");
            int c = 1;
            int r = 1;
    
            {
                ItemBuilder builder = ItemBuilder.of(Material.LEATHER_CHESTPLATE)
                        .applyMetaChange(meta -> ((LeatherArmorMeta) meta).setColor(Color.BLACK));
    
                if (queuedTeam == null) {
                    builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Queued Team" + ChatColor.DARK_GRAY + ": " + ChatColor.RESET + "None");
                    builder.glow();
                } else {
                    builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Unqueue Team");
                }
    
                ItemStack stack = builder.build();
    
                window.set(9 * r + c, new WindowButton(stack, (ClickHandler) e -> {
                    if (getGame().getTeamManager().getQueuedTeam(player) != null) {
                        getGame().getTeamManager().removeQueuedTeam(player);
                        MessageUtil.send(player, "You are no longer queued for a team");
                    }
                    showTeamQueueingGui(player);
                    giveTeamQueuingItem(player);
                }));
    
                c++;
                if (c >= 8) {
                    r++;
                    c = 1;
                }
            }
            
            for (String teamName : teamNames) {
                GameTeam team = getGame().getTeamManager().getTeam(teamName);
                
                ItemBuilder builder = ItemBuilder.of(Material.LEATHER_CHESTPLATE)
                        .applyMetaChange(meta -> ((LeatherArmorMeta) meta).setColor(team.getColor().getColor()));
                
                if (queuedTeam != null && queuedTeam.equals(team)) {
                    builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Queued Team" + ChatColor.DARK_GRAY + ": " + team.getColor().getChatColor() + team.getDisplayName());
                    builder.glow();
                } else {
                    builder.name("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Select Team " + team.getColor().getChatColor() + team.getDisplayName());
                }
                
    
                ItemStack stack = builder.build();
                
                window.set(9 * r + c, new WindowButton(stack, (ClickHandler) e -> {
                    int position = getGame().getTeamManager().queueTeam(player, team) + 1;
                    int size = getGame().getTeamManager().getQueued(team).size();
                    
                    MessageUtil.send(player, "You are " + ChatColor.DARK_GRAY + position + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + size + MessageUtil.DEFAULT + " in queue for " + team.getColor().getChatColor() + team.getDisplayName());
                    showTeamQueueingGui(player);
                    giveTeamQueuingItem(player);
                }));
                
                c++;
                if (c >= 8) {
                    r++;
                    c = 1;
                }
            }
            window.addCloseHandler(e -> Bukkit.getScheduler().runTask(ArcadeCorePlugin.getInstance(), player::updateInventory));
            window.show(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (isGameRunning()) {
            e.getPlayer().teleport(getMap().getMainSpawn().getLocation());
            getGame().getSpectateManager().spectatePlayer(e.getPlayer());
        } else {
            initLobbyPlayer(e.getPlayer(), true);
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
            initLobbyPlayer(e.getPlayer(), false);
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
            
            activeGame.getKitManager().setDefaultKits();
            for (Player player : Bukkit.getOnlinePlayers()) {
                initLobbyPlayer(player, false);
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
        paused = false;
        
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
