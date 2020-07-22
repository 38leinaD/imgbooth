package de.dplatz.imgbooth.browsershell;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("exit")
public class BrowserControlResource {
	
	Logger logger = Logger.getLogger(BrowserControlResource.class.getName());

	@ConfigProperty(name = "imgbooth.exitOnBrowserClose", defaultValue = "true")
	boolean exitBrowserClose;
	
	@POST
	@Consumes("text/plain")
	public void exit() {
		if (exitBrowserClose) {
			new Thread(() -> {
            	logger.info("Browser window closed. Exiting application...");
    			System.exit(0);
            }).start();
		}
	}
}