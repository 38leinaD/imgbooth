# ImgBooth

:warning: This software is still in development!

ImgBooth is an open source software for photoboxes as seen on weddings/parties.
The software allows to set up such a photobox from a normal PC/laptop, camera (e.g. a Canon DSLR) and printer (e.g. Canon Selphy).
As the software is developed with Java, it runs on all major operating systems (Windows, Linux, Mac) and at some point should also be useable with raspberry pi.

![Basic setup](./setup.svg)
<img src="./setup.svg">

Intentions/Pitch:
- Why are you not using the brower's support for webcams and instead handle the camera in the Java backend? This solution is intended mainly for connecting DSLR cameras which do not expose a webcam interface. Instead, the solution is about shooting high-quality photos with a DSLR. The support for Webcams is mainly for development and not the main purpose of this solution.
- Why is the admin interface written in JSF? Because I wanted a solution that can be opend in a webbrowser on e.g. a phone to control the photobox. And why did i not use the same approach/technology as for the photobox frontend? Because JSF is a very productive technology for me. It is very simple to provide complex UIs with inputs, selections, validations, etc. The admin interface does not have to be pixel-perfect and as flashy as the photobox frontend; JSF hits a sweet-spot that balances productivity and nice/consistent visuals.

- Why Quarkus for the backend? 

## :rocket: Installation

TODO


## :warning: Developer Info

### Preparations

#### Frontend development with VSCode

* Run the task 'Allow automatic tasks in this folder' within VSCode and restart VSCode. This will automatically start the typescript-compiler in watch-mode.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./gradlew quarkusDev
```

### Packaging and running the application

The application is packageable using `./gradlew quarkusBuild`.
It produces the executable `app-1.0.0-SNAPSHOT-runner.jar` file in `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/app-1.0.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:
```
./gradlew quarkusBuild --uber-jar
```