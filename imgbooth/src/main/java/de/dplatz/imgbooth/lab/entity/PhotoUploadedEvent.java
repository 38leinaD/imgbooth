package de.dplatz.imgbooth.lab.entity;
import java.awt.image.BufferedImage;
import java.io.File;


public class PhotoUploadedEvent {
	private BufferedImage image;
	private File storedFile;
	private PhotoshootMeta photoshoot;
	public PhotoUploadedEvent(BufferedImage image, File storedFile, PhotoshootMeta sequence) {
		super();
		this.image = image;
		this.storedFile = storedFile;
		this.photoshoot = sequence;
	}
	public BufferedImage getImage() {
		return image;
	}
	public File getStoredFile() {
		return storedFile;
	}
	public PhotoshootMeta getPhotoshoot() {
		return photoshoot;
	}
	
	@Override
	public String toString() {
		return "PhotoUploadedEvent [storedFile=" + storedFile + ", photoshoot=" + photoshoot + "]";
	}
}
