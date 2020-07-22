package de.dplatz.imgbooth.browsershell;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.dplatz.imgbooth.camera.control.CameraDeviceSystem;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class AppMain {
    static final Logger LOG = Logger.getLogger(AppMain.class.getName());

    @ConfigProperty(name = "imgbooth.browser.open", defaultValue = "true")
    Boolean openBrowser;

    @Inject
    CameraDeviceSystem cameraDeviceSystem;
    
    void onStart(@Observes StartupEvent ev) throws URISyntaxException {
        cameraDeviceSystem.cameraSystemReadiness().thenRun(() -> {
            URI url;
            try {
                url = new URI("http://localhost:8080/index.html");
            } catch (URISyntaxException e) {
               throw new RuntimeException(e);
            }

            if (openBrowser) {
                LOG.info("Launching Browser @ " + url);

                try {
                    Chrome chrome = new Chrome(new EnvPath());
                    if (chrome.available()) {
                        chrome.open(url);
                    } else {
                        Desktop.getDesktop().browse(url);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                LOG.info("**********************************************************************");
                LOG.info("Please open the browser @ " + url);
                LOG.info("**********************************************************************");
            }
        });
    }
}