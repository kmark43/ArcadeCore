package net.mutinies.arcadecore.game.map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMap extends ArcadeMap {
    private Map<String, List<UnloadedLocation>> teamSpawnpoints;
    private JsonObject rootObject;
    
    public GameMap(File file) throws FileNotFoundException {
        super(file);
    }
    
    @Override
    public void parse(JsonObject root) {
        super.parse(root);
        this.rootObject = root;
        
        teamSpawnpoints = new HashMap<>();
        for (JsonElement teamElement : root.get("teams").getAsJsonArray()) {
            JsonObject teamObject = teamElement.getAsJsonObject();
            String teamName = teamObject.get("name").getAsString();
            List<UnloadedLocation> spawnpoints = new ArrayList<>();
    
            for (JsonElement spawnpointElement : teamObject.get("spawnpoints").getAsJsonArray()) {
                spawnpoints.add(parseLocation(spawnpointElement.getAsJsonObject()));
            }
            
            teamSpawnpoints.put(teamName, spawnpoints);
        }
    }
    
    public JsonObject getRootObject() {
        return rootObject;
    }
    
    public List<String> getParsedTeams() {
        return new ArrayList<>(teamSpawnpoints.keySet());
    }
    
    public List<Location> getSpawnpoints(GameTeam team) {
        if (teamSpawnpoints.containsKey(team.getName())) {
            return teamSpawnpoints.get(team.getName()).stream().map(UnloadedLocation::getLocation).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
