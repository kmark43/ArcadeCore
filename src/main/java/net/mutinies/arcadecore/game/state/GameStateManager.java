package net.mutinies.arcadecore.game.state;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameStateSetEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.kit.Kit;
import net.mutinies.arcadecore.game.kit.KitManager;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.game.map.MapManager;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.game.team.TeamManager;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.prevent.NoDamageModule;
import net.mutinies.arcadecore.modules.prevent.NoHungerChangeModule;
import net.mutinies.arcadecore.modules.prevent.NoInteractModule;
import net.mutinies.arcadecore.util.MessageUtil;
import net.mutinies.arcadecore.util.PlayerUtil;
import net.mutinies.arcadecore.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

import static net.mutinies.arcadecore.util.ModuleUtil.disableModules;
import static net.mutinies.arcadecore.util.ModuleUtil.enableModules;

public class GameStateManager implements Module {
    public enum GameState {
        NOT_ACTIVE, STARTING, RUNNING, ENDING
    }
    
    private Game game;
    private GameState state;
    
    private List<Module> generalModules;
    private List<Module> startModules;
    private List<Module> runningModules;
    private List<Module> endingModules;
    
    private BukkitTask changeStateTask;
    
    public GameStateManager(Game game) {
        this.game = game;
        state = GameState.NOT_ACTIVE;
        
        generalModules = Arrays.asList(this, game.getSpectateManager(), game.getDamageManager(), game.getTeamManager(), game.getKitManager(), game.getProjectileManager(), game.getScoreboardManager());
        startModules = Arrays.asList(new NoInteractModule(), new NoDamageModule(), new NoHungerChangeModule());
        runningModules = Arrays.asList();
        endingModules = Arrays.asList();
    }
    
    public void start() {
        setState(GameState.STARTING);
    }
    
    public void stop() {
        setState(GameState.ENDING);
    }
    
    public void quickStop() {
        setState(GameState.NOT_ACTIVE);
    }
    
