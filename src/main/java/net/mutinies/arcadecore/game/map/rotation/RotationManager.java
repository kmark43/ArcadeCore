package net.mutinies.arcadecore.game.map.rotation;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.map.GameMap;
import net.mutinies.arcadecore.game.map.MapManager;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class RotationManager {
    private Game game;
    private MapManager mapManager;
    private Map<String, Rotation> rotationMap;
    
    public RotationManager(Game game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;
        rotationMap = new LinkedHashMap<>();
    }
    
    private void loadRotations() {
        File mapFolder = new File(ArcadeCorePlugin.getInstance().getDataFolder(), game.getName() + "/rotations/");
        File[] files = mapFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().endsWith(".txt")) continue;
                try {
                    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                        String nameWithoutExtension = file.getName().substring(0, file.getName().length() - ".txt".length());
                        
                        List<GameMap> rotationMaps = in.lines()
                                .filter(line -> !line.equals(""))
                                .map(line -> mapManager.getMap(line))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        
                        rotationMap.put(nameWithoutExtension, new Rotation(nameWithoutExtension, rotationMaps));
                    }
                } catch (Exception e) {
                    Bukkit.broadcast("mutinies.arcadecore.mapexceptionnotify", "Error loading rotation file " + file.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    public List<Rotation> getRotations() {
        return new ArrayList<>(rotationMap.values());
    }
    
    public Rotation getRotation(String name) {
        return rotationMap.get(name);
    }
}
