package net.mutinies.arcadecore.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardDisplay {
    void show(Player player);
    void setTitle(String title);
    void update(List<String> lines);
    void clear(Player player);
    default void cleanup(){}
}
