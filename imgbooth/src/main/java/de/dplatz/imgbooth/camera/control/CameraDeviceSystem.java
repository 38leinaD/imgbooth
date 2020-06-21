package de.dplatz.imgbooth.camera.control;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CameraDeviceSystem {

	Logger logger = Logger.getLogger(CameraDeviceSystem.class.getName());
	
	@Inject
	Instance<ICameraProvider> cameraProviders;
	
	@Inject
	@ConfigProperty(name = "photobooth.camera.provider", defaultValue = "WebCamera")
	String configuredCameraName;
	
    @Inject
    @ConfigProperty(name = "photobooth.camera.name")
    String cameraName;
	
	ICamera camera;
	
	List<ICamera> availableCameras = new LinkedList<ICamera>();
	
	void onStart(@Observes StartupEvent ev) {               
		logger.info("Initializing camera device-system...");
		for (ICameraProvider cameraProvider : cameraProviders) {
			if (cameraProvider.getClass().getSuperclass().getSimpleName().startsWith(configuredCameraName)) { // getClass().getSuperclass().getSimpleName() only works with Weld/Quarkus
			    availableCameras.addAll(cameraProvider.getCameras());
			    for (ICamera cam : cameraProvider.getCameras()) {
				    if (cam.getName().startsWith(cameraName)) {
				        this.activateCamera(cam);
				        break;
				    }
				}
			}
		}
		if (camera == null)	throw new RuntimeException("Unable to resolve Camera based on configured photobooth.camera = " + configuredCameraName);
		logger.info("Using camera " + camera);
	}
	
	void onStart(@Observes ShutdownEvent ev) {               
        logger.info("Shutdown of camera device-system...");
        if (camera != null) {
            camera.close();
        }
    }
	
	public ICamera getActiveCamera() {
		return camera;
	}
	
	public void activateCamera(ICamera camera) {
	    System.out.println("** ACTIVATING " + camera.getName());
	    this.camera = camera;
	    this.camera.open();
	    
	    // TODO close previous
	}
	
	public List<ICamera> getAvailableCameras() {
	    return availableCameras;
	}
}
