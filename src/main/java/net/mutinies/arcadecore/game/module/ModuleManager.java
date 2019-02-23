package net.mutinies.arcadecore.game.module;

import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private Game game;
    private List<Module> gameModules;
    
    public ModuleManager(Game game) {
        this.game = game;
        gameModules = new ArrayList<>();
    }
    
    public void addModules(Module... modules) {
        addModules(Arrays.asList(modules));
    }
    
    public void addModules(List<Module> modules) {
        gameModules.addAll(modules);
    }
    
    public List<Module> getGameModules() {
        return Collections.unmodifiableList(gameModules);
    }
}
