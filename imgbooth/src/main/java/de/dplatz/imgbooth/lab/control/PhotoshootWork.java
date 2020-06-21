package de.dplatz.imgbooth.lab.control;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class PhotoshootWork {
	private String id;
	private BufferedImage outputImage;
	
	private CountDownLatch latch;
	
	private CompletableFuture<Void> completion = new CompletableFuture<Void>();
	private int numberOfTiles;
	public PhotoshootWork(String id, int numberOfTiles) {
		super();
		this.id = id;
		this.latch = new CountDownLatch(numberOfTiles);
		this.numberOfTiles = numberOfTiles;
	}

	public String getId() {
		return id;
	}

	public BufferedImage getOutputImage() {
		return outputImage;
	}

	public void setOutputImage(BufferedImage outputImage) {
		this.outputImage = outputImage;
	}

	
	
	public CountDownLatch getLatch() {
		return latch;
	}

	public CompletableFuture<Void> getCompletion() {
		return completion;
	}

	public int getNumberOfTiles() {
		return numberOfTiles;
	}
	
	
	
}
