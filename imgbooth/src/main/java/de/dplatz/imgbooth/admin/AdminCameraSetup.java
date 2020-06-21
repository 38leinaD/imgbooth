package de.dplatz.imgbooth.admin;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import de.dplatz.imgbooth.camera.control.CameraDeviceSystem;
import de.dplatz.imgbooth.camera.control.ICamera;

@Model
public class AdminCameraSetup {
    
    @Inject
    CameraDeviceSystem cameraSystem;
    
    boolean livePreview;
    
    public Object applyClicked() {

        System.out.println("HELLO!!!");
        //output = "Hello " + input1 + " @" + System.currentTimeMillis();
        return null;
    }
    
    public void setSelectedCamera(String selectedCameraName) {
        System.out.println(selectedCameraName);
        ICamera selectedCamera = cameraSystem.getAvailableCameras().stream().filter(camera -> camera.getName().equals(selectedCameraName)).findFirst().get();
        cameraSystem.activateCamera(selectedCamera);
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
        List<SelectItem> cars = new ArrayList<>();
        cameraSystem.getAvailableCameras().forEach(camera -> {
            cars.add(new SelectItem(camera.getName()));
        });

        return cars;
    }

    public boolean isLivePreview() {
        return livePreview;
    }

    public void setLivePreview(boolean livePreview) {
        this.livePreview = livePreview;
    }
}
