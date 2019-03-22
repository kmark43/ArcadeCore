package net.mutinies.arcadecore.game.stats;

public class StatsProperty {
    private String name;
    private String displayName;
    private boolean shouldShow;
    private Object initialValue;
    
    public StatsProperty(String name, String displayName, boolean shouldShow, Object initialValue) {
        this.name = name;
        this.displayName = displayName;
        this.shouldShow = shouldShow;
        this.initialValue = initialValue;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isShouldShow() {
        return shouldShow;
    }
    
    public Object getInitialValue() {
        return initialValue;
    }
}
