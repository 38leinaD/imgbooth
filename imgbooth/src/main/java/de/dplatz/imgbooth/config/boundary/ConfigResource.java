package de.dplatz.imgbooth.config.boundary;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.config.ConfigProvider;

@RequestScoped
@Path("config")
public class ConfigResource {

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
