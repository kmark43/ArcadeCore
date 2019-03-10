package net.mutinies.arcadecore.modules;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import net.mutinies.arcadecore.event.GameDeathEvent;
import net.mutinies.arcadecore.game.team.GameTeam;
import net.mutinies.arcadecore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class KillComboModule implements Module {
    private int cooldownBetweenKills;
    private List<ComboType> comboTypes;
    private Map<UUID, Integer> killMap;
    private Map<UUID, BukkitTask> clearTasks;
    
    public KillComboModule() {
        this(-1);
    }
    
    public KillComboModule(int cooldownBetweenKills) {
        this.cooldownBetweenKills = cooldownBetweenKills;
        comboTypes = new ArrayList<>();
    }
    
    public void addComboType(String name, int numKills) {
        comboTypes.add(new ComboType(name, numKills));
    }
    
    @Override
    public void enable() {
        killMap = new HashMap<>();
        clearTasks = new HashMap<>();
        comboTypes.sort(Comparator.comparingInt(ComboType::getRequiredNumber));
    }
    
    @Override
    public void disable() {
        clearTasks.values().forEach(BukkitTask::cancel);
        killMap = null;
        clearTasks = null;
    }
    
    @EventHandler
    public void onPlayerDeath(GameDeathEvent e) {
        if (e.getKiller() instanceof Player) {
            Player killer = (Player) e.getKiller();
            if (!killMap.containsKey(killer.getUniqueId())) {
                killMap.put(killer.getUniqueId(), 0);
            }
            
            int numKills = killMap.get(killer.getUniqueId()) + 1;
            killMap.put(killer.getUniqueId(), killMap.get(killer.getUniqueId()) + 1);
            
            int index = Collections.binarySearch(comboTypes, new ComboType(null, numKills));
            if (index >= 0) {
                ComboType combo = comboTypes.get(index);
                
                GameTeam team = ArcadeCorePlugin.getGame().getTeamManager().getTeam(killer);
                ChatColor teamColor = team.getColor().getChatColor();
                Bukkit.broadcastMessage("" + teamColor + ChatColor.BOLD + killer.getName() + ChatColor.RESET + " got " + ChatColor.AQUA + ChatColor.BOLD + combo.getName() + " (" + combo.getRequiredNumber() + " Kills)!");
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.playSound(other.getLocation(), Sound.ENDERDRAGON_GROWL, 1f + (combo.getRequiredNumber() / 10f), 1f + (combo.getRequiredNumber() / 10f));
                }
                
                if (index == comboTypes.size() - 1) {
                    killMap.remove(killer.getUniqueId());
                }
            }
            
            if (cooldownBetweenKills >= 0) {
                if (clearTasks.containsKey(killer.getUniqueId())) {
                    clearTasks.remove(killer.getUniqueId()).cancel();
                }
                BukkitTask task = Bukkit.getScheduler().runTaskLater(ArcadeCorePlugin.getInstance(), () -> killMap.remove(killer.getUniqueId()), cooldownBetweenKills);
                clearTasks.put(killer.getUniqueId(), task);
            }
        }
        
        killMap.remove(e.getKilled().getUniqueId());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        killMap.remove(e.getPlayer().getUniqueId());
    }
    
    private static class ComboType implements Comparable<ComboType> {
        private String name;
        private int requiredNumber;
        
        private ComboType(String name, int requiredNumber) {
            this.name = name;
            this.requiredNumber = requiredNumber;
        }
        
        public String getName() {
            return name;
        }
        
        public int getRequiredNumber() {
            return requiredNumber;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ComboType)) return false;
            ComboType comboType = (ComboType) o;
            return requiredNumber == comboType.requiredNumber &&
                    Objects.equals(name, comboType.name);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, requiredNumber);
        }
        
        @Override
        public int compareTo(ComboType o) {
            return Integer.compare(requiredNumber, o.requiredNumber);
        }
    }
}
