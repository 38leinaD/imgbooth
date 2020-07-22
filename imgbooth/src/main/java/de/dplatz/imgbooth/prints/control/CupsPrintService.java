package de.dplatz.imgbooth.prints.control;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJob;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.WhichJobsEnum;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CupsPrintService implements PrintService {
	@Inject
	@ConfigProperty(name = "imgbooth.storagePath", defaultValue = "/tmp")
	String storagePath;
	
	@Inject
	@ConfigProperty(name = "imgbooth.printer.printduration")
	Integer printDuration;
	
	Logger logger = Logger.getLogger(PrintService.class.getName());
	
	public enum State {
		Unknown,
		Idle,
		Printing,
		Empty,
		Error
	}
	
	private CupsClient cupsClient;
	private CupsPrinter cupsPrinter;
	
	private String printerId = "PRINTER1";
	
	private State state = State.Unknown;
	private int numPaper;
	private long timeout = 60 * 1000;
	public State getState() {
		return state;
	}
	
	@PostConstruct
	public void init() {
		try {
			this.cupsClient = new CupsClient();
			this.cupsPrinter = cupsClient.getDefaultPrinter();
			this.state = State.Idle;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Printer not working.", e);
			this.state = State.Error;
		}
		
		this.timeout = (printDuration + 20) * 1000;
	}
	

	@Override
	public PrintResult print(File image) {
		if (this.state != State.Idle) {
			logger.info("Ignoring print operation because printer is not ready");
			return PrintResult.Failed;
		}
		
		long startTime = System.currentTimeMillis();
		logger.info("STARTING PRINT FOR " + image);
		if (!image.exists()) {
			return PrintResult.Failed;
		}
		
		// print
		try {
		InputStream inputStream = new FileInputStream(image);
		PrintJob printJob = new PrintJob.Builder(inputStream).build();
		PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
		System.out.println("1: " + printRequestResult.getResultCode());
		System.out.println("2: " + printRequestResult.getResultDescription());

		System.out.println("3: " + printRequestResult.getResultMessage());
		System.out.println("4: " + printRequestResult.isSuccessfulResult());

		while (true) {
			JobStateEnum jobStatus = cupsPrinter.getJobStatus(printRequestResult.getJobId());
			System.out.println("STATUS: " + jobStatus);
			if (jobStatus == JobStateEnum.COMPLETED) {
				// TODO: proper error-handling
				logger.info("FINISHED PRINT FOR " + image);

				return PrintResult.Success;
			}
			else if (jobStatus == JobStateEnum.ABORTED || jobStatus == JobStateEnum.CANCELED) {
				// TODO: proper error-handling
				logger.info("FAILED PRINT FOR " + image);

				return PrintResult.Failed;
			}
			
			if (System.currentTimeMillis() - startTime > timeout) {
				logger.warning("Operation for image timed out: " + image);
				this.cleanUp();
				return PrintResult.Failed;
			}
			Thread.sleep(1000);
		}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Printing failed", e);
			return PrintResult.Failed;
		}
	}


	private void cleanUp() throws Exception {
		List<PrintJobAttributes> jobs = cupsPrinter.getJobs(WhichJobsEnum.NOT_COMPLETED, null, false);
		for (PrintJobAttributes job : jobs) {
			logger.info("Canceling job with state " + job.getJobState() + " and id " + job.getJobID());
			boolean canelled = cupsClient.cancelJob(job.getJobID());
			if (!canelled) {
				logger.warning("Failed to cancel job with id " + job.getJobID());

			}
		}
	}
}
