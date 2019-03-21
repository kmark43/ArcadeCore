package net.mutinies.arcadecore.util;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class JsonUtil {
    public static Location parseLocation(JsonObject locationObject) {
        String worldName = locationObject.get("worldName").getAsString();
        return parseLocation(locationObject, Bukkit.getWorld(worldName));
    }
    
    public static Location parseLocation(JsonObject locationObject, World world) {
        double x = locationObject.get("x").getAsDouble();
        double y = locationObject.get("y").getAsDouble();
        double z = locationObject.get("z").getAsDouble();
        float yaw = 0;
        float pitch = 0;
        
        if (locationObject.has("yaw")) {
            yaw = locationObject.get("yaw").getAsFloat();
        }
        
        if (locationObject.has("pitch")) {
            pitch = locationObject.get("pitch").getAsFloat();
        }
        
        return new Location(world, x, y, z, yaw, pitch);
    }
}
