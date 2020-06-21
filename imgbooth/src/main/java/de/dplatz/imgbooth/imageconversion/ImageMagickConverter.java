package de.dplatz.imgbooth.imageconversion;
import java.io.File;
import java.io.IOException;

public class ImageMagickConverter implements ImageConverter {

	@Override
	public void convert(File input, File output) throws IOException {
		Process exec = Runtime.getRuntime().exec("convert " + input.getAbsolutePath() + " " + output.getAbsolutePath());
		try {
			if (exec.waitFor() != 0) {
				System.out.println("Command returned an error!");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
