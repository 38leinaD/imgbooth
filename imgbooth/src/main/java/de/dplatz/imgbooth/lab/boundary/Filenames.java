package de.dplatz.imgbooth.lab.boundary;
import java.io.File;

public class Filenames {
	public static String nameWithoutExtension(File f) {
		return f.getName().substring(0, f.getName().lastIndexOf("."));
	}
}
