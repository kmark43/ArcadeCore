package net.mutinies.arcadecore.scoreboard;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import net.mutinies.arcadecore.scoreboard.protocol.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class ProtocolScoreboardDisplay implements ScoreboardDisplay {
    private static final String OBJECTIVE_NAME = "display";
    private static final String TEAM_PREFIX = "board_team_";
    
    private String title;
    private List<String> currentLines;
    
    private Set<UUID> shownPlayers;
    
    public ProtocolScoreboardDisplay(String title) {
        this.title = Objects.requireNonNull(title);
        shownPlayers = new HashSet<>();
        currentLines = Collections.emptyList();
    }
    
    @Override
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title);
        WrapperPlayServerScoreboardObjective objectiveWrapper = new WrapperPlayServerScoreboardObjective();
        objectiveWrapper.setPacketMode((byte) WrapperPlayServerScoreboardObjective.Modes.UPDATE_VALUE);
        objectiveWrapper.setObjectiveName(OBJECTIVE_NAME);
        objectiveWrapper.setObjectiveValue(title);
        sendToAll(shownPlayers, objectiveWrapper);
    }
    
    @Override
    public void show(Player player) {
        WrapperPlayServerScoreboardObjective objectiveWrapper = new WrapperPlayServerScoreboardObjective();
        objectiveWrapper.setPacketMode((byte) 0);
        objectiveWrapper.setObjectiveName(OBJECTIVE_NAME);
        objectiveWrapper.setObjectiveValue(title);
        
        objectiveWrapper.sendPacket(player);
        
        WrapperPlayServerScoreboardDisplayObjective displayWrapper = new WrapperPlayServerScoreboardDisplayObjective();
        displayWrapper.setPosition((byte)WrapperPlayServerScoreboardDisplayObjective.Positions.SIDEBAR);
        displayWrapper.setScoreName(OBJECTIVE_NAME);
        
        displayWrapper.sendPacket(player);
    
        for (int i = 0; i < 15; i++) {
            String teamName = TEAM_PREFIX + i;
            String entryName = "" + ChatColor.values()[i] + ChatColor.RESET;
    
            WrapperPlayServerScoreboardTeam teamWrapper = new WrapperPlayServerScoreboardTeam();
            teamWrapper.setPacketMode((byte)WrapperPlayServerScoreboardTeam.Modes.TEAM_CREATED);
            teamWrapper.setTeamName(teamName);
            teamWrapper.setTeamDisplayName("");
            teamWrapper.setTeamPrefix("");
            teamWrapper.setTeamSuffix("");
            teamWrapper.setNameTagVisibility("always");
            teamWrapper.setOptions(0);
            teamWrapper.setColor(0);
            
            ArrayList<String> list = Lists.newArrayList();
            list.add(entryName);
            teamWrapper.setPlayers(list);
            
            teamWrapper.sendPacket(player);
        }
        
        shownPlayers.add(player.getUniqueId());
        
        for (int i = 0; i < currentLines.size(); i++) {
            addEntry(Collections.singletonList(player.getUniqueId()), i);
            updateEntry(Collections.singletonList(player.getUniqueId()), i, currentLines.get(i));
        }
    }
    
    @Override
    public void update(List<String> lines) {
        if (lines == null) {
            lines = Collections.emptyList();
        }
        
        int oldNumLines = currentLines.size();
        currentLines = new ArrayList<>(lines.subList(0, Math.min(lines.size(), 16)));
        int numLines = currentLines.size();
        
        assert oldNumLines <= 16;
        
        int end = Math.min(numLines, oldNumLines);
        
        for (int i = 0; i < end; i++) {
            updateEntry(shownPlayers, i, lines.get(i));
        }
        
        for (int i = end; i < numLines; i++) {
            addEntry(shownPlayers, i);
            updateEntry(shownPlayers, i, lines.get(i));
        }
        
        for (int i = end; i < oldNumLines; i++) {
            removeEntry(shownPlayers, i);
        }
    }
    
    private void addEntry(Collection<UUID> players, int index) {
        if (index < 0 || index >= 16) {
            throw new IndexOutOfBoundsException();
        }
        
        String entryName = "" + ChatColor.values()[index] + ChatColor.RESET;
        int targetScore = 15 - index;
        
        WrapperPlayServerScoreboardScore scoreWrapper = new WrapperPlayServerScoreboardScore();
        scoreWrapper.setPacketMode(EnumWrappers.ScoreboardAction.CHANGE);
        scoreWrapper.setItemName(entryName);
        scoreWrapper.setScoreName(OBJECTIVE_NAME);
        scoreWrapper.setValue(targetScore);
    
        sendToAll(players, scoreWrapper);
    }
    
    private void removeEntry(Collection<UUID> players, int index) {
        if (index < 0 || index >= 16) {
            throw new IndexOutOfBoundsException();
        }
        
        String entryName = "" + ChatColor.values()[index] + ChatColor.RESET;
    
        WrapperPlayServerScoreboardScore scoreWrapper = new WrapperPlayServerScoreboardScore();
        scoreWrapper.setPacketMode(EnumWrappers.ScoreboardAction.REMOVE);
        scoreWrapper.setItemName(entryName);
        scoreWrapper.setScoreName(OBJECTIVE_NAME);
    
        sendToAll(players, scoreWrapper);
    }
    
    private void updateEntry(Collection<UUID> players, int index, String value) {
        if (index < 0 || index >= currentLines.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        Objects.requireNonNull(value);
    
        String teamName = TEAM_PREFIX + index;
    
        WrapperPlayServerScoreboardTeam teamWrapper = new WrapperPlayServerScoreboardTeam();
        teamWrapper.setPacketMode((byte)2);
        teamWrapper.setTeamName(teamName);
        teamWrapper.setTeamDisplayName("");
        updatePrefixSuffix(value, teamWrapper);
        teamWrapper.setNameTagVisibility("always");
        teamWrapper.setOptions(0);
        teamWrapper.setColor(0);
        
        sendToAll(players, teamWrapper);
    }
    
    private void updatePrefixSuffix(String value, WrapperPlayServerScoreboardTeam teamWrapper) {
        String prefix;
        String suffix;
    
        if (value.length() <= 16) {
            prefix = value;
            suffix = "";
        } else {
            int splitIndex = value.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
            prefix = value.substring(0, splitIndex);
            String lastColor = ChatColor.getLastColors(prefix);
            suffix = lastColor + value.substring(splitIndex);
            suffix = suffix.substring(0, Math.min(suffix.length(), 16));
        }
        
        teamWrapper.setTeamPrefix(prefix);
        teamWrapper.setTeamSuffix(suffix);
    }

    private void sendToAll(Collection<UUID> players, AbstractPacket container) {
        boolean shouldRemove = players == shownPlayers;
        List<UUID> toRemove = null;
        
        for (UUID player : players) {
            if (Bukkit.getPlayer(player) != null) {
                container.sendPacket(Bukkit.getPlayer(player));
            } else if (shouldRemove) {
                if (toRemove == null) {
                    toRemove = new ArrayList<>();
                }
                toRemove.add(player);
            }
        }

        if (toRemove != null) {
            for (UUID uuid : toRemove) {
                players.remove(uuid);
            }
        }
    }

    @Override
    public void clear(Player player) {
        Objects.requireNonNull(player);
        
        shownPlayers.remove(player.getUniqueId());
        
        if (!player.isOnline()) {
            return;
        }

        for (int i = 0; i < currentLines.size(); i++) {
            removeEntry(Collections.singletonList(player.getUniqueId()), i);
        }

        WrapperPlayServerScoreboardObjective objectiveWrapper = new WrapperPlayServerScoreboardObjective();
        objectiveWrapper.setPacketMode((byte)WrapperPlayServerScoreboardObjective.Modes.REMOVE_OBJECTIVE);
        objectiveWrapper.setObjectiveName(OBJECTIVE_NAME);
        objectiveWrapper.sendPacket(player);

        for (int i = 0; i < 15; i++) {
            String teamName = TEAM_PREFIX + i;
            WrapperPlayServerScoreboardTeam teamWrapper = new WrapperPlayServerScoreboardTeam();
            teamWrapper.setTeamName(teamName);
            teamWrapper.setPacketMode((byte) WrapperPlayServerScoreboardTeam.Modes.TEAM_REMOVED);
            teamWrapper.sendPacket(player);
        }
    }
}
