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

import com.github.sarxos.webcam.Webcam;

@ApplicationScoped
public class WebCameraProvider implements ICameraProvider {
    Logger logger = Logger.getLogger(WebCamera.class.getName());

    List<ICamera> cameras = new LinkedList<ICamera>();
       
    @PostConstruct
    public void init() {
        List<Webcam> webcams = Webcam.getWebcams();
        webcams.stream().forEach(wc -> logger.info("Available webcam: '" + wc.getName() + "'"));

        for (Webcam nativeCam : webcams) {
            Dimension maxDimensions = Arrays.asList(nativeCam.getViewSizes()).stream()
                    .max(Comparator.comparingInt(dim -> (int) (dim.getWidth()))).get();
            nativeCam.setViewSize(maxDimensions);
            //webcam.open();
            //webcam.getImage(); // Just to warm up... :-)
            
            WebCamera webCamera = CDI.current().select(WebCamera.class).get();
            webCamera.setNativeCamera(nativeCam);
            cameras.add(webCamera);
        }
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
