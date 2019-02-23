package net.mutinies.arcadecore.game.map;


import org.bukkit.Bukkit;
import org.bukkit.Location;

public class UnloadedLocation {
    private String worldName;
    private Location location;
    
    public UnloadedLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
    
    public UnloadedLocation(String worldName, Location location) {
        this.worldName = worldName;
        this.location = location;
    }
    
    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }
}
