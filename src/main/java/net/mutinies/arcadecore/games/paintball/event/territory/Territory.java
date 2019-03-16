package net.mutinies.arcadecore.games.paintball.event.territory;

import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Territory {
    private GameTeam owningTeam;
    private Location centerLocation;
    
    public Territory(Location centerLocation) {
        this.centerLocation = centerLocation.clone();
    }
    
    public void claim(GameTeam team) {
        this.owningTeam = team;
        colorCenter(team.getColor().getDyeColor());
    }
    
    public void unclaim() {
        this.owningTeam = null;
        colorCenter(DyeColor.WHITE);
    }
    
    public GameTeam getOwningTeam() {
        return owningTeam;
    }
    
    public Location getCenterLocation() {
        return centerLocation.clone();
    }
    
    private void colorCenter(DyeColor color) {
        Block block = centerLocation.getBlock();
        block.setData(color.getData());
    }
}
