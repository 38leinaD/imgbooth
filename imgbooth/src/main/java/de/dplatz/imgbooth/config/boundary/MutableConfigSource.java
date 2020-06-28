package de.dplatz.imgbooth.config.boundary;

import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class MutableConfigSource implements ConfigSource {

    public static final String NAME = "MutableConfigSource";

    @Override
    public int getOrdinal() {
        return 900;
    }

    @Override
    public Map<String, String> getProperties() {
        ConfigManager configManager = ConfigManager.get();
        return configManager.getAll();
    }

    @Override
    public String getValue(String key) {
        ConfigManager configManager = ConfigManager.get();
        return configManager.get(key);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
