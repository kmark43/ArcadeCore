package net.mutinies.arcadecore.game.stats;

import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;

import java.util.*;

public class StatsManager implements Module {
    private Map<String, StatsProperty> propertyMap;
    private Map<UUID, Map<String, Object>> valueMap;
    
    public StatsManager() {
        propertyMap = new LinkedHashMap<>();
    }
    
    @Override
    public void enable() {
        valueMap = new HashMap<>();
    }
    
    @Override
    public void disable() {
        valueMap = null;
    }
    
    public void initPlayer(Player player) {
        valueMap.put(player.getUniqueId(), new HashMap<>());
    }
    
    public void registerProperty(StatsProperty property) {
        propertyMap.put(property.getName(), property);
    }
    
    public List<StatsProperty> getProperties() {
        return new ArrayList<>(propertyMap.values());
    }
    
    public StatsProperty getProperty(String name) {
        return propertyMap.get(name);
    }
    
    public boolean hasStats(Player player) {
        return valueMap.containsKey(player.getUniqueId());
    }
    
    public void setValue(Player player, String propertyName, Object value) {
        if (!propertyMap.containsKey(propertyName)) {
            throw new IllegalArgumentException();
        }
        if (!valueMap.containsKey(player.getUniqueId())) {
            valueMap.put(player.getUniqueId(), new HashMap<>());
        }
        Map<String, Object> playerValues = valueMap.get(player.getUniqueId());
        playerValues.put(propertyName, value);
    }
    
    public Object getValue(Player player, String propertyName) {
        if (!propertyMap.containsKey(propertyName)) {
            throw new IllegalArgumentException();
        }
        if (!valueMap.containsKey(player.getUniqueId())) {
            return null;
        }
        Map<String, Object> playerValues = valueMap.get(player.getUniqueId());
        return playerValues.get(propertyName);
    }
    
    public Object getValue(Player player, String propertyName, Object def) {
        Object val = getValue(player, propertyName);
        if (val == null) {
            return def;
        } else {
            return val;
        }
    }
}
