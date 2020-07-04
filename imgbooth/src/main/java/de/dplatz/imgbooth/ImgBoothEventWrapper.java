package de.dplatz.imgbooth;

public class ImgBoothEventWrapper {
    private Object event;

    public ImgBoothEventWrapper(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return event;
    }
    
    public String getType() {
        return event.getClass().getSimpleName();
    }
}
