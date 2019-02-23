package net.mutinies.arcadecore.game.config;

import net.mutinies.arcadecore.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private Game game;
    private Map<String, ConfigProperty> propertyMap;
    
    public ConfigManager(Game game) {
        this.game = game;
        propertyMap = new HashMap<>();
    }
    
    public void registerProperty(ConfigProperty property) {
        propertyMap.put(property.getName(), property);
    }
    
    public boolean hasProperty(String key) {
        return propertyMap.containsKey(key);
    }
    
    public ConfigProperty getProperty(String key) {
        return propertyMap.get(key);
    }
    
    public List<ConfigProperty> getProperties() {
        return new ArrayList<>(propertyMap.values());
    }
}
