package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardSelector {
    private static int width = 30;
    private String title ;
    private List<Card> cards = new ArrayList<>();
    private int toSelect;

    private int[] absInit;

    public CardSelector(HashMap<Integer, Card> hashMapCards, int toSelect, int[] absInit){
        for (int i = 0; i< hashMapCards.size(); i++){
            this.cards.add(hashMapCards.get(i));
        }
        this.absInit = absInit;
        this.toSelect = toSelect;
        switch (toSelect){
            case 0:
                title = "Available cards";
                break;
            case 1:
                title = "Select " + toSelect + " card";
                break;
            default:
                title = "Select " + toSelect + " cards";
                break;
        }
    }

    public CardSelector(List<Card> availableCards, int toSelect, int[] absInit){
        this.cards = availableCards;
        this.absInit = absInit;
        this.toSelect = toSelect;

        switch (toSelect){
            case 0:
                title = "Available cards";
                break;
            case 1:
                title = "Select " + toSelect + " card";
                break;
            default:
                title = "Select " + toSelect + " cards";
                break;
        }

    }

    int[] selection (){
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        int selection = 0;
        List<Integer> selected = new ArrayList<Integer>();

        Terminal.hideCursor();
        print(selection, selected);
        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                        if ( selected.size() < toSelect) {
                            if(!selected.contains(selection)) {
                                selected.add(selection);
                                print(selection, selected);
                            }
                            if(selected.size() == toSelect){
                                break;
                            }
                        }
                        else break;
                    }

                    //getting an ARROW KEY
                    else if (c == 27) { // first part of arrow key == ESC
                        int next1 = System.in.read();
                        int next2 = System.in.read();
                        if (next1 == 91) { //  read [
                            if (next2 == 65) {                     //UP  arrow
                                if (selection > 0 && selection <= cards.size() -1) {
                                    selection--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (selection >= 0 && selection < cards.size() -1) {
                                    selection++;
                                }
                            }
                        }
                        print(selection, selected);
                    }//end arrow management

                } //end system in available
            } catch (IOException e) {
                //todo frame per except
            }
        }// end while true

        //conversion to array
        int[] ret = new int[toSelect];
        for (int i=0; i < toSelect; i++)
        {
            ret[i] = selected.get(i);
        }
        return ret;

    }


    private void print (int current, List<Integer> selected) {
        int height = cards.size();
        int innerwidth = width - 4;

        String titleString = Utils.centerString(width, title);
        StringBuilder title = new StringBuilder();

        //top line
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        System.out.print("╔");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        Terminal.moveAbsoluteCursor(absInit[0] + 1, absInit[1]); // goo down one line

        //title line
        title.append("║").append(titleString.replaceAll("\n", " ").toUpperCase()).append("║");
        System.out.print(title);

        Terminal.moveAbsoluteCursor(absInit[0] + 2, absInit[1]); // goo down one line

        //close tile line
        System.out.print("╠");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╣");

        Terminal.moveAbsoluteCursor(absInit[0] + 3, absInit[1]); // goo down one line

        //middle item lines
        for (int i = 0; i < height; i++) {
            String nonewline = cards.get(i).getName().replaceAll("\n", " "); //remove newlines in text field
            String output = String.format(". %-" + innerwidth + "s", nonewline);

            System.out.print("║ ");

            if (selected.contains(i)){
                if( i == current){
                    System.out.print("\u001b[48;5;" + 22 + "m"); //verdino scuro == current&&selected
                    System.out.print(i);
                    System.out.print(output);
                    System.out.print(Colors.reset());
                }
                else {
                    System.out.print("\u001b[48;5;" + 35 + "m"); //verdino
                    System.out.print(i);
                    System.out.print(output);
                    System.out.print(Colors.reset());
                }

            }
            else if (i == current) {
                System.out.print("\u001b[48;5;" + 243 + "m"); //grigio
                System.out.print(i);
                System.out.print(output);
                System.out.print(Colors.reset());
            } else {
                System.out.print(i);
                System.out.print(output);
            }
            System.out.print("║");

            Terminal.moveAbsoluteCursor(absInit[0] + 4 + i, absInit[1]);
        }

        //closeline
        System.out.print("╚");
        for (int i = 0; i< (width); i++ ){
            System.out.print("═");
        }
        System.out.print("╝");

    }

}
