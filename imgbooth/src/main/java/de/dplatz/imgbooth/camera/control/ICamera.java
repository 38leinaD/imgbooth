package de.dplatz.imgbooth.camera.control;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CompletionStage;

public interface ICamera {
	public void open();
	public void close();
	public CompletionStage<File> takePhoto();
	public CompletionStage<BufferedImage> capturePreview();
	public String getName();
}
