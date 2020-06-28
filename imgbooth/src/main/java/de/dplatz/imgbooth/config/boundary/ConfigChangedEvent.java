package de.dplatz.imgbooth.config.boundary;

public class ConfigChangedEvent {
    private String type = ConfigChangedEvent.class.getSimpleName();
    private String key;
    private String value;
    
    public ConfigChangedEvent(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getConfigKey() {
        return key;
    }
    
    public String getConfigValue() {
        return value;
    }
    
    public String getType() {
        return type;
    }
}
