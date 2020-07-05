package de.dplatz.imgbooth.camera.control;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gphoto2.Camera;
import org.gphoto2.CameraFile;
import org.gphoto2.CameraUtils;

import com.sun.jna.Pointer;

import io.quarkus.arc.Unremovable;

@Dependent
@Unremovable
public class GPhoto2Camera implements ICamera {
    Logger logger = Logger.getLogger(GPhoto2Camera.class.getName());

    @Inject
    @ConfigProperty(name = "photobooth.storagePath", defaultValue = "/tmp")
    String storagePath;

    private String name;
    private Pointer portInfo;
    
    private Camera nativeCamera;
    private Semaphore lock = new Semaphore(1);

    public void setNativeCamera(String name, Pointer portInfo) {
        this.name = name;
        this.portInfo = portInfo;
    }

    @Override
    public void open() {
        lock.acquireUninterruptibly();
        try {
            if (nativeCamera == null) {
                nativeCamera = new Camera();
                //nativeCamera.setPortInfo(portInfo);
            }
            nativeCamera.initialize();
        } finally {
            lock.release();
        }
    }

    @Override
    public void close() {
        lock.acquireUninterruptibly();
        try {
            if (nativeCamera != null) {
                CameraUtils.closeQuietly(nativeCamera);
            }
        } finally {
            lock.release();
        }
    }

    @Override
    public CompletionStage<File> takePhoto() {
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.supplyAsync(() -> {
            final CameraFile cf2 = nativeCamera.captureImage();
            File outputFile = new File(storagePath + "/" + UUID.randomUUID().toString() + ".jpg");

            cf2.save(outputFile.getAbsolutePath());
            CameraUtils.closeQuietly(cf2);

            logger.info("Wrote GPhoto2Camera image to " + outputFile);
            return outputFile;
        }).whenComplete((file, throwable) -> lock.release());
    }

    @Override
    public CompletionStage<BufferedImage> capturePreview() {
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            lock.release();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.supplyAsync(() -> {
            final CameraFile cf2 = nativeCamera.capturePreview();
            File outputFile = new File(storagePath + "/preview.jpg");

            cf2.save(outputFile.getAbsolutePath());
            CameraUtils.closeQuietly(cf2);

            try {
                return ImageIO.read(outputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).whenComplete((file, throwable) -> lock.release());
    }

    @Override
    public String getName() {
        return name;
    }
}
