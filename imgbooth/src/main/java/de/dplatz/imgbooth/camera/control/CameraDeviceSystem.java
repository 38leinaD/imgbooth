package de.dplatz.imgbooth.camera.control;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;

import de.dplatz.imgbooth.ImgBoothConfig;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CameraDeviceSystem {

	Logger logger = Logger.getLogger(CameraDeviceSystem.class.getName());
	
	@Inject
	Instance<ICameraProvider> cameraProviders;
	
	ICamera camera;
	
	List<ICamera> availableCameras = new LinkedList<ICamera>();
	
	CompletableFuture<Void> cameraSystemReady = new CompletableFuture<>();
	
	void onStart(@Observes StartupEvent ev) {               
		logger.info("Initializing camera device-system...");
		for (ICameraProvider cameraProvider : cameraProviders) {
			    availableCameras.addAll(cameraProvider.getCameras());
		}
		if (availableCameras == null)	throw new RuntimeException("Unable to find a camera on this system.");
		
		String cameraName = ConfigProvider.getConfig().getOptionalValue(ImgBoothConfig.CAMERA_NAME, String.class).orElse(null);
		ICamera selectedCamera = null;
		if (cameraName != null) {
		    for (ICamera cam : availableCameras) {
		        if (cam.getName().startsWith(cameraName)) {
		            selectedCamera = cam;
		            break;
		        }
		    }
		}
		
		if (selectedCamera == null) {
		    selectedCamera = availableCameras.get(0);
		}
        this.activateCamera(selectedCamera);
        cameraSystemReady.complete(null);
	}
	
	void onShutdown(@Observes ShutdownEvent ev) {               
        logger.info("Shutdown of camera device-system...");
        if (camera != null) {
            camera.close();
        }
    }
	
	public ICamera getActiveCamera() {
		return camera;
	}
	
	public void activateCamera(ICamera camera) {
        logger.info("Activating camera " + camera.getName());
        if (this.camera != null) {
            this.camera.close();
        }
	    this.camera = camera;
	    this.camera.open();
	}
	
	public List<ICamera> getAvailableCameras() {
	    return availableCameras;
	}
	
	public CompletableFuture<Void> cameraSystemReadiness() {
	    return cameraSystemReady;
	}
}
