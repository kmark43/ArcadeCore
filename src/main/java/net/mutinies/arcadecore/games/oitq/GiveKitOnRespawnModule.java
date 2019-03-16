package net.mutinies.arcadecore.games.oitq;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.module.Module;
import net.mutinies.arcadecore.util.PlayerUtil;
import org.bukkit.event.EventHandler;

public class GiveKitOnRespawnModule implements Module {
    @EventHandler
    public void onGameRespawn(GameRespawnEvent e) {
        PlayerUtil.clearInventory(e.getPlayer());
        ArcadeCorePlugin.getGame().getKitManager().getKit(e.getPlayer()).giveItems(e.getPlayer());
    }
}
