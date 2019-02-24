package net.mutinies.arcadecore.util;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.MakeKitItemsUnbreakable;
import net.mutinies.arcadecore.modules.prevent.NoBuildModule;
import net.mutinies.arcadecore.modules.prevent.NoDamageModule;
import net.mutinies.arcadecore.modules.prevent.NoHungerChangeModule;
import net.mutinies.arcadecore.modules.prevent.NoNaturalChangesModule;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class ModuleUtil {
    public static void enableModules(List<Module> modules) {
        for (Module module : modules) {
            try {
                module.enable();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getPluginManager().registerEvents(module, ArcadeCorePlugin.getInstance());
        }
    }
    
    public static void disableModules(List<Module> modules) {
        for (Module module : modules) {
            HandlerList.unregisterAll(module);
            try {
                module.disable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static List<Module> getVanillaList() {
        ArrayList<Module> list = new ArrayList<>();
        list.add(new NoNaturalChangesModule());
        return list;
    }
    
    public static List<Module> getPvpList() {
        List<Module> list = getVanillaList();
        list.add(new NoBuildModule());
        list.add(new NoHungerChangeModule());
        list.add(new MakeKitItemsUnbreakable());
        return list;
    }
    
    public static List<Module> getMinigameList() {
        List<Module> list = getVanillaList();
        list.add(new NoDamageModule());
        return list;
    }
}
