package net.mutinies.arcadecore.games.paintball;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class PaintBlocksEvent extends Event {
    private static HandlerList handlerList = new HandlerList();
    
    private Set<Block> blocks;
    
    public PaintBlocksEvent(Set<Block> blocks) {
        this.blocks = blocks;
    }
    
    public Set<Block> getBlocks() {
        return blocks;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
