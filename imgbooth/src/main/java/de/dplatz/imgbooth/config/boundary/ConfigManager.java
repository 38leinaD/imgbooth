package de.dplatz.imgbooth.config.boundary;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

@ApplicationScoped
public class ConfigManager {
    private Map<String, Object> config;

    //@Inject
    //Event<ConfigChangedEvent> configChangedEvent;
    
    private static final Path CONFIG_FILE_PATH = Paths.get("./config.json");
    
    @PostConstruct
    public void init() throws IOException {
        config = new ConcurrentHashMap<String, Object>();

        if (Files.exists(CONFIG_FILE_PATH)) {
            JsonbBuilder.create().fromJson(new FileReader(CONFIG_FILE_PATH.toFile()), HashMap.class);
        }
    }
    
    public void put(String key, Object value) {
        config.put(key, value);
        //configChangedEvent.fire(new ConfigChangedEvent());
        flushToDisk();
    }
    
    public Object get(String key) {
        return config.get(key);
    }    
    
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(config);
    }
    
    private void flushToDisk() {
        try {
            JsonbConfig jsonbConfig = new JsonbConfig();
            jsonbConfig.withFormatting(true);
            JsonbBuilder.create(jsonbConfig).toJson(this.config, new FileWriter(CONFIG_FILE_PATH.toFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
