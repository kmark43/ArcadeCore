package net.mutinies.arcadecore.weather;

import net.mutinies.arcadecore.manager.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.HashSet;
import java.util.Set;

public class WorldWeatherPreventer implements Manager {
    private Set<String> preventedWorlds;
    
    @Override
    public void enable() {
        preventedWorlds = new HashSet<>();
    }
    
    @Override
    public void disable() {
        preventedWorlds = null;
    }
    
    public void registerWorld(String worldName) {
        preventedWorlds.add(worldName);
    }
    
    public void unregisterWorld(String worldName) {
        preventedWorlds.remove(worldName);
    }
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (preventedWorlds.contains(e.getWorld().getName())) {
            e.setCancelled(true);
        }
    }
}
