package it.polimi.ingsw.psp40.view.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Class used to display a cool hourglass in ASCII while user is waiting
 * "Tempus fugit"
 *
 * @author TiberioG
 */
public class Hourglass implements Runnable {
    private Frame upper;
    private Frame lower;
    private volatile boolean cancelled;
    private boolean lateral = false;

    /**
     * constructor
     *
     * @param upper   frame where the hourglass will be printed
     * @param lower   frame where to add additional messages
     * @param lateral specifies if it's a lateral hourglass
     */
    public Hourglass(Frame upper, Frame lower, boolean lateral) {
        this.upper = upper;
        this.lower = lower;
        this.lateral = lateral;
    }

    @Override
    public void run() {
        // this is for the hourglass in the center
        if (!lateral) {
            try {
                Terminal.noBuffer();
            } catch (IOException | InterruptedException e) {
                // e.printStackTrace();
            }
            upper.clear();
            Terminal.hideCursor();
            while (!cancelled) {
                try {
                    lower.center(URLReader(getClass().getResource("/ascii/waiting")), 100);
                } catch (IOException e) {
                    //
                }
                for (int i = 1; i <= 39; i++) { // 39 is the number of hourglass frames in ascii
                    if (cancelled) {
                        break;
                    }
                    try {
                        upper.centerFixed(URLReader(getClass().getResource("/ascii/hourglass/" + i)), 26, 10);
                    } catch (IOException e) {
                        //
                    }
                }
                Utils.doTimeUnitSleep(500);
            }
        }
        //this is for a lateral hourglass
        else {
            Terminal.hideCursor();
            while (!cancelled) {
                for (int i = 1; i <= 39; i++) { // 39 is the number of hourglass frames in ascii
                    if (cancelled) {
                        break;
                    }
                    try {
                        upper.centerCenterFixed(URLReader(getClass().getResource("/ascii/hourglass/" + i)), "Your turn is over", 26, 23, 10);
                    } catch (IOException e) {
                        //
                    }
                }
                Utils.doTimeUnitSleep(500);
            }
        }

    }

    /**
     * stops the animation
     */
    public void cancel() {
        cancelled = true;
    }

    /**
     * small utility to convert paths to string
     *
     * @param url where there is a resource
     * @return the path string
     * @throws IOException
     */
    public static String URLReader(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }


}
