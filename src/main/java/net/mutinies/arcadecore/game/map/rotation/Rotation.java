package net.mutinies.arcadecore.game.map.rotation;

import net.mutinies.arcadecore.game.map.GameMap;

import java.util.List;

public class Rotation {
    private String name;
    private List<GameMap> maps;
    
    public Rotation(String name, List<GameMap> maps) {
        this.name = name;
        this.maps = maps;
    }
    
    public String getName() {
        return name;
    }
    
    public List<GameMap> getMaps() {
        return maps;
    }
}
