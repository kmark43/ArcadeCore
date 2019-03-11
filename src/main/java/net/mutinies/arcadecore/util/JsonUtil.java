package net.mutinies.arcadecore.util;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class JsonUtil {
    public static Location parseLocation(JsonObject locationObject) {
        String worldName = locationObject.get("worldName").getAsString();
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
    
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
