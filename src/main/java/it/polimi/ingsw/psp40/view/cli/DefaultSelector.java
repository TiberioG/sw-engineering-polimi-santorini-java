package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import java.io.IOException;
import java.util.List;

public class DefaultSelector {
    private String title;

    private List<String> listForSelector;

    private int width;

    private int[] init;


    public DefaultSelector(Frame mainContainer, String title, List<String> listForSelector, boolean centered) {
        this.title = title;
        this.listForSelector = listForSelector;

        this.width = Math.max(listForSelector.stream().mapToInt(String::length).max().orElse(title.length()), title.length()) + 5;

        if (!centered){
            init = mainContainer.getInit();
        }

        else {
            int col = (mainContainer.getColSpan() - width ) / 2;
            init = new int[] {mainContainer.getInit()[0], col};

        }

    }


    private void callTerminalNoBuffer() {
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getSelectionIndex() {
        callTerminalNoBuffer();
        int selection = 0;
        Terminal.hideCursor();
        printListOfSelection(selection);

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
                                if (selection > 0 && selection <= listForSelector.size() -1) {
                                    selection--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (selection >= 0 && selection < listForSelector.size() -1) {
                                    selection++;
                                }
                            }
                        }
                        printListOfSelection(selection);
                        //printFunction.executeFunction();

                    }//end arrow management
                } //end system in available
            } catch (IOException e) {
                //todo frame per except
            }
        }// end while true
        return selection;
    }

    public String getSelection() {
        int selection = getSelectionIndex();
        return listForSelector.get(selection);
    }

    public void printListOfSelection(int currentSelection) {
        printTopLine();
        printOpenTitleLine();
        printCloseTitleLine();
        printMiddleItemLines(currentSelection);
        printCloseLine();
    }


    public void printTopLine() {
        //top line
        Terminal.moveAbsoluteCursor(init[0], init[1]);
        System.out.print("╔");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        Terminal.moveAbsoluteCursor(init[0] + 1, init[1]); // goo down one line
    }

    public void printOpenTitleLine() {
        String titleString = Utils.centerString(width, title);
        StringBuilder title = new StringBuilder();
        title.append("║").append(titleString.replaceAll("\n", " ").toUpperCase()).append("║");
        System.out.print(title);

        Terminal.moveAbsoluteCursor(init[0] + 2, init[1]); // goo down one line
    }

    public void printCloseTitleLine() {
        //close tile line
        System.out.print("╠");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╣");
        Terminal.moveAbsoluteCursor(init[0] + 3, init[1]); // goo down one line
    }

    public void printMiddleItemLines(int currentSelection) {
        int innerwidth = width - 4;
        for (int i = 0; i < listForSelector.size(); i++) {
            String output = String.format(". %-" + innerwidth + "s", listForSelector.get(i).replaceAll("\n", ""));
            System.out.print("║ ");

            if (i == currentSelection) System.out.print("\u001b[48;5;" + 243 + "m"); //grigio
            System.out.print(i);
            System.out.print(output);
            if (i == currentSelection) System.out.print(Colors.reset());
            System.out.print("║");
            Terminal.moveAbsoluteCursor(init[0] + 4 + i, init[1]);
        };
    };

    public void printCloseLine() {
        //closeline
        System.out.print("╚");
        for (int i = 0; i< (width); i++ ){
            System.out.print("═");
        }
        System.out.print("╝");
    }
}
