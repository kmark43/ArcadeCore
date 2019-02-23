package net.mutinies.arcadecore.graphics.inventory;

import net.mutinies.arcadecore.util.AnimatedObjectUpdater;

public abstract class GuiUpdater extends AnimatedObjectUpdater<InventoryWindow> {
    public GuiUpdater(InventoryWindow targetObject) {
        this(targetObject, 10);
    }
    
    public GuiUpdater(InventoryWindow targetObject, int delay) {
        super(targetObject, delay);
        
        targetObject.addCloseHandler(closeEvent -> {
            if (isRunning()) {
                stop();
            }
        });
    }
    
    @Override
    protected void preUpdate(InventoryWindow window) {
        for (int i = 0; i < window.getInventorySize(); i++) {
            window.clear(i);
        }
    }
    
    @Override
    public abstract void onUpdate(InventoryWindow window);
}
