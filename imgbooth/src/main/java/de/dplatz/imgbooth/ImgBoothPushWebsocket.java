package de.dplatz.imgbooth;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import io.smallrye.config.events.ChangeEvent;

@ServerEndpoint("/events")
@ApplicationScoped
public class ImgBoothPushWebsocket {
    Set<Session> sessions = new HashSet<Session>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("OPEN!!!!");
        this.sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        this.sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        this.sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message from client: " + message);
    }

    public void onConfigChangedEvent(@Observes ChangeEvent event) {
        this.send(event);
    }

    public void send(Object event) {
        Jsonb jsonb = JsonbBuilder.create();
        final String jsonString = jsonb.toJson(new ImgBoothEventWrapper(event));

        sessions.forEach(session -> session.getAsyncRemote().sendObject(jsonString, result -> {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        }));
    }
}