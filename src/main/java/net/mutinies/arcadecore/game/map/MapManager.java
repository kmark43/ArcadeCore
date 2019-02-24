package net.mutinies.arcadecore.game.map;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapManager {
    private Game game;
    private Map<String, GameMap> mapNameMap;
    private List<GameMap> maps;
    private GameMap currentMap;
    
    public MapManager(Game game) {
        this.game = game;
        maps = new ArrayList<>();
        mapNameMap = new HashMap<>();
        loadMaps();
        if (maps.isEmpty()) {
            throw new RuntimeException("No maps defined");
        }
    }
    
    private void loadMaps() {
        File mapFolder = new File(ArcadeCorePlugin.getInstance().getDataFolder(), game.getName() + "/maps/");
        File[] files = mapFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    GameMap map = getMap(file);
                    maps.add(map);
                    mapNameMap.put(map.getName().toLowerCase(), map);
                } catch (Exception e) {
                    Bukkit.broadcast("mutinies.arcadecore.mapexceptionnotify", "Error loading map file " + file.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected GameMap getMap(File mapFile) throws Exception {
        return new GameMap(mapFile);
    }
    
    public List<GameMap> getMaps() {
        return maps;
    }
    
    public GameMap getMap(String name) {
        return mapNameMap.get(name);
    }
    
    public GameMap getCurrentMap() {
        return currentMap;
    }
    
    public void chooseRandomMap() {
        setCurrentMap(maps.get((int)(Math.random() * maps.size())));
    }
    
    public void clearMap() {
        if (currentMap != null) {
            currentMap.unloadWorld();
            currentMap = null;
        }
    }
    
    public void setCurrentMap(GameMap map) {
        if (map != null) {
            this.currentMap = map;
            currentMap.loadWorld();
        } else {
            this.currentMap = null;
        }
    }
}
