package de.dplatz.imgbooth.lab.control;
import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "photobooth")
public class PhotoboothConfig {
	
	public Float targetAspectRatio = Float.NaN;
	
	/*public PrinterConfig printer;
	
	public static class PrinterConfig {
        public Float aspectRatio;
    }*/
}