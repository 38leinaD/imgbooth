package de.dplatz.imgbooth.camera.control;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gphoto2.Camera;
import org.gphoto2.CameraFile;
import org.gphoto2.CameraList;
import org.gphoto2.CameraUtils;

import com.github.sarxos.webcam.Webcam;

@ApplicationScoped
public class GPhoto2Camera implements ICamera {
	Logger logger = Logger.getLogger(GPhoto2Camera.class.getName());

	@Inject
	@ConfigProperty(name = "photobooth.storagePath", defaultValue = "/tmp")
	String storagePath;

	@Inject
	@ConfigProperty(name = "photobooth.camera.name")
	String cameraName;

	private Camera camera;
	private Semaphore lock = new Semaphore(1);

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
	
	@PostConstruct
	@Override
	public void open() {
		lock.acquireUninterruptibly();
		try {
			// First kill any gphoto2 processed that are started when the camera is connected or powered on
			this.killGphoto2Service();
			
			logger.info("GPhoto version: " + Camera.getLibraryVersion());
			final CameraList cameras = new CameraList();
			logger.info("Cameras: " + cameras);
			CameraUtils.closeQuietly(cameras);
			
			camera = new Camera();
			camera.initialize();
		} finally {
			lock.release();
		}
	}

	@PreDestroy
	@Override
	public void close() {
		lock.acquireUninterruptibly();
		try {
			if (camera != null) {
				CameraUtils.closeQuietly(camera);
				camera = null;
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
			final CameraFile cf2 = camera.captureImage();
			File outputFile = new File(storagePath + "/" + UUID.randomUUID().toString() + ".jpg");

			cf2.save(outputFile.getAbsolutePath());
			CameraUtils.closeQuietly(cf2);
			
			logger.info("Wrote GPhoto2Camera image to " + outputFile);
			return outputFile;
		}).whenComplete((file, throwable) -> lock.release());
	}
	
	@Override
	public CompletionStage<BufferedImage> capturePreview() {
	    /*
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
		return CompletableFuture.supplyAsync(() -> {
				
				final CameraFile cf2 = camera.capturePreview();
				File outputFile = new File(storagePath + "/preview.jpg");

				cf2.save(outputFile.getAbsolutePath());
				CameraUtils.closeQuietly(cf2);
				
				return outputFile;
		}).whenComplete((file, throwable) -> lock.release());
		*/
	    return null;
	}

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
}
