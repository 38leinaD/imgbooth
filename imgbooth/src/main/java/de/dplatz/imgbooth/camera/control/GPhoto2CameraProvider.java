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
    Logger logger = Logger.getLogger(WebCamera.class.getName());

    List<ICamera> cameras = new LinkedList<ICamera>();

    @PostConstruct
    public void init() {
        // First kill any gphoto2 processed that are started when the camera is connected or powered on
        this.killGphoto2Service();

        logger.info("GPhoto version: " + Camera.getLibraryVersion());
        final CameraList cameras = new CameraList();
        logger.info("Cameras: " + cameras);
        CameraUtils.closeQuietly(cameras);

        Camera nativeCamera = new Camera();

        GPhoto2Camera camera = CDI.current().select(GPhoto2Camera.class).get();
        camera.setNativeCamera(nativeCamera);
        this.cameras.add(camera);

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
