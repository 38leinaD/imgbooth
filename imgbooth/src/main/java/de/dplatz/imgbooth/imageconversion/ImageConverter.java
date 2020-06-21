package de.dplatz.imgbooth.imageconversion;
import java.io.File;
import java.io.IOException;

public interface ImageConverter {
	public void convert(File input, File output) throws IOException;
}
