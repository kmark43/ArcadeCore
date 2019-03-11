package net.mutinies.arcadecore.games.paintball.event.territory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.event.GameStateSetEvent;
import net.mutinies.arcadecore.event.ProjectileHitBlockEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.state.GameStateManager;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.games.paintball.PaintBlocksEvent;
import net.mutinies.arcadecore.games.paintball.ReviveModule;
import net.mutinies.arcadecore.modules.gamescore.TeamWinHandler;
import net.mutinies.arcadecore.util.JsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class TerritoryModule extends TeamWinHandler {
    private Game game;
    private ReviveModule reviveModule;
    private int targetScore;
    private int respawnTime;
    
    private Map<String, Integer> scoreMap;
    private List<Territory> territories;
    private BukkitTask scoreTask;
    
    private Map<UUID, BukkitTask> respawnTasks;
    
    private GameTeam winner;
    
    public TerritoryModule(Game game, ReviveModule reviveModule, int targetScore, int respawnTime) {
        this.game = game;
        this.reviveModule = reviveModule;
        this.targetScore = targetScore;
        this.respawnTime = respawnTime;
        setScoreboardLines();
    }
    
    @Override
    public void enable() {
        scoreMap = new HashMap<>();
        territories = new ArrayList<>();
        respawnTasks = new HashMap<>();
    
        parseTerritories();
    
        for (String team : game.getMapManager().getCurrentMap().getParsedTeams()) {
            scoreMap.put(team, 0);
        }
    }
    
    @Override
    public void disable() {
        respawnTasks.values().forEach(BukkitTask::cancel);
        scoreMap = null;
        scoreTask = null;
        respawnTasks = null;
        territories = null;
        winner = null;
    }
    
    @EventHandler
    public void onGameStart(GameStateSetEvent e) {
        if (e.getNewState() == GameStateManager.GameState.RUNNING) {
            scoreTask = Bukkit.getScheduler().runTaskTimer(ArcadeCorePlugin.getInstance(),
                    this::incrementScores,
                    20, 20);
        }
    }
    
    @EventHandler
    public void onGameEnd(GameStateSetEvent e) {
        if (e.getNewState() == GameStateManager.GameState.ENDING) {
            scoreTask.cancel();
            scoreTask = null;
        }
    }
    
    private void setScoreboardLines() {
        game.getScoreboardManager().setTitle("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + game.getDisplayName());
        game.getScoreboardManager().setLineFunction((player -> {
            List<String> lines = new ArrayList<>();
            for (String teamName : scoreMap.keySet()) {
                GameTeam team = game.getTeamManager().getTeam(teamName);
                int points = scoreMap.getOrDefault(team.getName(), 0);
                int territories = getClaimedTerritories(team).size();
                int livingPlayers = team.getLivingPlayers().size();

                lines.add("");
                lines.add("" + team.getColor().getChatColor() + ChatColor.BOLD + team.getDisplayName());
                lines.add("" + ChatColor.LIGHT_PURPLE + points + ChatColor.WHITE + " Points");
                lines.add("" + ChatColor.LIGHT_PURPLE + territories + ChatColor.WHITE + " Territories");
                lines.add("" + ChatColor.LIGHT_PURPLE + livingPlayers + ChatColor.WHITE + " Alive");
            }
            return lines;
        }));
    }
    
    @EventHandler
    public void givePotion(GameDeathEvent e) {
        if (e.getKiller() instanceof Player) {
            Player player = (Player)e.getKiller();
            int numPotions = reviveModule.getNumPotions(player, 1);
            if (numPotions < 3) {
//                player.getInventory().addItem(reviveModule.getReviveStack(player, 1));
                reviveModule.setNumPotions(player, 1, numPotions + 1);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        Player player = e.getKilled();
        GameTeam team = game.getTeamManager().getTeam(player);
        
        if (team.getLivingPlayers().size() == 0 &&
                getClaimedTerritories(team).size() == 0 &&
                game.getTeamManager().getLivingTeams().size() <= 1) {
            if (game.getTeamManager().getLivingTeams().size() == 1) {
                winner = game.getTeamManager().getLivingTeams().get(0);
            }
            game.getGameStateManager().stop();
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> {
                respawnTasks.remove(player.getUniqueId());
                List<Territory> claimedTerritories = getClaimedTerritories(team);
                if (!claimedTerritories.isEmpty()) {
                    Territory territory = claimedTerritories.get((int) (Math.random() * claimedTerritories.size()));
                    game.getDamageManager().respawn(player);
                    player.teleport(territory.getCenterLocation().clone().add(0, 1, 0));
                } else if (team.getLivingPlayers().size() == 0 && game.getTeamManager().getLivingTeams().size() <= 1) {
                    if (game.getTeamManager().getLivingTeams().size() == 1) {
                        winner = game.getTeamManager().getLivingTeams().get(0);
                    }
                    game.getGameStateManager().stop();
                }
            }, respawnTime);
    
            respawnTasks.put(player.getUniqueId(), task);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        cancelRespawn(e.getPlayer());
        checkEliminationWin();
    }
    
    private void checkEliminationWin() {
        if (game.getTeamManager().getLivingTeams().size() <= 1) {
            GameTeam winningTeam = game.getTeamManager().getLivingTeams().get(0);
        
            boolean shouldStop = true;
            for (UUID uuid : respawnTasks.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                GameTeam team = game.getTeamManager().getTeam(player);
                assert !game.getDamageManager().isAlive(player);
            
                if (winningTeam != null && !team.equals(winningTeam) && !getClaimedTerritories(team).isEmpty()) {
                    shouldStop = false;
                    break;
                }
            }
            if (shouldStop) {
                winner = winningTeam;
                game.getGameStateManager().stop();
            }
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        cancelRespawn(e.getPlayer());
    }
    
    private void cancelRespawn(Player player) {
        BukkitTask task = respawnTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
    
    @EventHandler
    public void onBlocksPainted(PaintBlocksEvent e) {
        for (Block block : new ArrayList<>(e.getBlocks())) {
            Territory territory = getTerritory(block);
            if (territory != null) {
                e.getBlocks().remove(block);
            }
        }
    }
    
    @EventHandler
    public void onProjectileHitBlock(ProjectileHitBlockEvent e) {
        if (!(e.getProjectile().getShooter() instanceof Player)) return;
        Player shooter = (Player)e.getProjectile().getShooter();
        GameTeam team = game.getTeamManager().getTeam(shooter);
        DyeColor dyeColor = team.getColor().getDyeColor();
    
        Territory territory = getTerritory(e.getHitBlock());
        if (territory == null) return;
        
        String owningTeamName = territory.getOwningTeamName();
        
        Block block = e.getHitBlock();
        BlockState state = block.getState();
        MaterialData data = state.getData();
        
        if (block.equals(territory.getCenterLocation().getBlock())) return;
    
        switch (data.getItemType()) {
            case WOOL:
            case STAINED_CLAY:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case CARPET:
                block.setData(dyeColor.getData());
                break;
        }
        
        if (owningTeamName == null) {
            BlockFace[] relatives = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
            Block center = territory.getCenterLocation().getBlock();
            
            boolean allMatch = true;
            for (BlockFace relative : relatives) {
                Block b = center.getRelative(relative);
                DyeColor bColor = DyeColor.getByData(b.getData());
                if (!bColor.equals(dyeColor)) {
                    allMatch = false;
                    break;
                }
            }
            
            if (allMatch) {
                territory.claim(team);
                Bukkit.getPluginManager().callEvent(new TerritoryClaimEvent(territory, team));
            }
        } else if (!owningTeamName.equals(team.getName())) {
            territory.unclaim();
            GameTeam t = game.getTeamManager().getTeam(owningTeamName);
            checkEliminationWin();
            Bukkit.getPluginManager().callEvent(new TerritoryUnclaimEvent(territory, t));
        }
    }
    
    private void incrementScores() {
        for (Territory territory : territories) {
            String teamName = territory.getOwningTeamName();
            if (teamName == null) continue;
            GameTeam team = game.getTeamManager().getTeam(teamName);
            int score = scoreMap.getOrDefault(team.getName(), 0);
            score++;
            scoreMap.put(team.getName(), score);
            
            if (score >= targetScore) {
                winner = team;
                game.getGameStateManager().stop();
            }
        }
    }
    
    @Override
    public GameTeam getWinningTeam(Game game) {
        return winner;
    }
    
    private void parseTerritories() {
        JsonObject rootObject = game.getMapManager().getCurrentMap().getRootObject();
        JsonArray territoryArray = rootObject.get("territories").getAsJsonArray();
        for (JsonElement territory : territoryArray) {
            Location location = JsonUtil.parseLocation(territory.getAsJsonObject()).subtract(0, 1, 0);
            territories.add(new Territory(location));
        }
    }
    
    private Territory getTerritory(Block block) {
        Territory territory = null;
        for (Territory t : territories) {
            if (block.getY() == t.getCenterLocation().getBlockY() &&
                    Math.abs(block.getX() - t.getCenterLocation().getBlockX()) <= 1 &&
                    Math.abs(block.getZ() - t.getCenterLocation().getBlockZ()) <= 1) {
                
                territory = t;
                break;
            }
        }
        return territory;
    }
    
    private List<Territory> getClaimedTerritories(GameTeam team) {
        return territories.stream().filter(territory -> territory.getOwningTeamName() != null &&
                territory.getOwningTeamName().equals(team.getName()))
                .collect(Collectors.toList());
    }
}