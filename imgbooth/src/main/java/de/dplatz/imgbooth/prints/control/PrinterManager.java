package de.dplatz.imgbooth.prints.control;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PrinterManager {

	@Inject
	Instance<PrintService> printServices;
	
	@Inject
	@ConfigProperty(name = "imgbooth.printer", defaultValue = "cups")
	String configuredPrinter;
	
	@ApplicationScoped
	@Produces
	@Configured
	public PrintService printService() {
		for (PrintService ps : printServices) {
			if (ps.getClass().getSimpleName().toLowerCase().startsWith(configuredPrinter)) return ps;
		}
		throw new RuntimeException("Unable to resolve PrintService based on configured imgbooth.printer = " + configuredPrinter);
	}
}