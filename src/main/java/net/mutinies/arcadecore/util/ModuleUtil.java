package net.mutinies.arcadecore.util;

import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.modules.MakeKitItemsUnbreakable;
import net.mutinies.arcadecore.modules.prevent.NoBuildModule;
import net.mutinies.arcadecore.modules.prevent.NoDamageModule;
import net.mutinies.arcadecore.modules.prevent.NoHungerChangeModule;
import net.mutinies.arcadecore.modules.prevent.NoNaturalChangesModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleUtil {
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
