package net.mutinies.arcadecore.games.paintball.gun.handler;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileDamageHandler;
import net.mutinies.arcadecore.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class StaticHealHandler implements ProjectileDamageHandler {
    private double heal;
    
    public StaticHealHandler(double heal) {
        this.heal = heal;
    }
    
    @Override
    public void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent damageByEntityEvent) {
        if (damageByEntityEvent.getEntity() instanceof Player) {
            Player player = (Player) damageByEntityEvent.getEntity();
            Player shooter = (Player) projectile.getProjectile().getShooter();
            Game game = ArcadeCorePlugin.getInstance().getGameManager().getGame();
            GameTeam playerTeam = game.getTeamManager().getTeam(player);
            GameTeam shooterTeam = game.getTeamManager().getTeam(shooter);
            if (shooterTeam.equals(playerTeam)) {
                damageByEntityEvent.setCancelled(true);
                // todo heal
            }
        }
    }
}
