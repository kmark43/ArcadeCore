package net.mutinies.arcadecore.util;

import net.mutinies.arcadecore.ArcadeCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public abstract class AnimatedObjectUpdater<T> {
    private BukkitTask task;
    private T targetObject;
    private int delay;
    
    public AnimatedObjectUpdater(T targetObject) {
        this(targetObject, 1);
    }

    public AnimatedObjectUpdater(T targetObject, int delay) {
        if (delay <= 0) {
            throw new IllegalArgumentException("Delay must be at least 1");
        }
        
        this.targetObject = Objects.requireNonNull(targetObject);
        this.delay = delay;
    }

    public boolean isRunning() {
        return task != null;
    }
    
    public void start() {
        if (isRunning()) {
            throw new IllegalStateException();
        }
        task = Bukkit.getScheduler().runTaskTimer(ArcadeCorePlugin.getInstance(), () -> {
            preUpdate(targetObject);
            onUpdate(targetObject);
            postUpdate(targetObject);
        }, 0, delay);
    }
    
    public void stop() {
        if (!isRunning()) {
            throw new IllegalStateException();
        }
        task.cancel();
        task = null;
    }
    
    public T getTarget() {
        return targetObject;
    }
    
    public int getDelay() {
        return delay;
    }
    
    protected void preUpdate(T object){}
    public abstract void onUpdate(T object);
    protected void postUpdate(T object){}
}
