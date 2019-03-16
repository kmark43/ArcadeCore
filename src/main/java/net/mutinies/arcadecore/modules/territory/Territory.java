package net.mutinies.arcadecore.modules.territory;

import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Territory {
    private GameTeam owningTeam;
    private Location centerLocation;
    
    public Territory(Location centerLocation) {
        this.centerLocation = centerLocation.clone();
    }
    
    public void claim(GameTeam team) {
        this.owningTeam = team;
        Bukkit.getPluginManager().callEvent(new TerritoryClaimEvent(this, team));
    }
    
    public void unclaim() {
        GameTeam lastOwningTeam = owningTeam;
        this.owningTeam = null;
        Bukkit.getPluginManager().callEvent(new TerritoryUnclaimEvent(this, lastOwningTeam));
    }
    
    public GameTeam getOwningTeam() {
        return owningTeam;
    }
    
    public Location getCenterLocation() {
        return centerLocation.clone();
    }
}
