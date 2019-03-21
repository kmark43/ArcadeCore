package net.mutinies.arcadecore.game.team;

import net.mutinies.arcadecore.ArcadeCorePlugin;
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

public class TeamManager implements Module {
    private Game game;
    
    private Map<String, GameTeam> teamMap;
    
    private Map<UUID, GameTeam> playerMap;
    private List<GameTeam> teams;
    private Scoreboard scoreboard;
    
    public TeamManager(Game game) {
        this.game = game;
        teamMap = new HashMap<>();
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
        Collections.shuffle(players);
        
        int i = 0;
        
        for (Player player : players) {
            setTeam(player, assignableTeams.get(i % assignableTeams.size()));
            i++;
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
    }
}
