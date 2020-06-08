package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Player;
import org.davidmoten.text.utils.WordWrap;

import java.io.IOException;
import java.util.List;

/**
 * Tis class is used to show the selection of players
 * @author TiberioG
 */
public class PlayerSelector {
    private final static int  SPACING = 3;
    private String title ="Select first player"; // this will be upperCASED automatically
    private int widthLeft;
    private int widthRight;

    private List<Player> allPlayers;
    private String[] names;

    private Frame f1;
    private Frame f2;

    /**
     * constructor
     * @param allPlayers a list of player to select
     * @param container {@link Frame} where you want to show the selector
     */
    public PlayerSelector(List<Player> allPlayers, Frame container){
        this.allPlayers = allPlayers;
        names = allPlayers.stream().map(Player::getName).toArray(String[]::new);
        widthLeft = Math.max(Utils.longestArray(names), title.length()) + 5;
        widthRight = widthLeft + 5;

        //this is used to create a 2frame centered layout inseide the container
        f1 = new Frame(new int[]{10, (container.getColSpan() - (widthLeft + SPACING + widthRight) ) / 2 }, container.getAbsEnd(), container.getIn(), container.getOut());
        f2 = new Frame(new int[]{10, ( (container.getColSpan() - (widthLeft + SPACING + widthRight) ) / 2 ) + widthLeft + SPACING }, container.getAbsEnd(), container.getIn(), container.getOut());
    }


    /**
     * returns the selected string
     * @return
     */
    public String selection (){
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
        }
        int selection = 0;

        Terminal.hideCursor();
        print(selection);
        showText(allPlayers.get(selection).getCurrentCard().getName(), allPlayers.get(selection).getCurrentCard().getDescription());
        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                     break;
                    }

                    //getting an ARROW KEY
                    else if (c == 27) { // first part of arrow key == ESC
                        int next1 = System.in.read();
                        int next2 = System.in.read();
                        if (next1 == 91) { //  read [
                            if (next2 == 65) {                     //UP  arrow
                                if (selection > 0 && selection <= allPlayers.size() -1) {
                                    selection--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (selection >= 0 && selection < allPlayers.size() -1) {
                                    selection++;
                                }
                            }
                        }
                        print(selection);
                        showText(allPlayers.get(selection).getCurrentCard().getName(), allPlayers.get(selection).getCurrentCard().getDescription());

                    }//end arrow management

                } //end system in available
            } catch (IOException e) {
            }
        }// end while true

        return names[selection];

    }


    private void print (int current) {
        int height = allPlayers.size();
        int innerwidth = widthLeft - 4;

        String titleString = Utils.centerString(widthLeft, title);
        StringBuilder title = new StringBuilder();

        //top line
        Terminal.moveAbsoluteCursor(f1.getInit()[0], f1.getInit()[1]);
        System.out.print("╔");
        for (int i = 0; i < (widthLeft); i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        Terminal.moveAbsoluteCursor(f1.getInit()[0] + 1, f1.getInit()[1]); // goo down one line

        //title line
        title.append("║").append(titleString.replaceAll("\n", " ").toUpperCase()).append("║");
        System.out.print(title);

        Terminal.moveAbsoluteCursor(f1.getInit()[0] + 2, f1.getInit()[1]); // goo down one line

        //close tile line
        System.out.print("╠");
        for (int i = 0; i < (widthLeft); i++) {
            System.out.print("═");
        }
        System.out.print("╣");

        Terminal.moveAbsoluteCursor(f1.getInit()[0] + 3, f1.getInit()[1]); // goo down one line

        //middle item lines
        for (int i = 0; i < height; i++) {
            String nonewline = allPlayers.get(i).getName().replaceAll("\n", " "); //remove newlines in text field
            String output = String.format(". %-" + innerwidth + "s", nonewline);

            System.out.print("║ ");

            if (i == current) {
                System.out.print("\u001b[48;5;" + 243 + "m"); //grigio
                System.out.print(i);
                System.out.print(output);
                System.out.print(Colors.reset());
            } else {
                System.out.print(i);
                System.out.print(output);
            }
            System.out.print("║");

            Terminal.moveAbsoluteCursor(f1.getInit()[0] + 4 + i, f1.getInit()[1]);
        }

        //closeline
        System.out.print("╚");
        for (int i = 0; i< (widthLeft); i++ ){
            System.out.print("═");
        }
        System.out.print("╝");
    }



    private void showText(String cardname,  String description){
        f2.clearRight(); //ued to odelete previous box
        String titleString = Utils.centerString(widthLeft, "Card " + cardname); //title
        StringBuilder title = new StringBuilder();
        String wrapped =  //content
                WordWrap.from(description)
                        .maxWidth(widthLeft)
                        .wrap();
        String[] lines = wrapped.split("\\r?\\n"); //split in lines

        //top line
        Terminal.moveAbsoluteCursor(f2.getInit()[0], f2.getInit()[1]);
        System.out.print("╔");
        for (int i = 0; i < widthRight; i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        //title line
        Terminal.moveAbsoluteCursor(f2.getInit()[0] + 1, f2.getInit()[1]); // goo down one line
        title.append("║  ").append(titleString.replaceAll("\n", " ").toUpperCase()).append("   ║");
        System.out.print(title);

        //close tile line
        Terminal.moveAbsoluteCursor(f2.getInit()[0] + 2, f2.getInit()[1]); // goo down one line
        System.out.print("╠");
        for (int i = 0; i < (widthRight); i++) {
            System.out.print("═");
        }
        System.out.print("╣");

        Terminal.moveAbsoluteCursor(f2.getInit()[0] + 3, f2.getInit()[1]); // goo down one line

        //middle item lines
        for (int i = 0; i < lines.length; i++) {
            String output = String.format("  %-"+ widthLeft + "s", lines[i]);
            System.out.print("║ ");
            System.out.print(output);
            System.out.print("  ║");
            Terminal.moveAbsoluteCursor(f2.getInit()[0] + 4 + i, f2.getInit()[1]);

        }

        //closeline
        System.out.print("╚");
        for (int i = 0; i< (widthRight ); i++ ){
            System.out.print("═");
        }
        System.out.print("╝");

    }
}


