package de.dplatz.imgbooth.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.inject.Model;
import javax.faces.model.SelectItem;

import org.eclipse.microprofile.config.ConfigProvider;

import de.dplatz.imgbooth.ImgBoothConfig;
import de.dplatz.imgbooth.config.boundary.ConfigManager;

@Model
public class AdminAppearanceSetup {
    
    Logger logger = Logger.getLogger(AdminAppearanceSetup.class.getName());
    
    public Object applyClicked() {
        return null;
    }
    
    public void setSelectedLocale(String selectedlocale) {
        ConfigManager.get().put(ImgBoothConfig.LOCALE, selectedlocale);
    }
    
    public String getSelectedLocale() {
        return ConfigProvider.getConfig().getOptionalValue(ImgBoothConfig.LOCALE, String.class).orElse("en");
    }

    public List<SelectItem> getLocaleOptions() {
        List<SelectItem> locales = new ArrayList<>();
        List.of("en", "de").forEach(locale -> {
            locales.add(new SelectItem(locale));
        });

        return locales;
    }

}