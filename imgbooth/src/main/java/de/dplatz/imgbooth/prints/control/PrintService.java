package de.dplatz.imgbooth.prints.control;
import java.io.File;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author daniel
 */
@ApplicationScoped
public interface PrintService {
	
	public enum PrintResult {
		Success,
		Failed
	}
	
	public PrintResult print(File image);
}
