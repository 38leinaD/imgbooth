package de.dplatz.imgbooth.browsershell;

import java.util.HashMap;
import java.util.stream.StreamSupport;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@RequestScoped
@Path("configuration")
public class ConfigResource {

    public static final String CONFIG_PREFIX = "photobooth";

    @GET
    @Produces(value = "application/json")
    public JsonObject getConfig() {
        final Config config = ConfigProvider.getConfig();

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        HashMap<String, String> configProperties = new HashMap<String, String>();

        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(name -> name.startsWith(CONFIG_PREFIX + ".")).forEach(name -> {
                    configProperties.put(name.substring(CONFIG_PREFIX.length() + 1),
                            config.getValue(name, String.class));
                });

        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(name -> name.startsWith("%dev." + CONFIG_PREFIX + ".")).forEach(name -> {
                    configProperties.put(name.substring("%dev.".length() + CONFIG_PREFIX.length() + 1),
                            config.getValue(name, String.class));
                });

        configProperties.entrySet().stream().forEach(e -> jsonObjectBuilder.add(e.getKey(), e.getValue()));
        return jsonObjectBuilder.build();
    }
}
