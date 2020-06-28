package de.dplatz.imgbooth;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.subscription.Cancellable;

public class Sandbox {

    public static void main(String[] args) throws InterruptedException {
     // Creation from an emitter
        Multi<String> multi = Multi.createFrom().emitter(emitter -> {
            int i=0;
            while (!emitter.isCancelled()) {
                String msg = "- " + i++;
                System.out.println("Emitting " + msg);
                emitter.emit(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Cancelled");
            //emitter.complete();
            
        });
        System.out.println("Subscribe a");
        Cancellable cancellable = subscriber("a", multi);
        
        System.out.println("Sleeping");
        Thread.sleep(3000);

        System.out.println("Subscribe b");
        Cancellable cancellableb = subscriber("b", multi);
        
        Thread.sleep(3000);
        cancellable.cancel();
        cancellableb.cancel();

        System.out.println("Exit");
    }

    private static Cancellable subscriber(String name, Multi<String> multi) {
        return multi.runSubscriptionOn(Infrastructure.getDefaultWorkerPool()).subscribe().with(
                item -> System.out.println(name + ": Got " + item),
                failure -> System.out.println(name + ": Got a failure: " + failure),
                () -> System.out.println(name + ": Got the completion event"));
    }
}
