package net.mutinies.arcadecore.game.map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.weather.WorldWeatherPreventer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ArcadeMap {
    private String name;
    private String displayName;
    private String worldName;
    private World.Environment environment;
    private World world;
    private UnloadedLocation mainSpawn;
    
    public ArcadeMap(File file) throws FileNotFoundException {
        parse(new JsonParser().parse(new JsonReader(new FileReader(file))).getAsJsonObject());
    }
    
    public void parse(JsonObject root) {
        name = root.get("name").getAsString();
        displayName = root.get("displayName").getAsString();
        worldName = root.get("worldName").getAsString();
        environment = World.Environment.NORMAL;
        if (root.has("environment")) {
            try {
                environment = World.Environment.valueOf(root.get("environment").getAsString());
            } catch (IllegalArgumentException ignored) {}
        }
        mainSpawn = parseLocation(root.get("mainSpawn").getAsJsonObject());
    }
    
    public void loadWorld() {
        if (world != null) return;
        
        world = Bukkit.createWorld(new WorldCreator(worldName)
                .environment(environment)
                .generator(new VoidGenerator()));
        
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doMobLoot", "false");
        world.setGameRuleValue("doTileDrops", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("keepInventory", "true");
        WorldWeatherPreventer weatherPreventer = ArcadeCorePlugin.getManagerHandler().getManager(WorldWeatherPreventer.class);
        weatherPreventer.registerWorld(worldName);
    }

    public void unloadWorld() {
        if (world == null) return;
        
        Bukkit.unloadWorld(world, false);
        world = null;
        WorldWeatherPreventer weatherPreventer = ArcadeCorePlugin.getManagerHandler().getManager(WorldWeatherPreventer.class);
        weatherPreventer.unregisterWorld(worldName);
    }
    
    protected UnloadedLocation parseLocation(JsonObject locationObject) {
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
        
        return new UnloadedLocation(worldName, x, y, z, yaw, pitch);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public World.Environment getEnvironment() {
        return environment;
    }
    
    public World getWorld() {
        return world;
    }
    
    public UnloadedLocation getMainSpawn() {
        return mainSpawn;
    }
}
