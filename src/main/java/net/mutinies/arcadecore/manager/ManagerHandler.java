package net.mutinies.arcadecore.manager;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerHandler {
    private Map<Class<? extends Manager>, Manager> managerMap;
    private Map<Class<? extends JavaPlugin>, List<Class<? extends Manager>>> pluginMap;

    public ManagerHandler() {
        managerMap = new HashMap<>();
        pluginMap = new HashMap<>();
    }
    
    public void disable() {
        for (Manager manager : new ArrayList<>(managerMap.values())) {
            disableManager(manager.getClass());
        }
        managerMap = null;
        pluginMap = null;
    }
    
    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin() instanceof JavaPlugin && e.getPlugin() != ArcadeCorePlugin.getInstance()) {
            disable((JavaPlugin) e.getPlugin());
        }
    }
    
    public void registerAndEnable(JavaPlugin plugin, Manager... managers) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(managers);
        
        if (!pluginMap.containsKey(plugin.getClass())) {
            pluginMap.put(plugin.getClass(), new ArrayList<>());
        }
        
        List<Class<? extends Manager>> managerClassList = Stream.of(managers).map(Manager::getClass).collect(Collectors.toList());
        pluginMap.get(plugin.getClass()).addAll(managerClassList);
        for (int i = 0; i < managers.length; i++) {
            enableManager(managers[i]);
        }
    }
    
    public <T extends Manager> T getManager(Class<T> clazz) {
        ManagerHandler handler = ArcadeCorePlugin.getManagerHandler();
        return clazz.cast(handler.managerMap.get(clazz));
    }
    
    public void disable(JavaPlugin plugin) {
        List<Class<? extends Manager>> managerList = pluginMap.remove(plugin.getClass());
        
        if (managerList == null) {
            return;
        }
        
        for (int i = managerList.size() - 1; i >= 0; i--) {
            disableManager(managerList.get(i));
        }
    }
    
    private void enableManager(Manager manager) {
        managerMap.put(manager.getClass(), manager);
        Bukkit.getPluginManager().registerEvents(manager, ArcadeCorePlugin.getInstance());
        manager.enable();
    }
    
    private void disableManager(Class<? extends Manager> managerClass) {
        Manager manager = managerMap.remove(managerClass);
        manager.disable();
        HandlerList.unregisterAll(manager);
    }
}
