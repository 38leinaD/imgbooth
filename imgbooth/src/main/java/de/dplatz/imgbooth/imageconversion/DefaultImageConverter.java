package de.dplatz.imgbooth.imageconversion;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DefaultImageConverter implements ImageConverter {
	public void convert(File input, File output) throws IOException {
		if (!output.getName().endsWith(".jpg")) {
			throw new RuntimeException("Unsupported filetype/extension of file " + output.getName());

		}
		
	    ImageIO.write(ImageIO.read(input), "JPEG", output);
	}
}
