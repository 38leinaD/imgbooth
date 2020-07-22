package de.dplatz.imgbooth.imageconversion;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ImageConverterProducer {
	Logger logger = Logger.getLogger(ImageConverterProducer.class.getName());

	@ConfigProperty(name = "imgbooth.imageconversion", defaultValue = "java")
	String imageConversion;
	
	@Produces
	@ApplicationScoped
	public ImageConverter produce() {
		if (imageConversion.equals("java")) {
			return new DefaultImageConverter();
		}
		else if (imageConversion.equals("imagemagick")) {
			logger.info("Using imagepick for image-conversions.");
			return new ImageMagickConverter();
		}
		else {
			throw new RuntimeException("Unsupported image-converter.");
		}
	}
}
