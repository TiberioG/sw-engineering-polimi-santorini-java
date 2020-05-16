package it.polimi.ingsw.psp40.view.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Class used to display a cool hourlass in ASCII while user is waiting
 * "Tempus fugit"
 * @author TiberioG
 */
public class Hourglass implements Runnable{
    private Frame upper;
    private Frame lower;
    private volatile boolean cancelled;

    public Hourglass(Frame upper, Frame lower) {
        this.upper = upper;
        this.lower = lower;
    }

    @Override
    public void run() {
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        upper.clear();
        Terminal.hideCursor();
        while (!cancelled) {
            try {
                lower.center( URLReader(getClass().getResource("/ascii/waiting")), 100);
            } catch (IOException e) {
                //
            }
            for (int i = 1; i <= 39; i++) {
                if (cancelled){
                    break;
                }
                try {
                    upper.centerFixed(URLReader(getClass().getResource("/ascii/hourglass/" +i)), 26, 10);
                } catch (IOException e) {
                    //
                }
            }
            Utils.doTimeUnitSleep(500);
        }
    }

    public void cancel()
    {
        cancelled = true;
    }


    public static String URLReader(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }


}
