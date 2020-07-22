package de.dplatz.imgbooth.prints.control;
import java.io.File;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MockPrintService implements PrintService {
	Logger logger = Logger.getLogger(MockPrintService.class.getName());

	@Inject
	@ConfigProperty(name = "imgbooth.printer.printduration")
	Integer printDuration;
	
	@Override
	public PrintResult print(File image) {
		logger.info("MOCKED PRINT FOR " + image);
		
		try {
			System.out.println("Sleeping for " + printDuration + " seconds.");
			Thread.sleep(printDuration * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PrintResult.Success;
	}
}
