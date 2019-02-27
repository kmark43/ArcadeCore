package net.mutinies.arcadecore.util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.Objects;

public class PlayerSnapshot {
    private Player player;
    private ItemStack[] inventoryContents;
    private ItemStack[] armorContents;
    private Location location;
    private Location compassTarget;
    private GameMode gameMode;
    private Scoreboard scoreboard;
    private boolean sleepingIgnored;
    private int expLevel;
    private float exp;
    private int foodLevel;
    private float saturation;
    private float exhaustion;
    private double health;
    private boolean flying;
    private boolean allowFlight;
    private float flySpeed;
    private float walkSpeed;
    private int remainingAir;
    private int maximumAir;
    private double lastDamage;
    private int noDamageTicks;
    private int maximumNoDamageTicks;
    private Collection<PotionEffect> potionEffectList;
    private boolean canPickupItems;
    private Entity leashHolder;
    private Entity vehicle;
    private int fireTicks;
    private float fallDistance;
    private String displayName;
    private String playerListName;
    private Location bedSpawnLocation;
    private double healthScale;
    private boolean healthScaled;
    private EntityDamageEvent lastDamageCause;

    public PlayerSnapshot(Player player) {
        this.player = Objects.requireNonNull(player);
        inventoryContents = player.getInventory().getContents();
        armorContents = player.getInventory().getArmorContents();
        location = player.getLocation();
        compassTarget = player.getCompassTarget();
        gameMode = player.getGameMode();
        scoreboard = player.getScoreboard();
        sleepingIgnored = player.isSleepingIgnored();
        expLevel = player.getLevel();
        exp = player.getExp();
        foodLevel = player.getFoodLevel();
        saturation = player.getSaturation();
        exhaustion = player.getExhaustion();
        health = player.getHealth();
        flying = player.isFlying();
        allowFlight = player.getAllowFlight();
        flySpeed = player.getFlySpeed();
        walkSpeed = player.getWalkSpeed();
        remainingAir = player.getRemainingAir();
        maximumAir = player.getMaximumAir();
        lastDamage = player.getLastDamage();
        noDamageTicks = player.getNoDamageTicks();
        maximumNoDamageTicks = player.getMaximumNoDamageTicks();
        potionEffectList = player.getActivePotionEffects();
        canPickupItems = player.getCanPickupItems();
//        leashHolder = player.getLeashHolder();
        vehicle = player.getVehicle();
        fireTicks = player.getFireTicks();
        fallDistance = player.getFallDistance();
        displayName = player.getDisplayName();
        playerListName = player.getPlayerListName();
        bedSpawnLocation = player.getBedSpawnLocation();
        healthScale = player.getHealthScale();
        healthScaled = player.isHealthScaled();
        lastDamageCause = player.getLastDamageCause();
    }
    
    public void restore() {
        // teleport
        player.teleport(location);
        player.setGameMode(gameMode);
        player.setAllowFlight(allowFlight);
        player.setFlying(flying);
        player.setFlySpeed(flySpeed);
        player.setWalkSpeed(walkSpeed);
        player.setFireTicks(fireTicks);
        player.setFallDistance(fallDistance);
        player.setHealthScaled(healthScaled);
        player.setHealthScale(healthScale);
        player.setHealth(health);
        player.setMaximumAir(maximumAir);
        player.setRemainingAir(remainingAir);
        player.setCanPickupItems(canPickupItems);
//        player.setLeashHolder(leashHolder);
        player.setBedSpawnLocation(bedSpawnLocation);
        player.setCompassTarget(compassTarget);
        player.setSleepingIgnored(sleepingIgnored);
        player.setSaturation(saturation);
        player.setExhaustion(exhaustion);
        player.setFoodLevel(foodLevel);
        player.setLevel(expLevel);
        player.setExp(exp);
        player.setLastDamage(lastDamage);
        player.setLastDamageCause(lastDamageCause);
        player.setMaximumNoDamageTicks(maximumNoDamageTicks);
        player.setNoDamageTicks(noDamageTicks);
        player.setDisplayName(displayName);
        player.setPlayerListName(playerListName);
        player.setScoreboard(scoreboard);
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffects(potionEffectList);
        if (vehicle != null) {
            vehicle.setPassenger(player);
        }
    }
}
