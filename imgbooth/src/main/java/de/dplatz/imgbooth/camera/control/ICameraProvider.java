package de.dplatz.imgbooth.camera.control;

import java.util.List;

public interface ICameraProvider {
    public List<ICamera> getCameras();
    public String getName();
}
