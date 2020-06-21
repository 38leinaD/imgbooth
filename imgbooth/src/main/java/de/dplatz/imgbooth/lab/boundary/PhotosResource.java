package de.dplatz.imgbooth.lab.boundary;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Timed;

import de.dplatz.imgbooth.imageconversion.ImageConverter;
import de.dplatz.imgbooth.lab.control.PhotoshootProcessor;

@Path("photos")
public class PhotosResource {

	Logger logger = Logger.getLogger(PhotosResource.class.getName());
	
	@Context
	UriInfo uriInfo;
	
	@Inject
	PhotoshootProcessor collages;
	
	@Inject
	ImageConverter imageConverter;
	
	@Inject
	@ConfigProperty(name = "photobooth.storagePath", defaultValue = "/tmp")
	String storagePath;
	
	@GET
	@Path("{file}")
	public Response download(@PathParam("file") String fileName) throws IOException {
		File localFile = new File("/tmp/" + fileName);
	    return Response.ok(localFile, localFile.getName().endsWith(".jpeg") ? "image/jpeg" : "image/png").build();
	}
	
	@Timed
	@GET
	@Path("/photoshoots/{sequenceId}.{extension}")
	public Response downloadCollage(@PathParam("sequenceId") String sequenceId, @PathParam("extension") String extension) throws IOException {
		
		Optional<CompletableFuture<Void>> awaitCompletion = collages.awaitCompletion(sequenceId);
		if (awaitCompletion != null) {
			logger.info("Collage " + sequenceId + " not ready yet. Waiting...");
			awaitCompletion.get().join();
		}
		
		logger.info("Now serving... "+ sequenceId + "." + extension);
		
		File requestedFile = new File("/tmp/" + sequenceId + "." + extension);
		if (!requestedFile.exists()) {
			// test if png exist
			File baseFile = new File("/tmp/" + sequenceId + ".png");
			if (baseFile.exists() && extension.equals("jpeg")) {
				// Generate requested type
				logger.info("Generating Jpeg...");
				
				imageConverter.convert(baseFile, requestedFile);
				logger.info("Generated Jpeg. Done.");

				StreamingOutput fileStream =  new StreamingOutput() 
		        {
		            @Override
		            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
		            {
		                try
		                {
		                    byte[] data = Files.readAllBytes(requestedFile.toPath());
		                    output.write(data);
		                    output.flush();
		                } 
		                catch (Exception e) 
		                {
		                    throw new WebApplicationException("File Not Found !!");
		                }
		            }
		        };
			    
			    
			    return Response.ok(fileStream, "image/jpeg").build();
			}
			else {
				return Response.status(404).build();
			}
		}
		
		if (extension.equals("png")) {
		    System.out.println("*****************");
		    StreamingOutput fileStream =  new StreamingOutput() 
	        {
	            @Override
	            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
	            {
	                try
	                {
	                    byte[] data = Files.readAllBytes(requestedFile.toPath());
	                    output.write(data);
	                    output.flush();
	                } 
	                catch (Exception e) 
	                {
	                    throw new WebApplicationException("File Not Found !!");
	                }
	            }
	        };
		    
		    
		    return Response.ok(fileStream, "image/jpeg").build();
		}
		else if (extension.equals("jpeg")) {
		    return Response.ok(requestedFile, "image/jpeg").build();
		}
		else {
			return Response.status(404).build();
		}
	}
}
