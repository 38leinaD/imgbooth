package de.dplatz.imgbooth.config.boundary;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("config")
public class ConfigResource {

    @Inject
    ConfigManager config;
    
    @GET
    @Produces("application/json")
    public Map<String, Object> getAll() {
        return config.getAll();
    }
    
    @PATCH
    @Consumes("application/json")
    public void patch(Map<String, Object> patches) {
        patches.forEach((key, value) -> this.config.put(key, value)); 
    }
}
