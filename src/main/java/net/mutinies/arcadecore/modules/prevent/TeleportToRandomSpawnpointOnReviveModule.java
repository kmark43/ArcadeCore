package net.mutinies.arcadecore.modules.prevent;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.util.List;

public class TeleportToRandomSpawnpointOnReviveModule implements Module {
    @EventHandler
    public void onPlayerRevive(GameRespawnEvent e) {
        Game game = ArcadeCorePlugin.getGame();
        GameTeam team = game.getTeamManager().getTeam(e.getPlayer());
        List<Location> spawnpoints = game.getMapManager().getCurrentMap().getSpawnpoints(team);
        e.getPlayer().teleport(spawnpoints.get((int)(Math.random() * spawnpoints.size())));
    }
}
