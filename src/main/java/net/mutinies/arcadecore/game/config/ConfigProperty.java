package net.mutinies.arcadecore.game.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigProperty {
    private ConfigType type;
    private List<PropertyConstraint> constraintList;
    private String name;
    private Object defaultValue;
    private Object value;
    
    public ConfigProperty(ConfigType type, String name, Object defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }
    
    public void addConstraint(PropertyConstraint constraint) {
        if (constraintList == null) {
            constraintList = new ArrayList<>();
        }
        constraintList.add(constraint);
    }
    
    public List<PropertyConstraint> getConstraints() {
        return constraintList == null ? Collections.emptyList() : new ArrayList<>(constraintList);
    }
    
    public ConfigType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public boolean setValue(String toParse) {
        if (toParse == null) {
            return false;
        }
        try {
            Object value;
            switch (type) {
                case BOOLEAN:
                    if (toParse.equalsIgnoreCase("true") || toParse.equalsIgnoreCase("false")) {
                        value = Boolean.parseBoolean(toParse);
                    } else {
                        return false;
                    }
                    break;
                case INT:
                    value = Integer.parseInt(toParse);
                    break;
                case DOUBLE:
                    value = Double.parseDouble(toParse);
                    break;
                default:
                    value = null;
            }
            
            for (PropertyConstraint constraint : constraintList) {
                if (!constraint.isValid(value)) {
                    return false;
                }
            }
            
            setValue(value);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}
