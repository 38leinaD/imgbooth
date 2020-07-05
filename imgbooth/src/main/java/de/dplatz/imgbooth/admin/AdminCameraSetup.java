package de.dplatz.imgbooth.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import de.dplatz.imgbooth.ImgBoothConfig;
import de.dplatz.imgbooth.camera.control.CameraDeviceSystem;
import de.dplatz.imgbooth.camera.control.ICamera;
import de.dplatz.imgbooth.config.boundary.ConfigManager;

@Named
@SessionScoped
public class AdminCameraSetup {
    
    Logger logger = Logger.getLogger(AdminCameraSetup.class.getName());
    
    @Inject
    CameraDeviceSystem cameraSystem;
    
    boolean livePreview;
    
    public Object applyClicked() {
        return null;
    }
    
    public void setSelectedCamera(String selectedCameraName) {
        ICamera selectedCamera = cameraSystem.getAvailableCameras().stream().filter(camera -> camera.getName().equals(selectedCameraName)).findFirst().get();
        cameraSystem.activateCamera(selectedCamera);
        
        ConfigManager.get().put(ImgBoothConfig.CAMERA_NAME, selectedCameraName);
    }
    
    public String getSelectedCamera() {
        if (cameraSystem.getActiveCamera() != null) {
            return cameraSystem.getActiveCamera().getName();
        }
        else {
            return null;
        }
    }

    public List<SelectItem> getCameraOptions() {
        List<SelectItem> cameras = new ArrayList<>();
        cameraSystem.getAvailableCameras().forEach(camera -> {
            cameras.add(new SelectItem(camera.getName()));
        });

        return cameras;
    }

    public boolean isLivePreview() {
        return livePreview;
    }

    public void setLivePreview(boolean livePreview) {
        this.livePreview = livePreview;
    }
}
