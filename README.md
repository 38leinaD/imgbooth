![ImgBooth](https://raw.github.com/38leinaD/imgbooth/master/imgbooth-frontend/src/assets/logo.png)

# ImgBooth

## :sunrise: Inspiration
For my wife's birthday, I have been building a photobox (wooden box with camera and laptop inside) but struggled with the available software.
There is no good open-source/free software. The one I used on the event was a commercial Windows-based application. It did it's job but did not offer a lot of choices I cared for. E.g. giving the user the option if he wants to take a single shot or make a series of 4 photos to produce a collage.

## :question:	What it does
The software allows to drive a photobox by connecting a DSLR camera via the Gphoto2 library or a plain Webcam. It also allows to connect a photo-printer.
The main application runs full-screen in a browser and guides the user to take either a single shot or 4-photo-series.
Afterwards, the image (or generated collage) is presented to the user with a choice to print it on the connected printer.

![architecture](https://raw.github.com/38leinaD/imgbooth/master/photobox.svg)

Additionally, the software has a second Web-UI that allows to control the photobox. Currently, it allows to change the used camera or language settings. It is built as a Web-UI so it can be opened on a photo.

## :construction_worker: How I built it
I built the software as a combination of different technologies I always wanted to try to combine:

* Quarkus-based backend that exposes restful interfaces to control the camera or download images
* HTML4 / Lit-Html-based UI for the photobox frontend
* JSF-based admin interface (using Quarkus MyFaces extension)

## :shit: Challenges I ran into
The main challenge (and important requirement) currently is the performance of the preview-feed for the camera. The camera shows a live preview stream to the users. The GPhoto2 library allows to access the DSLR-cameras live-feed only by saving it to disk and then accessing it from there. For a stream/feed, this is a lot of IO and it would be better to stream it from the camera to memory.
I will try to experiment with using a ramdisk or if it is necessary to directly access the camera interface without GPhoto2.
The advantage of GPhoto2 is that it supports a lot of camera-types, but the delay of the feed on screen is noticeable; ~1-2 seconds.

## :fireworks: Accomplishments that I'm proud of
I am proud of what i was able to achieve in a few days. I.e. bringing together the backend with a rather nice-looking frontend; and even get JSF working for the admin-interface.

## :mortar_board: What I learned
I learned a lot about Quarkus, LitHtml and that even nowadays, performance is important. The live-feed runs fine on my modern PC; but on the old laptop in the photobox, the software shows a noticable delay for the live-feed.

## :rocket: What's next for imgbooth
This is basically only a proof-of-concept at the current stage. It needs a lot of error-handling in the front-end and proof that is can run for a whole evening without constant monitoring (the admin interface will help with it though).
I might be setting it up on a neighborhood event next weekend to see how it performs.
The admin-interface still needs a simple way to restart the whole application so i dont have to fiddle with the laptop in the photobox directly.

## :construction_worker:  Installation

You will need JDK 11 and NPM installed on your system to build the software from source.

Run:

```
$ ./gradlew quarkusBuild --uber-jar
```

It produces the `imgbooth-1.0.0-SNAPSHOT-runner.jar` file in the `imgbooth/build` directory.

The application is now runnable using `java -jar imgbooth/build/imgbooth-1.0.0-SNAPSHOT-runner.jar`.

A browser should automatically open and show the UI. In case no browser opens, you can manually open the UI by opening <http://localhost:8080/index.html> in a browser.

You can control the two on-screen buttons with the keyboard.

* left button by pressing "1"
* right button by pressing "2" 

By default, it should be using your default Webcam. To change the camera that is used, open the admin interface at <http://localhost:8080/admin/camera.xhtml>.

For support of DSLR cameras, the GPhoto2 library is required. Search on Google to see how it can be installed on your distribution. Ideally, it should be no more than

```
$ sudo apt install gphoto2
$ sudo apt install libgphoto2-6
```

To use a printer, it needs to be set up as the default CUPS printer.

I have succesfully tested this software with a Canon EOS M50 and a Canon Selphy 1300 printer.