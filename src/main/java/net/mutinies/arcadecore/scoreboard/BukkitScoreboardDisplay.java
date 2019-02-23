package net.mutinies.arcadecore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Deprecated
public class BukkitScoreboardDisplay implements ScoreboardDisplay {
    private static final String OBJECTIVE_NAME = "display";
    private static final String TEAM_PREFIX = "board_team_";
    
    private Scoreboard scoreboard;
    private Objective displayObjective;
    private Team[] teams;
    private int numLines;
    
    public BukkitScoreboardDisplay() {
        this(Bukkit.getScoreboardManager().getNewScoreboard());
    }
    
    public BukkitScoreboardDisplay(Scoreboard scoreboard) {
        this.scoreboard = Objects.requireNonNull(scoreboard);
        
        cleanup();
        
        displayObjective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy");
        displayObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        teams = new Team[15];
        
        for (int i = 0; i < 15; i++) {
            teams[i] = scoreboard.registerNewTeam(TEAM_PREFIX + "");
            teams[i].addEntry("" + ChatColor.values()[i] + ChatColor.RESET);
        }
        
        numLines = 0;
    }
    
    public void cleanup() {
        unregisterObjective();
        unregisterTeams();
    }
    
    public void unregisterObjective() {
        if (scoreboard.getObjective(OBJECTIVE_NAME) != null) {
            scoreboard.getObjective(OBJECTIVE_NAME).unregister();
        }
    }
    
    public void unregisterTeams() {
        for (int i = 0; i < 15; i++) {
            Team team = scoreboard.getTeam(TEAM_PREFIX + i);
            if (team != null) {
                List<String> entries = new ArrayList<>(team.getEntries());
                for (String entry : entries) {
                    team.removeEntry(entry);
                }
            }
        }
    }
    
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    
    @Override
    public void setTitle(String title) {
        displayObjective.setDisplayName(title);
    }
    
    public void show(Player player) {
        Objects.requireNonNull(player);
        player.setScoreboard(scoreboard);
    }
    
    public void clear(Player player) {
        Objects.requireNonNull(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
    
    public void update(List<String> lines) {
        if (lines == null) {
            lines = Collections.emptyList();
        }
        
        int oldNumLines = numLines;
        numLines = Math.min(lines.size(), 16);
        
        assert oldNumLines <= 16;
        
        int end = Math.min(numLines, oldNumLines);
        
        for (int i = 0; i < end; i++) {
            updateEntry(i, lines.get(i));
        }
        
        for (int i = end; i < numLines; i++) {
            addEntry(i);
            updateEntry(i, lines.get(i));
        }
        
        for (int i = end; i < oldNumLines; i++) {
            removeEntry(i);
        }
    }
    
    private void addEntry(int index) {
        if (index < 0 || index >= 16) {
            throw new IndexOutOfBoundsException();
        }
        
        displayObjective.getScore("" + ChatColor.values()[index] + ChatColor.RESET).setScore(15 - index);
    }
    
    private void removeEntry(int index) {
        if (index < 0 || index >= 16) {
            throw new IndexOutOfBoundsException();
        }
        
        scoreboard.resetScores("" + ChatColor.values()[index] + ChatColor.RESET);
    }
    
    private void updateEntry(int index, String value) {
        if (index < 0 || index >= numLines) {
            throw new IndexOutOfBoundsException();
        }
        
        Objects.requireNonNull(value);
        
        if (value.length() <= 16) {
            teams[index].setPrefix(value);
            teams[index].setSuffix("");
        } else {
            int splitIndex = value.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
            String prefix = value.substring(0, splitIndex);
            String lastColor = ChatColor.getLastColors(prefix);
            String suffix = lastColor + value.substring(splitIndex);
            
            teams[index].setPrefix(prefix);
            teams[index].setSuffix(suffix);
        }
    }
}
