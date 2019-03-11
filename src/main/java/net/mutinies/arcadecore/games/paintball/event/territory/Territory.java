package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Territory {
    private String owningTeamName;
    private Location centerLocation;
    
    public Territory(Location centerLocation) {
        this.centerLocation = centerLocation.clone();
    }
    
    public void claim(GameTeam team) {
        this.owningTeamName = team.getName();
        colorCenter(team.getColor().getDyeColor());
    }
    
    public void unclaim() {
        this.owningTeamName = null;
        colorCenter(DyeColor.WHITE);
    }
    
    public String getOwningTeamName() {
        return owningTeamName;
    }
    
    public Location getCenterLocation() {
        return centerLocation.clone();
    }
    
    private void colorCenter(DyeColor color) {
        Block block = centerLocation.getBlock();
        block.setData(color.getData());
    }
}