    private void setState(GameState newState) {
        if (this.state == newState) {
            return;
        }
        
        if (changeStateTask != null) {
            changeStateTask.cancel();
            changeStateTask = null;
        }
        
        GameState oldState = this.state;
        
        switch (oldState) {
            case STARTING:
                disableModules(startModules);
                break;
            case RUNNING:
                disableModules(runningModules);
                
                if (newState != GameState.ENDING) {
                    disableModules(game.getModuleManager().getGameModules());
                    disableModules(getKitModules());
                }
                break;
            case ENDING:
                disableModules(game.getModuleManager().getGameModules());
                disableModules(getKitModules());
                disableModules(endingModules);
                break;
        }
    
        this.state = newState;
        
        switch (newState) {
            case NOT_ACTIVE:
                disableModules(generalModules);
                disableModules(Arrays.asList(game.getEndHandler()));
                ArcadeCorePlugin.getGameManager().handleGameStop();
                game.getMapManager().clearMap();
                break;
            case STARTING:
                int freezeDelay = ArcadeCorePlugin.getInstance().getConfig().getInt("freezeDelay");
                setPlayerStartingStates();
                enableModules(Arrays.asList(game.getEndHandler()));
                enableModules(generalModules);
                enableModules(startModules);
                List<Player> nonParticipants = new ArrayList<>(Bukkit.getOnlinePlayers());
                nonParticipants.removeAll(ArcadeCorePlugin.getParticipants());
                for (Player nonParticipant : nonParticipants) {
                    game.getSpectateManager().spectatePlayer(nonParticipant);
                }
                assignTeams();
                game.getKitManager().setDefaultKits();
                teleportPlayersToGame();
                game.getKitManager().giveKitSelectionItems();
                displayGameStartMessage();
                changeStateTask = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> setState(GameState.RUNNING), 20 * freezeDelay);
                break;
            case RUNNING:
                game.getSpectateManager().getNonspectators().forEach(PlayerUtil::setDefaultPlayerState);
                teleportPlayersToSpawnpoints();
                giveKits();
                enableModules(runningModules);
                enableModules(game.getModuleManager().getGameModules());
                enableModules(getKitModules());
                break;
            case ENDING:
                int endDelay = ArcadeCorePlugin.getInstance().getConfig().getInt("endDelay");
                enableModules(endingModules);
                game.getEndHandler().onWin(game);
                changeStateTask = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> setState(GameState.NOT_ACTIVE), 20 * endDelay);
                break;
        }
        
        Bukkit.getPluginManager().callEvent(new GameStateSetEvent(game, oldState, state));
    }
    
    private void displayGameStartMessage() {
        int freezeDelay = ArcadeCorePlugin.getInstance().getConfig().getInt("freezeDelay");
        String currentMapName = game.getMapManager().getCurrentMap().getDisplayName();
        Bukkit.broadcastMessage(MessageUtil.LINE_SEPARATOR);
        MessageUtil.broadcast("Game", MessageUtil.CATEGORY + "Map" + MessageUtil.SEPARATOR + " - " + MessageUtil.VARIABLE + currentMapName);
        TitleUtil.broadcastTitle("" + ChatColor.LIGHT_PURPLE + game.getDisplayName(), game.getMapManager().getCurrentMap().getDisplayName(), 5, freezeDelay * 20, 15);
    }
    
    private List<Module> getKitModules() {
        Set<Module> kitModules = new HashSet<>();
        List<Kit> kits = game.getKitManager().getKits();
        for (Kit kit : kits) {
            kitModules.addAll(kit.getModules());
        }
        return new ArrayList<>(kitModules);
    }
    
    private void setPlayerStartingStates() {
        for (Player player : ArcadeCorePlugin.getParticipants()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setLevel(0);
            player.setLastDamageCause(null);
            player.setLastDamage(0);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);
            for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
                player.removePotionEffect(effect.getType());
            }
            player.setExp(0);
        }
    }
    
    private void assignTeams() {
        List<GameTeam> teams = game.getMapManager().getCurrentMap().getParsedTeams().stream()
                .map(teamName -> game.getTeamManager().getTeam(teamName)).collect(Collectors.toList());
        game.getTeamManager().assignTeams(teams);
    }
    
    private void teleportPlayersToGame() {
        Location loc = game.getMapManager().getCurrentMap().getMainSpawn().getLocation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(loc);
            clearInventory(player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChangeWorlds(PlayerChangedWorldEvent e) {
        if (state == GameState.STARTING) {
            Player player = e.getPlayer();
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }
    
    private void clearInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.setArmorContents(new ItemStack[4]);
        inventory.setContents(new ItemStack[36]);
    }
    
    private void teleportPlayersToSpawnpoints() {
        MapManager mapManager = game.getMapManager();
        TeamManager teamManager = game.getTeamManager();
        GameMap currentMap = mapManager.getCurrentMap();
        
        for (GameTeam team : teamManager.getTeams()) {
            List<Location> spawnpoints = currentMap.getSpawnpoints(team);
            if (spawnpoints != null) {
                List<UUID> players = new ArrayList<>(team.getPlayers());
                Collections.shuffle(players);
                for (int i = 0; i < players.size(); i++) {
                    Location loc = spawnpoints.get(i % spawnpoints.size());
                    Player player = Bukkit.getPlayer(players.get(i));
                    if (player != null) {
                        player.teleport(loc);
                    }
                }
            }
        }
    }
    
    private void giveKits() {
        KitManager kitManager = game.getKitManager();
        for (Player player : game.getSpectateManager().getNonspectators()) {
            Kit kit = kitManager.getKit(player);
            if (kit == null) {
                kitManager.setKit(player, kitManager.getKits().get(0));
            }
            kit = kitManager.getKit(player);
            clearInventory(player);
            
            kit.giveItems(player);
            kit.giveEffects(player);
        }
    }
    
    public GameState getState() {
        return state;
    }
}
