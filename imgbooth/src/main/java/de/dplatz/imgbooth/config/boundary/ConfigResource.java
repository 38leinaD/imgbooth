package de.dplatz.imgbooth.config.boundary;

import java.util.HashMap;
import java.util.stream.StreamSupport;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@RequestScoped
@Path("config")
public class ConfigResource {

    public static final String CONFIG_PREFIX = "imgbooth";
    
    @GET
    @Produces(value = "application/json")
    public JsonObject getConfig() {
        final Config config = ConfigProvider.getConfig();
        
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        
        HashMap<String, String> configProperties = new HashMap<String, String>();
        
        StreamSupport.stream(config.getPropertyNames().spliterator(), false).filter(name -> name.startsWith(CONFIG_PREFIX + ".")).forEach(name -> {
            config.getOptionalValue(name, String.class).ifPresent(value -> configProperties.putIfAbsent(name, value));
        });
        
        StreamSupport.stream(config.getPropertyNames().spliterator(), false).filter(name -> name.startsWith("%dev." + CONFIG_PREFIX + ".")).forEach(name -> {
            config.getOptionalValue(name, String.class).ifPresent(value -> configProperties.putIfAbsent(name.substring("%dev.".length() + 1), value));
        });
        
        configProperties.entrySet().stream().forEach(e -> jsonObjectBuilder.add(e.getKey(), e.getValue()));
        return jsonObjectBuilder.build();
    }
    
    @GET
    @Produces("text/plain")
    @Path("{key}")
    public String get(@PathParam("key") String key) {
        return ConfigProvider.getConfig().getValue(key, String.class);
    }
    
    @PUT
    @Consumes("text/plain")
    @Path("{key}")
    public void patch(@PathParam("key") String key, String value) {
        ConfigManager.get().put(key, value);
    }
}
