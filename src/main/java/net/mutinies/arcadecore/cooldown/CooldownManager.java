package net.mutinies.arcadecore.cooldown;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager implements Manager {
    private class CooldownTask {
        private long endTime;
        private BukkitTask task;
    
        public CooldownTask(long endTime, BukkitTask task) {
            this.endTime = endTime;
            this.task = task;
        }
    }
    
    private Map<UUID, Map<String, CooldownTask>> cooldownMap;
    
    @Override
    public void enable() {
        cooldownMap = new HashMap<>();
    }
    
    @Override
    public void disable() {
        cooldownMap = null;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        clearAllCooldowns(e.getPlayer());
    }
    
    public boolean checkAvailableOrStartCooldown(Player player, String key, long cooldown) {
        if (!cooldownMap.containsKey(player.getUniqueId())) {
            cooldownMap.put(player.getUniqueId(), new HashMap<>());
        }
        Map<String, CooldownTask> taskMap = cooldownMap.get(player.getUniqueId());
        if (taskMap.containsKey(key)) {
            return false;
        } else {
            long endTime = System.currentTimeMillis() + cooldown;
            BukkitTask task = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> taskMap.remove(key), cooldown * 20 / 1000);
            // todo add event when cooled down
            taskMap.put(key, new CooldownTask(endTime, task));
            return true;
        }
    }
    
    public boolean isAvailable(Player player, String key) {
        if (!cooldownMap.containsKey(player.getUniqueId())) {
            return true;
        }
        Map<String, CooldownTask> taskMap = cooldownMap.get(player.getUniqueId());
        return !taskMap.containsKey(key);
    }
    
    public int getTimeLeft(Player player, String key) {
        if (!cooldownMap.containsKey(player.getUniqueId())) {
            return -1;
        }
        Map<String, CooldownTask> taskMap = cooldownMap.get(player.getUniqueId());
        
        if (taskMap.containsKey(key)) {
            long time = taskMap.get(key).endTime - System.currentTimeMillis();
            return (int)time;
        }
        return -1;
    }
    
    public void clearCooldown(Player player, String key) {
        if (!cooldownMap.containsKey(player.getUniqueId())) {
            cooldownMap.put(player.getUniqueId(), new HashMap<>());
        }
        Map<String, CooldownTask> taskMap = cooldownMap.get(player.getUniqueId());
        CooldownTask task = taskMap.remove(key);
        if (task != null) {
            task.task.cancel();
        }
    }
    
    public void clearAllCooldowns(Player player) {
        if (cooldownMap.containsKey(player.getUniqueId())) {
            Map<String, CooldownTask> taskMap = cooldownMap.remove(player.getUniqueId());
            for (CooldownTask task : taskMap.values()) {
                task.task.cancel();
            }
        }
    }
}
