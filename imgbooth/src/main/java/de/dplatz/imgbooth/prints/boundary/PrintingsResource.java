package de.dplatz.imgbooth.prints.boundary;
import java.io.File;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import de.dplatz.imgbooth.prints.control.Configured;
import de.dplatz.imgbooth.prints.control.PrintService;
import de.dplatz.imgbooth.prints.control.PrintService.PrintResult;

@Path("prints")
public class PrintingsResource {

	Logger logger = Logger.getLogger(PrintingsResource.class.getName());

	@Inject
	@ConfigProperty(name = "photobooth.storagePath", defaultValue = "/tmp")
	String storagePath;
	
	@Inject
	@Configured
	PrintService printService;
	
	//@Inject
	ManagedExecutor executor;
	
	@POST
	public CompletionStage<Response> print(@QueryParam("image") String image) throws Exception {
		System.out.println("@@@@@ IMAGE TO PRINT :: " + image);
		executor = ManagedExecutor.builder().build(); // TODO: Why is injection not working???
		File requestedFile = new File(storagePath + "/" + image);
		if (!requestedFile.exists()) {
			return executor.completedStage(Response.status(Status.NOT_FOUND).build());
		}
		return executor.supplyAsync(() -> printService.print(requestedFile))
			.thenApply(this::handlePrintResult);
	}
	
	private Response handlePrintResult(PrintResult result) {
		return Response.status(result != PrintResult.Success ? 500 : 200).build();
	}
}
