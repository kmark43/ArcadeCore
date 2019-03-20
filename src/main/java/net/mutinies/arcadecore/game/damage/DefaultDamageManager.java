package net.mutinies.arcadecore.game.damage;

import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.event.GamePreDeathEvent;
import net.mutinies.arcadecore.event.GameRespawnEvent;
import net.mutinies.arcadecore.event.PlayerHealthChangeEvent;
import net.mutinies.arcadecore.game.Game;
import net.mutinies.arcadecore.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

import static net.mutinies.arcadecore.util.MessageUtil.getColoredName;

public class DefaultDamageManager implements DamageManager {
    private static final int LAST_DAMAGE_TAG_COOLDOWN = 10000;
    private Game game;
    
    private Map<UUID, LinkedList<DamageInstance>> damageTracking;
    
    private Set<UUID> deadPlayers;
    
    public DefaultDamageManager(Game game) {
        this.game = game;
    }
    
    @Override
    public void enable() {
        damageTracking = new HashMap<>();
        deadPlayers = new HashSet<>();
    }
    
    @Override
    public void disable() {
        damageTracking = null;
        deadPlayers = null;
    }
    
    @Override
    public boolean isAlive(Player player) {
        return !deadPlayers.contains(player.getUniqueId());
    }
    
    @Override
    public void respawn(Player player) {
        // todo custom respawn handlers, make one to respawn where you died with same inv, at a random spawn, etc
        double oldHealth = player.getHealth();
        if (!isAlive(player)) {
            deadPlayers.remove(player.getUniqueId());
            game.getSpectateManager().unspectatePlayer(player);
            oldHealth = 0;
            Bukkit.getPluginManager().callEvent(new GameRespawnEvent(player));
        }
        player.setHealth(player.getMaxHealth());
        damageTracking.remove(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new PlayerHealthChangeEvent(player, oldHealth, player.getHealth()));
    }
    
    @Override
    public void setHealth(Player player, double health) {
        if (!isAlive(player)) {
            deadPlayers.remove(player.getUniqueId());
            game.getSpectateManager().unspectatePlayer(player);
        }
        double oldHealth = player.getHealth();
        if (health <= 0) {
            health = 0;
            handleDeath(player);
        } else if (health >= player.getMaxHealth()) {
            health = player.getMaxHealth();
            player.setHealth(health);
        }
        Bukkit.getPluginManager().callEvent(new PlayerHealthChangeEvent(player, oldHealth, health));
    }
    
