package net.mutinies.arcadecore.modules.kit;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.kit.Kit;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class KitModule implements Module {
    private Set<String> kitNames;
    
    public KitModule() {
        this.kitNames = new HashSet<>();
    }
    
    public void addKit(Kit kit) {
        kitNames.add(kit.getName());
    }
    
    protected boolean inGameWithKit(Player player) {
        Game game = ArcadeCorePlugin.getInstance().getGameManager().getGame();
        return !game.getSpectateManager().isSpectator(player) &&
                kitNames.contains(game.getKitManager().getKit(player).getName());
    }
}
