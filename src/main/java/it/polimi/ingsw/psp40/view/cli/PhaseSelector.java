package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.Player;
import org.davidmoten.text.utils.WordWrap;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author TiberioG
 */
public class PhaseSelector {
    private final static int  SPACING = 3;
    private String title ="Select Phase";

    private List<Phase> phaseList;

    private Frame container;

    public PhaseSelector(List<Phase> phaseList, Frame container){
        this.phaseList = phaseList;
        this.container = container;
    }


    Phase selection (){
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        int selection = 0;

        Terminal.hideCursor();
        print(selection);

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
                                if (selection > 0 && selection <= phaseList.size() -1) {
                                    selection--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (selection >= 0 && selection < phaseList.size() -1) {
                                    selection++;
                                }
                            }
                        }
                        print(selection);


                    }//end arrow management

                } //end system in available
            } catch (IOException e) {
            }
        }// end while true

        return  phaseList.get(selection);

    }


    private void print (int current) {
        int height = phaseList.size();
        String[] phases = phaseList.stream().map(phase -> phase.getType().toString()).toArray(String[]::new);

        int width = Math.max(Utils.longestArray(phases), title.length()) + 5;
        int innerwidth = width - 4;



        String titleString = Utils.centerString(width, title);
        StringBuilder title = new StringBuilder();

        //top line
        Terminal.moveAbsoluteCursor(container.getInit()[0], container.getInit()[1]);
        System.out.print("╔");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        Terminal.moveAbsoluteCursor(container.getInit()[0] + 1, container.getInit()[1]); // goo down one line

        //title line
        title.append("║").append(titleString.replaceAll("\n", " ").toUpperCase()).append("║");
        System.out.print(title);

        Terminal.moveAbsoluteCursor(container.getInit()[0] + 2, container.getInit()[1]); // goo down one line

        //close tile line
        System.out.print("╠");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╣");

        Terminal.moveAbsoluteCursor(container.getInit()[0] + 3, container.getInit()[1]); // goo down one line

        //middle item lines
        for (int i = 0; i < height; i++) {
            String nonewline = phases[i];
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

            Terminal.moveAbsoluteCursor(container.getInit()[0] + 4 + i, container.getInit()[1]);
        }

        //closeline
        System.out.print("╚");
        for (int i = 0; i< (width); i++ ){
            System.out.print("═");
        }
        System.out.print("╝");
    }


}


