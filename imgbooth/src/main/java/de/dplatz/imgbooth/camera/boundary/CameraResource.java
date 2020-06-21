package de.dplatz.imgbooth.camera.boundary;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import de.dplatz.imgbooth.camera.control.CameraDeviceSystem;
import de.dplatz.imgbooth.lab.boundary.Filenames;
import de.dplatz.imgbooth.lab.entity.PhotoUploadedEvent;
import de.dplatz.imgbooth.lab.entity.PhotoshootMeta;

@Path("camera")
public class CameraResource {

	private static final int LIVEVIEW_TARGET_FPS = 20;

	Logger logger = Logger.getLogger(CameraResource.class.getName());

	@Context
	UriInfo uriInfo;

	@Inject
	CameraDeviceSystem cameraSystem;

	@Inject
	Event<PhotoUploadedEvent> photoUploaded;

	@Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry metricRegistry;
	
	@POST
	public CompletionStage<Response> takePhoto(@HeaderParam("X-PhotoMeta") PhotoshootMeta photoshoot) {
		UriBuilder absolutePathBuilder = uriInfo.getAbsolutePathBuilder();
		logger.info("Requested to take photo for " + photoshoot);
		return cameraSystem.getActiveCamera().takePhoto()
				.thenApply(photoFile -> {
					
				    var event = new PhotoUploadedEvent(null, photoFile, photoshoot);
					photoUploaded.fire(event);

					logger.info("Photo taken for" + photoshoot);

					try {
						return Response
								.created(absolutePathBuilder.host(InetAddress.getLocalHost().getHostName() + ".local")
										.path(photoFile.getName()).build())
								.header("X-ImageId", Filenames.nameWithoutExtension(photoFile))
								.build();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

	@GET
	@Path("/live")
	public Response livePreview() throws IOException {

		StreamingOutput videoStream = new StreamingOutput() {
			@Override
			public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
				try {
					// multipart/x-mixed-replace; boundary=--boundary

					while (true) {
						long startFrame = System.currentTimeMillis();

						BufferedImage preview = cameraSystem.getActiveCamera().capturePreview().toCompletableFuture().get();

						output.write("--boundary\r\n".getBytes());
						output.write("Content-Type: image/jpeg\r\n".getBytes());
						output.write(("Content-Length: " + (preview.getWidth() * preview.getHeight() * 3) + "\r\n").getBytes());
						output.write("\r\n".getBytes());
						ImageIO.write(preview, "jpg", output);
						output.write("\r\n".getBytes());

						output.flush();
						
						long endFrame = System.currentTimeMillis();
						long frameBudget = 1000 / LIVEVIEW_TARGET_FPS;
						long usedBudget = endFrame - startFrame;
						
						metricRegistry.timer("preview-frame").update(usedBudget, TimeUnit.MILLISECONDS);
						
						if (usedBudget > frameBudget) {
							logger.finer(String.format("Dropping frames on liveview. Used %s ms when budget is %s.", usedBudget, frameBudget));
						}
						Thread.sleep(Math.max(0, frameBudget - usedBudget)); // 20 fps
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new WebApplicationException("File Not Found !!");
				}
			}
		};
		return Response.ok(videoStream, "multipart/x-mixed-replace; boundary=--boundary").build();
	}
}