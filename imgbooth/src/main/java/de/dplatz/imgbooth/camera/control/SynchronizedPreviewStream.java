package de.dplatz.imgbooth.camera.control;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SynchronizedPreviewStream {

    Logger logger = Logger.getLogger(SynchronizedPreviewStream.class.getName());
    
    private static final int LIVEVIEW_TARGET_FPS = 20;

    AtomicLong subscriberCount = new AtomicLong();
    
    AtomicLong lastFrameTimestamp = new AtomicLong();
    BufferedImage lastFrame;
    
    @Inject
    CameraDeviceSystem cameraSystem;
    
    public void register() {
        if (subscriberCount.getAndIncrement() == 0) {
            // first -> start stream
        }
    }
    
    public void unregister() {
        if (subscriberCount.decrementAndGet() == 0) {
            // last -> stop stream
        }
    }
    
    public CompletableFuture<BufferedImage> frame() {
        BufferedImage preview = null;
        long startFrame = System.currentTimeMillis();
        long frameBudget = 1000 / LIVEVIEW_TARGET_FPS;
        
        if (lastFrameTimestamp.get() == 0 || (startFrame - lastFrameTimestamp.get()) >= frameBudget) {
            try {
                preview = cameraSystem.getActiveCamera().capturePreview().toCompletableFuture().get();
                long endFrame = System.currentTimeMillis();
                long usedBudget = endFrame - startFrame;
                if (usedBudget > frameBudget) {
                    logger.finer(String.format("Dropping frames on liveview. Used %s ms when budget is %s.", usedBudget,
                            frameBudget));
                }
                
                lastFrame = preview;
                lastFrameTimestamp.set(startFrame);
            }
            catch (Exception e1) {}
            
        }
        return CompletableFuture.supplyAsync(() -> {
            return lastFrame;
        });
    }
}