    @Override
    public List<Player> getDeadPlayers() {
        return deadPlayers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
    
        Entity damager = e.getDamager();
        Entity directDamager = e.getDamager();
        Player damagee = (Player) e.getEntity();
        double effectiveDamage = Math.min(e.getDamage(), damagee.getHealth());
        
        if (damager instanceof Player && game.getSpectateManager().isSpectator((Player) damager)) {
            e.setCancelled(true);
            return;
        }
    
        if (!damageTracking.containsKey(damagee.getUniqueId())) {
            damageTracking.put(e.getEntity().getUniqueId(), new LinkedList<>());
        }
    
        if (damager instanceof Projectile && ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof LivingEntity) {
            e.setCancelled(true);
            
            damager = ((LivingEntity) ((Projectile) damager).getShooter());
            
            if (damager instanceof Player) {
                Player pDamager = (Player) damager;
                pDamager.setLevel(pDamager.getLevel() + (int)effectiveDamage);
            }
            
            damageTracking.get(damagee.getUniqueId()).addFirst(new DamageInstance(e.getCause(), directDamager));
    
            Vector trajectory = directDamager.getVelocity().setY(0).normalize();
            playDamageEffect(damagee, trajectory, 2);
            applyDamage(damagee, e.getDamage());
    
            if (damager instanceof Player) {
                ((Player) damager).playSound(damager.getLocation(), Sound.ORB_PICKUP, 1, 3);
            }
    
//            Bukkit.getPluginManager().callEvent(new ProjectileDamageEvent(damagee, damager, (Projectile) directDamager));
            
//            damage(damagee, e.getDamage(), damager, e.getCause());
        } else if (damager instanceof TNTPrimed) {
            damager = ((TNTPrimed) damager).getSource();
        }
        
        if (!e.isCancelled() && damager instanceof Player) {
            Player pDamager = (Player) damager;
            pDamager.setLevel((int) effectiveDamage);
        }
    
        damageTracking.get(e.getEntity().getUniqueId()).addFirst(new DamageInstance(e.getCause(), directDamager));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        
        if (!(e instanceof EntityDamageByEntityEvent)) {
            if (!damageTracking.containsKey(player.getUniqueId())) {
                damageTracking.put(player.getUniqueId(), new LinkedList<>());
            }
    
            damageTracking.get(e.getEntity().getUniqueId()).addFirst(new DamageInstance(e.getCause(), null));
        }
        
        if (e.getDamage() >= player.getHealth() || e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            e.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new PlayerHealthChangeEvent(player, player.getHealth(), 0));
            handleDeath(player);
        }
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player)e.getEntity().getShooter();
            shooter.setLevel(0);
        }
    }
    
    @Override
    public void damage(Player damagee, double damage, Entity damager, EntityDamageEvent.DamageCause cause) {
        Entity directDamager = Objects.requireNonNull(damager);

        if (damager instanceof Projectile && ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof LivingEntity) {
            Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(damager, damagee, cause, damage));
        } else {
            // todo implement
        }
    }
    
    private void playDamageEffect(Entity damagee, Vector trajectory, double knockback) {
        damagee.playEffect(EntityEffect.HURT);
        knockback = Math.log10(knockback);
        knockback *= 2;
    
        trajectory.multiply(.6 * knockback);
        trajectory.setY(Math.abs(trajectory.getY()));
        double vel = .2 + trajectory.length() * .8;
    
        double yAdd = Math.abs(.2 * knockback);
        double yMax = 0.4 + (.04 * knockback);
    
        trajectory.normalize();
        trajectory.multiply(vel);
        trajectory.setY(trajectory.getY() + yAdd);
    
        if (trajectory.getY() > yMax)
            trajectory.setY(yMax);
    
        trajectory.setY(trajectory.getY() + 0.2);
        damagee.setFallDistance(0);
        damagee.setVelocity(trajectory);
    }
    
    private void applyDamage(Player player, double damage) {
        double oldHealth = player.getHealth();
        double newHealth = Math.min(20, Math.max(player.getHealth() - damage, 0));
        
        if (newHealth <= 0) {
            handleDeath(player);
        } else if (newHealth <= 20) {
            player.setHealth(newHealth);
        }
    
        Bukkit.getPluginManager().callEvent(new PlayerHealthChangeEvent(player, oldHealth, newHealth));
    }
    
    private void handleDeath(Player player) {
        Location deathLocation = player.getLocation();
        
        deadPlayers.add(player.getUniqueId());
        game.getSpectateManager().spectatePlayer(player, false);
    
        LinkedList<DamageInstance> damageInstances = damageTracking.get(player.getUniqueId());
        DamageInstance lastDamage = damageInstances == null || damageInstances.isEmpty() ? null : damageInstances.getFirst();
        
        GamePreDeathEvent preDeathEvent = new GamePreDeathEvent(player,
                getLastDamager(player),
                getDeathMessage(player),
                lastDamage == null ? null : lastDamage.getCause(),
                damageTracking.get(player.getUniqueId()));
        
        Bukkit.getPluginManager().callEvent(preDeathEvent);
        if (preDeathEvent.getDeathMessage() != null) {
            Bukkit.broadcastMessage(MessageUtil.formatMessage("Death", preDeathEvent.getDeathMessage()));
        }
        
        if (lastDamage != null && lastDamage.getCause() == EntityDamageEvent.DamageCause.VOID) {
            player.teleport(game.getMapManager().getCurrentMap().getMainSpawn().getLocation());
        }
    
        GameDeathEvent deathEvent = new GameDeathEvent(player,
                getLastDamager(player),
                getDeathMessage(player),
                deathLocation,
                lastDamage == null ? null : lastDamage.getCause(),
                damageTracking.get(player.getUniqueId()));
        
        Bukkit.getPluginManager().callEvent(deathEvent);
    }
    
    private Entity getLastDamager(Player player) {
        LinkedList<DamageInstance> damageInstances = damageTracking.get(player.getUniqueId());
        Entity damager = null;
        long time = System.currentTimeMillis();
        
        if (damageInstances != null) {
            for (DamageInstance next : damageInstances) {
                if (time - next.getTime() > LAST_DAMAGE_TAG_COOLDOWN) break;
                
                Entity causer = next.getCauser();
                if (causer instanceof Projectile && ((Projectile) causer).getShooter() instanceof Player) {
                    causer = (Player)((Projectile) causer).getShooter();
                }
                
                if (causer != null && !(causer instanceof Player)) {
                    if (damager == null) {
                        damager = causer;
                    }
                } else if (causer != null) {
                    damager = causer;
                    break;
                }
            }
        }
        return damager;
    }
    
    private String getDeathMessage(Player player) {
        LinkedList<DamageInstance> damageInstances = damageTracking.get(player.getUniqueId());
        if (damageInstances == null || damageInstances.isEmpty()) {
            return getColoredName(player) + MessageUtil.DEFAULT + " died";
        }
        DamageInstance lastDamage = damageInstances.getFirst();
        Entity lastDamager = getLastDamager(player);
    
        switch (lastDamage.getCause()) {
            case CONTACT:
                return getColoredName(player) + MessageUtil.DEFAULT + " died";
            case ENTITY_ATTACK:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was killed by " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was killed.";
                }
            case PROJECTILE:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was shot by " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was shot.";
                }
            case SUFFOCATION:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " suffocated in a wall while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " suffocated in a wall";
                }
            case FALL:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " fell to their death while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " fell to their death";
                }
            case FIRE:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " burned while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " burned";
                }
            case FIRE_TICK:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " burned while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " burned";
                }
            case MELTING:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " melted while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " melted";
                }
            case LAVA:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " burned in lava while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " burned in lava";
                }
            case DROWNING:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " drowned while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " drowned";
                }
            case BLOCK_EXPLOSION:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was blown up while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was blown up.";
                }
            case ENTITY_EXPLOSION:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was blown up by " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was blown up.";
                }
            case VOID:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was knocked in to the void by " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " fell in the void";
                }
            case LIGHTNING:
                if (lastDamager != null) {
                
                } else {
                
                }
                return getColoredName(player) + MessageUtil.DEFAULT + " was struck by lightning";
            case SUICIDE:
                return getColoredName(player) + MessageUtil.DEFAULT + " killed themselves";
            case STARVATION:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " starved to death while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " starved to death";
                }
            case POISON:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was poisoned to death while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was poisoned to death";
                }
            case MAGIC:
                return getColoredName(player) + MessageUtil.DEFAULT + " was killed by magic";
            case WITHER:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " withered away while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " withered away";
                }
            case FALLING_BLOCK:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was toppled by a falling block while fighting " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was toppled by a falling block";
                }
            case THORNS:
                if (lastDamager != null) {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was pricked to death by " + getColoredName(lastDamager);
                } else {
                    return getColoredName(player) + MessageUtil.DEFAULT + " was pricked to death.";
                }
            case CUSTOM:
                return getColoredName(player) + MessageUtil.DEFAULT + " was killed by custom";
            default:
                return null;
        }
    }
}
