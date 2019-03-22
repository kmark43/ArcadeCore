package net.mutinies.arcadecore.game.team;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.PlayerQueueTeamEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.config.ConfigProperty;
import net.mutinies.arcadecore.game.config.ConfigType;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.util.MutiniesColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class TeamManager implements Module {
    private Game game;
    
    private Map<String, GameTeam> teamMap;
    
    private Map<UUID, GameTeam> playerMap;
    private List<GameTeam> teams;
    private Scoreboard scoreboard;
    
    private Map<String, Set<UUID>> queued;
    
    public TeamManager(Game game) {
        this.game = game;
        teamMap = new HashMap<>();
        queued = new HashMap<>();
        teams = new ArrayList<>();
    
        createTeam("players", "Players", MutiniesColor.PURPLE);
    
        createTeam("red", "Red", MutiniesColor.RED);
        createTeam("blue", "Blue", MutiniesColor.BLUE);
        createTeam("green", "Green", MutiniesColor.GREEN);
        createTeam("orange", "Orange", MutiniesColor.ORANGE);
        createTeam("pink", "Pink", MutiniesColor.PINK);
        createTeam("aqua", "Aqua", MutiniesColor.AQUA);
        createTeam("lime", "Lime", MutiniesColor.LIME);
        createTeam("yellow", "Yellow", MutiniesColor.YELLOW);
        
//        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.BOOLEAN, "friendly_fire", false));
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.BOOLEAN, "show_enemy_nametags", true));
        game.getConfigManager().registerProperty(new ConfigProperty(ConfigType.BOOLEAN, "show_ally_nametags", true));
    }
    
    @Override
    public void enable() {
        playerMap = new HashMap<>();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        
        for (GameTeam team : teams) {
            if (scoreboard.getTeam(team.getName()) == null) {
                Team t = scoreboard.registerNewTeam(team.getName());
                t.setDisplayName(team.getDisplayName());
                t.setPrefix(team.getColor().getChatColor().toString());
            } else {
                Set<String> entries = scoreboard.getTeam(team.getName()).getEntries();
                for (String entry : entries) {
                    scoreboard.getTeam(team.getName()).removeEntry(entry);
                }
            }
            
            boolean showEnemyNametags = (boolean)game.getConfigManager().getProperty("show_enemy_nametags").getValue();
            boolean showAllyNametags = (boolean)game.getConfigManager().getProperty("show_ally_nametags").getValue();
            
            if (!showAllyNametags && !showEnemyNametags) {
                scoreboard.getTeam(team.getName()).setNameTagVisibility(NameTagVisibility.NEVER);
            } else if (!showAllyNametags) {
                scoreboard.getTeam(team.getName()).setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);
            } else if (!showEnemyNametags) {
                scoreboard.getTeam(team.getName()).setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            } else {
                scoreboard.getTeam(team.getName()).setNameTagVisibility(NameTagVisibility.ALWAYS);
            }
        }
    }
    
    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setTeam(player, null);
        }
        
        for (GameTeam team : teams) {
            Team t = scoreboard.getTeam(team.getName());
            if (t != null) {
                Set<String> entries = scoreboard.getTeam(team.getName()).getEntries();
                for (String entry : entries) {
                    scoreboard.getTeam(team.getName()).removeEntry(entry);
                }
                t.unregister();
            }
        }
        
        playerMap = null;
    }
    
    public void assignTeams(List<GameTeam> assignableTeams) {
        List<Player> players = new ArrayList<>(ArcadeCorePlugin.getParticipants());
        Collections.shuffle(assignableTeams);
        Collections.shuffle(players);
        
        int maxTeamSize = (int)Math.ceil((double)players.size() / assignableTeams.size());
        for (GameTeam team : assignableTeams) {
            Set<UUID> toAdd = queued.get(team.getName());
            if (toAdd == null) continue;
            Iterator<UUID> queue = toAdd.iterator();
            for (int i = 0; i < maxTeamSize && queue.hasNext(); i++) {
                Player player;
                
                do {
                    player = Bukkit.getPlayer(queue.next());
                } while (player == null && queue.hasNext());
                
                if (player != null) {
                    setTeam(player, team);
                }
            }
        }
        
        queued.clear();
        
        for (Player player : players) {
            if (getTeam(player) != null) continue;
            GameTeam smallestTeam = null;
            int smallestNumPlayers = Integer.MAX_VALUE;
            for (GameTeam team : assignableTeams) {
                if (team.getPlayers().size() < smallestNumPlayers) {
                    smallestNumPlayers = team.getPlayers().size();
                    smallestTeam = team;
                }
            }
            setTeam(player, smallestTeam);
        }
    }
    
    public void createTeam(String teamName, String displayName, MutiniesColor color) {
        GameTeam team = new GameTeam(teamName, displayName, color);
        teams.add(team);
        teamMap.put(teamName, team);
    }
    
    public GameTeam getTeam(String teamName) {
        return teamMap.get(teamName);
    }
    
    public List<GameTeam> getTeams() {
        return teams;
    }
    
    public GameTeam getTeam(Player player) {
        return teamMap == null ? null : playerMap.get(player.getUniqueId());
    }
    
    public void setTeam(Player player, GameTeam team) {
        GameTeam oldTeam = getTeam(player);
        if (oldTeam != null) {
            oldTeam.removePlayer(player);
            if (scoreboard.getTeam(oldTeam.getName()).hasEntry(player.getName())) {
                scoreboard.getTeam(oldTeam.getName()).removeEntry(player.getName());
            }
            playerMap.remove(player.getUniqueId());
        }
        
        if (team != null) {
            team.addPlayer(player);
            scoreboard.getTeam(team.getName()).addEntry(player.getName());
            playerMap.put(player.getUniqueId(), team);
        }
    }
    
    public Set<Player> getQueued(GameTeam team) {
        Set<UUID> uuids = queued.get(team.getName());
        if (uuids == null) {
            return new HashSet<>();
        } else {
            return uuids.stream().map(Bukkit::getPlayer).collect(Collectors.toSet());
        }
    }
    
    public GameTeam getQueuedTeam(Player player) {
        for (String teamName : queued.keySet()) {
            Set<UUID> uuids = queued.get(teamName);
            if (uuids.contains(player.getUniqueId())) {
                return getTeam(teamName);
            }
        }
        return null;
    }
    
    /**
     * Queues the given player for the given team
     * @param player The player to queue
     * @return The index the player has in this team
     */
    public int queueTeam(Player player, GameTeam team) {
        if (!queued.containsKey(team.getName())) {
            queued.put(team.getName(), new LinkedHashSet<>());
        }
        Set<UUID> uuids = queued.get(team.getName());
        if (uuids.contains(player.getUniqueId())) {
            int i = 0;
            for (UUID uuid : uuids) {
                if (player.getUniqueId().equals(uuid)) {
                    return i;
                }
                i++;
            }
            return -1; // should never reach here
        } else {
            removeQueuedTeam(player);
            uuids.add(player.getUniqueId());
            Bukkit.getPluginManager().callEvent(new PlayerQueueTeamEvent(player, team, uuids.size(), uuids.size()));
            return uuids.size() - 1;
        }
    }
    
    public void removeQueuedTeam(Player player) {
        Iterator<String> teamNameIterator = queued.keySet().iterator();
        while (teamNameIterator.hasNext()) {
            String teamName = teamNameIterator.next();
            Set<UUID> uuids = queued.get(teamName);
            if(uuids.contains(player.getUniqueId())) {
                uuids.remove(player.getUniqueId());
                Bukkit.getPluginManager().callEvent(new PlayerQueueTeamEvent(player, getTeam(teamName), uuids.size(), uuids.size()));
                if (uuids.isEmpty()) {
                    teamNameIterator.remove();
                }
            }
        }
    }
    
    public List<GameTeam> getTeamsWithPlayers() {
        List<GameTeam> teams = new ArrayList<>();
        for (GameTeam team : this.teams) {
            if (!team.getPlayers().isEmpty()) {
                teams.add(team);
            }
        }
        return teams;
    }
    
    public List<GameTeam> getLivingTeams() {
        List<GameTeam> livingTeams = new ArrayList<>();
        for (GameTeam team : teams) {
            if (!team.getLivingPlayers().isEmpty()) {
                livingTeams.add(team);
            }
        }
        return livingTeams;
    }
    
    public List<Player> getLivingPlayers() {
        List<Player> players = new ArrayList<>();
        for (GameTeam team : teams) {
            players.addAll(team.getLivingPlayers());
        }
        return players;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        setTeam(e.getPlayer(), null);
        removeQueuedTeam(e.getPlayer());
    }
}
