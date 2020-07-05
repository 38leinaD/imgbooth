package de.dplatz.imgbooth.camera.control;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

import org.gphoto2.Camera;
import org.gphoto2.CameraList;
import org.gphoto2.CameraUtils;

import com.github.sarxos.webcam.Webcam;

@ApplicationScoped
public class GPhoto2CameraProvider implements ICameraProvider {
    Logger logger = Logger.getLogger(GPhoto2CameraProvider.class.getName());

    List<ICamera> cameras = new LinkedList<ICamera>();

    @PostConstruct
    public void init() {
        // First kill any gphoto2 processed that are started when the camera is connected or powered on
        this.killGphoto2Service();

        logger.info("GPhoto version: " + Camera.getLibraryVersion());
        final CameraList cameras = new CameraList();
        for (int i = 0; i < cameras.getCount(); i++) {
            String cameraName = cameras.getModel(i);
            logger.info("Available camera: " + cameraName);

            GPhoto2Camera camera = CDI.current().select(GPhoto2Camera.class).get();
            camera.setNativeCamera(cameraName, null);
            //camera.setNativeCamera(cameraName, cameras.getPortInfo(i)); // TODO: Crashes with segfault; lib needs to be updated...
            this.cameras.add(camera);
        }
        CameraUtils.closeQuietly(cameras);
    }

    private void killGphoto2Service() {
        ProcessHandle
                .allProcesses()
                .filter(p -> p.info().commandLine().map(c -> c.contains("gvfsd-gphoto2")).orElse(false))
                .findFirst()
                .ifPresent(this::killGphoto2Process);
    }

    private void killGphoto2Process(ProcessHandle ph) {
        logger.info("Killing gphoto2 process that has claimed camera: " + ph.toString());
        ph.destroy();
    }

    @Override
    public List<ICamera> getCameras() {
        return cameras;
    }

    @Override
    public String getName() {
        return Webcam.class.getSimpleName();
    }

}
