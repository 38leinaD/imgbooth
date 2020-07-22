package de.dplatz.imgbooth.browsershell;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.config.ConfigProvider;

public class Chrome {

    Logger logger = Logger.getLogger(Chrome.class.getName());

    boolean useDevToolsApi = false;

    private EnvPath envPath;

    private String profileDir;

    private String devToolsUrl;

    private int devToolsPort;

    private String devToolsPath;

    public Chrome(EnvPath envPath) {
        this.envPath = envPath;
    }

    public void useDevToolsApi() {
        useDevToolsApi = true;
    }

    protected boolean runExecutable(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting browser", e);
            return false;
        }
    }

    public boolean available() {
        String executable = findExecutable();
        if (executable != null) {
            return true;
        }
        return false;
    }

    public void open(URI uri) {
        new Thread(() -> {
            String executable = findExecutable();
            if (executable != null) {
                String cmdExt = "";
                if (useDevToolsApi) {
                    String tmpDir = System.getProperty("java.io.tmpdir");
                    profileDir = tmpDir + File.separator + "imgbooth-chrome-profile";
                    logger.info(String.format("Using %s to store temporary chrome profile.", profileDir));
                    // https://bugs.chromium.org/p/chromium/issues/detail?id=348426
                    cmdExt = " --kiosk --remote-debugging-port=0 --no-default-browser-check --no-first-run --user-data-dir="
                            + profileDir;
                } // use-fake-ui-for-media-stream
                else {
                    String tmpDir = System.getProperty("java.io.tmpdir");
                    profileDir = tmpDir + File.separator + "imgbooth-chrome-profile";
                    logger.info(String.format("Using %s to store temporary chrome profile.", profileDir));
                    // --chrome-frame --kiosk
                    cmdExt = ConfigProvider.getConfig().getValue("imgbooth.chromeflags", String.class);
                }

                File oldDevToolsFile = Paths.get(profileDir, "DevToolsActivePort").toFile();
                if (oldDevToolsFile.exists()) {
                    oldDevToolsFile.delete();
                }

                String cmd = executable + " --app=" + uri.toString() + " " + cmdExt;
                logger.info(String.format("Present! Running '" + cmd + "'."));

                boolean result = runExecutable(cmd);

                if (useDevToolsApi) {

                    waitForChromeProfileDirCreation();

                    try {
                        List<String> devToolsResult = Files.readAllLines(Paths.get(profileDir, "DevToolsActivePort"));
                        devToolsPort = Integer.parseInt(devToolsResult.get(0));

                        devToolsUrl = "ws://localhost:" + devToolsPort + devToolsResult.get(1);
                        devToolsPath = devToolsResult.get(1);

                        // logger.info("Devtools listing in port {}",
                        // devToolsPort + ". Connect-url: " + devToolsUrl);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

    }

    private String findExecutable() {

        for (String exe : Arrays.asList("chromium-browser", "chromium", "google-chrome")) {
            logger.fine("Checking for '" + exe + "' executable...");

            if (envPath.isExecutableOnPath(exe)) {
                logger.fine("Found '" + exe + "'!");
                return exe;
            }
        }
        return null;
    }

    private void waitForChromeProfileDirCreation() {
        File devToolsFile = Paths.get(profileDir, "DevToolsActivePort").toFile();
        int i = 0;
        while (i++ < 50) {
            if (devToolsFile.exists())
                return;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("No profile created under " + profileDir);
    }

    public void reloadPage() {
        System.out.println("--- wait");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // try {
        // String resp = RxHttpClient.create(new URL("http://localhost:" +
        // devToolsPort)).retrieve(HttpRequest.GET("/json/version")).blockingFirst();

        System.out.println("--- continue");

        // RxWebSocketClient wsClient =
        // appContext.createBean(RxWebSocketClient.class, new
        // URL("http://localhost:" + devToolsPort));
        // ChromeDevToolsControl devToolsConnector =
        // wsClient.connect(ChromeDevToolsControl.class ,
        // devToolsPath).blockingFirst();
        // devToolsConnector.handshake().thenRun(() ->
        // devToolsConnector.reload());
        // } catch (MalformedURLException e) {
        // e.printStackTrace();
        // }
    }

}