package net.mutinies.arcadecore.games.oitq.event;

import net.mutinies.arcadecore.game.projectile.ListeningProjectile;
import net.mutinies.arcadecore.game.projectile.ProjectileDamageHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class InstantKillHandler implements ProjectileDamageHandler {
    @Override
    public void onProjectileDamage(ListeningProjectile projectile, EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity damaged = (LivingEntity) e.getEntity();
        e.setDamage(damaged.getMaxHealth());
    }
}
