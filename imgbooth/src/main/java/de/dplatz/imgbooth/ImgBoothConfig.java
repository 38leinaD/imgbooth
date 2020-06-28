package de.dplatz.imgbooth;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "imgbooth") 
public interface ImgBoothConfig {
    public static final String CAMERA_NAME = "imgbooth.camera.name";
    
    public static final String LOCALE = "imgbooth.locale";

    @ConfigProperty(name = "camera.name") 
    Optional<String> cameraName();

    @ConfigProperty(name = "locale", defaultValue = "en") 
    String locale();
}
