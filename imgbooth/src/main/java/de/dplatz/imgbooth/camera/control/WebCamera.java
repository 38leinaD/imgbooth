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

import com.github.sarxos.webcam.Webcam;

import io.quarkus.arc.Unremovable;

@Dependent
@Unremovable
public class WebCamera implements ICamera {

	Logger logger = Logger.getLogger(WebCamera.class.getName());

	@Inject
	@ConfigProperty(name = "photobooth.storagePath", defaultValue = "/tmp")
	String storagePath;

	private Webcam webcam;
	private Semaphore lock = new Semaphore(1);

	public void setNativeCamera(Webcam webcam) {
	    this.webcam = webcam;
	}
	
	@Override
	public void open() {
		lock.acquireUninterruptibly();
		try {
			webcam.open();
			webcam.getImage(); // Just to warm up... :-)
		} finally {
			lock.release();
		}
	}

	@Override
	public void close() {
		lock.acquireUninterruptibly();
		try {
			if (webcam != null && webcam.isOpen()) {
				webcam.close();
				webcam = null;
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
			try {
				BufferedImage image = webcam.getImage();
				File outputFile = new File(storagePath + "/" + UUID.randomUUID().toString() + ".jpg");
				ImageIO.write(image, "JPG", outputFile);
				logger.info("Wrote webcam image to " + outputFile);
				return outputFile;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).whenComplete((file, throwable) -> lock.release());
	}

	@Override
	public CompletionStage<BufferedImage> capturePreview() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
		return CompletableFuture.supplyAsync(() -> {
			return webcam.getImage();
		}).whenComplete((file, throwable) -> lock.release());
	}

    @Override
    public String getName() {
        return webcam.getName();
    }
}
