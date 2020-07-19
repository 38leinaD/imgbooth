package de.dplatz.imgbooth.lab.control;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.google.common.io.Files;

import de.dplatz.imgbooth.lab.entity.PhotoUploadedEvent;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class PhotoshootProcessor {

    Logger logger = Logger.getLogger(PhotoshootProcessor.class.getName());

    @Inject
    PhotoboothConfig config;

    private Map<String, PhotoshootWork> sequence2work = new HashMap<>();

    private ExecutorService threadPool;

    private LinkedBlockingQueue<PhotoshootWork> workQueue = new LinkedBlockingQueue<PhotoshootWork>();

    @PostConstruct
    public void init() {
        this.threadPool = Executors.newFixedThreadPool(4);

    }

    void onStart(@Observes StartupEvent event) {
    }

    void onShutdown(@Observes ShutdownEvent event) {
        logger.entering(this.getClass().getSimpleName(), "onShutdown");
        threadPool.shutdownNow();
        logger.exiting(this.getClass().getSimpleName(), "onShutdown");
    }

    public void onPhotoUploaded(@Observes PhotoUploadedEvent photoUploaded) {
        var photoshoot = photoUploaded.getPhotoshoot();

        PhotoshootWork work = sequence2work.computeIfAbsent(photoUploaded.getPhotoshoot().getId(),
                (key) -> new PhotoshootWork(photoUploaded.getPhotoshoot().getId(), photoshoot.getSize()));

        if (photoshoot.getSize() == 1) {
            var outputFile = new File("/tmp/" + work.getId() + ".jpeg");

            try {
                Files.copy(photoUploaded.getStoredFile(), outputFile);
            } catch (IOException e) {
                work.getCompletion().completeExceptionally(e);
            }
            work.getCompletion().complete(null);
            return;
        }

        // TODO THIS needs to happen async
        BufferedImage image = photoUploaded.getImage();
        if (image == null) {
            try {
                image = ImageIO.read(photoUploaded.getStoredFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final BufferedImage _image = image;

        this.threadPool.submit(() -> {
            try {
                this.process(work, _image, photoUploaded.getPhotoshoot().getIndex());
            } catch (Exception e) {
                logger.log(java.util.logging.Level.SEVERE, "Error while processing image", e);
            }
        });

        if (photoshoot.getIndex() == photoshoot.getSize() - 1) {
            this.threadPool.submit(() -> {
                try {
                    this.writeImage(work);
                    work.getCompletion().complete(null);
                } catch (IOException e) {
                    work.getCompletion().completeExceptionally(e);
                }
            });
        }
    }

    public Optional<CompletableFuture<Void>> awaitCompletion(String sequenceId) {
        PhotoshootWork collageWork = sequence2work.get(sequenceId);
        if (collageWork == null) {
            return Optional.empty();
        } else {
            return Optional.of(collageWork.getCompletion());
        }
    }

    private void writeImage(PhotoshootWork work) throws IOException {
        logger.info("Waiting to write output image of photoshoot " + work.getId() + " ...");
        try {
            work.getLatch().await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        BufferedImage outputImageBuffer = work.getOutputImage();
        logger.info("Writing output image of photoshoot " + work.getId() + " ...");

        var outputFile = new File("/tmp/" + work.getId() + ".jpeg");
        if (ImageIO.write(outputImageBuffer, "JPEG", outputFile)) {
            logger.info("Wrote output image of photoshoot " + work.getId() + ": " + outputFile);
        } else {
            logger.warning("Unable to write output image for photoshoot " + work.getId() + ": " + outputFile);
        }
    }

    public LinkedBlockingQueue<PhotoshootWork> getWorkQueue() {
        return workQueue;
    }

    private void process(PhotoshootWork work, BufferedImage inputImage, int index) {
        logger.info("Generating tile " + index + " of collage...");
        BufferedImage referenceImage = inputImage;
        int referenceWidth = referenceImage.getWidth();
        int referenceHeight = referenceImage.getHeight();

        int targetWidth = referenceWidth;
        int targetHeight = referenceHeight;
        boolean cropToTarget = config.targetAspectRatio == Float.NaN;
        if (cropToTarget) {
            logger.info(
                    String.format("Cropping of images to targetAspectRatio %s will be performed.", config.targetAspectRatio));

            if (referenceWidth * 1.0f / referenceHeight < config.targetAspectRatio) {
                targetWidth = referenceWidth;
                targetHeight = (int) (referenceWidth / config.targetAspectRatio);
            } else {
                targetWidth = (int) (referenceHeight * config.targetAspectRatio);
                targetHeight = referenceHeight;
            }
        }

        var outputImageWidth = work.getNumberOfTiles() == 4 ? targetWidth * 2 : targetWidth;
        var outputImageHeight = work.getNumberOfTiles() == 4 ? targetHeight * 2 : targetHeight;

        if (work.getOutputImage() == null) {
            work.setOutputImage(new BufferedImage(outputImageWidth, outputImageHeight,
                    referenceImage.getType()));
        }

        Graphics2D g2d = work.getOutputImage().createGraphics();

        BufferedImage croppedImage = null;
        if (index == 0) {
            if (cropToTarget) {
                if (targetWidth == referenceWidth) {
                    croppedImage = cropToHeight(inputImage, targetHeight);
                } else {
                    croppedImage = cropToWidth(inputImage, targetWidth);
                }
            } else {
                croppedImage = inputImage;
            }

            g2d.drawImage(croppedImage, 0, 0, null);
        } else if (index == 1) {
            if (cropToTarget) {
                if (targetWidth == referenceWidth) {
                    croppedImage = cropToHeight(inputImage, targetHeight);
                } else {
                    croppedImage = cropToWidth(inputImage, targetWidth);
                }
            } else {
                croppedImage = inputImage;
            }
            g2d.drawImage(croppedImage, targetWidth, 0, null);
        } else if (index == 2) {
            if (cropToTarget) {
                if (targetWidth == referenceWidth) {
                    croppedImage = cropToHeight(inputImage, targetHeight);
                } else {
                    croppedImage = cropToWidth(inputImage, targetWidth);
                }
            } else {
                croppedImage = inputImage;
            }
            g2d.drawImage(croppedImage, 0, targetHeight, null);
        } else if (index == 3) {
            if (cropToTarget) {
                if (targetWidth == referenceWidth) {
                    croppedImage = cropToHeight(inputImage, targetHeight);
                } else {
                    croppedImage = cropToWidth(inputImage, targetWidth);
                }
            } else {
                croppedImage = inputImage;
            }
            g2d.drawImage(croppedImage, targetWidth, targetHeight, null);
        } else {
            throw new AssertionError("Unexpected index/tile " + index);
        }
        g2d.dispose();
        logger.info("Generated tile " + index + " of photoshoot-collage.");

        work.getLatch().countDown();
    }

    private BufferedImage cropToWidth(BufferedImage input, int targetWidth) {
        int referenceWidth = input.getWidth();
        int referenceHeight = input.getHeight();

        assert referenceWidth > referenceHeight;

        int targetHeight = referenceHeight;

        int croppedSide = (referenceWidth - targetWidth) / 2;

        return input.getSubimage(croppedSide, 0, targetWidth, targetHeight);
    }

    private BufferedImage cropToHeight(BufferedImage input, int targetHeight) {
        int referenceWidth = input.getWidth();
        int referenceHeight = input.getHeight();

        assert referenceWidth > referenceHeight;

        int targetWidth = referenceWidth;

        int croppedTop = (referenceHeight - targetHeight) / 2;

        return input.getSubimage(0, croppedTop, targetWidth, targetHeight);
    }
}
