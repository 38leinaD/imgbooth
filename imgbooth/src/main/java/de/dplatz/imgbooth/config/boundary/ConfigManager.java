package de.dplatz.imgbooth.config.boundary;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.smallrye.config.events.ChangeEvent;
import io.smallrye.config.events.ChangeEventNotifier;
import io.smallrye.config.events.Type;

// TODO: Make CDI bean if possible
public class ConfigManager {

    private static ConfigManager INSTANCE;

    public static ConfigManager get() {
        if (INSTANCE == null) {
            ConfigManager inst = new ConfigManager();
            inst.init();
            INSTANCE = inst;
        }
        return INSTANCE;
    }

    Logger logger = Logger.getLogger(ConfigManager.class.getName());

    private Properties properties = new Properties();

    private static final Path CONFIG_FILE_PATH = Paths.get("./config.properties");

    public void init() {
        if (Files.exists(CONFIG_FILE_PATH)) {
            try {
                properties.load(new FileReader(CONFIG_FILE_PATH.toFile()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void put(String key, String value) {
        properties.put(key, value);

        //CDI.current().select(Event.class).get().fire(new ConfigChangedEvent(key, value));
        ChangeEventNotifier.getInstance().fire(new ChangeEvent(Type.UPDATE, key, null, value, MutableConfigSource.NAME));

        flushToDisk();
    }

    public String get(String key) {
        return (String) properties.get(key);
    }

    public Map<String, String> getAll() {
        return properties.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getKey()));
    }

    private void flushToDisk() {
        try {
            properties.store(new FileWriter(CONFIG_FILE_PATH.toFile()), "");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to store config-change to file-system", e);
        }
    }
}
