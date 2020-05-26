package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Card;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ColorSelector {

    private final static int SPACING = 3;

    private final static int LEN = 13;
    private final static int HEI = 7;

    int init;

    private Frame frame;
    List<String> colorsAvailable;

     public ColorSelector(List<String> colorsAvailable, Frame frame ){
        this.frame = frame;
        this.colorsAvailable = colorsAvailable;

        init  = frame.getInit()[1] + ( frame.getColSpan() - (LEN*colorsAvailable.size() + SPACING*(colorsAvailable.size() -1) )  )/2;

    }
    int selection (){
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        int selection = 0;
        List<Integer> selected = new ArrayList<Integer>();

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
                            if (next2 == 67) {                     //RIGHT  arrow
                                if (selection >= 0 && selection < colorsAvailable.size() -1) {
                                    selection++;
                                }
                            } else if (next2 == 68) {              //LEFT arrow
                                if (selection > 0 && selection <= colorsAvailable.size() -1) {
                                    selection--;
                                }
                            }
                        }
                        Terminal.moveAbsoluteCursor(frame.getInit()[0] + HEI , 0);
                        Terminal.clearLine();
                        print(selection);

                    }//end arrow management

                } //end system in available
            } catch (IOException e) {
            }
        }// end while true

        return selection;

    }


    private void print(int current)  {


        for(int i= 0; i < colorsAvailable.size(); i++ ){
            Terminal.moveAbsoluteCursor(frame.getInit()[0], init + i*(LEN + SPACING));
            for (int k = 0; k < HEI ; k ++){
                Terminal.moveAbsoluteCursor(frame.getInit()[0] + k, init + i*(LEN + SPACING)); // scendo di una riga ogni volta
                for (int j = 0; j < LEN; j++){
                    System.out.print(Colors.valueOf(colorsAvailable.get(i)).getAnsiCode()+ "█"); //verdino
                }
            }

            System.out.print(Colors.reset());

            if (i == current){
                Terminal.moveAbsoluteCursor(frame.getInit()[0] + HEI , init + i*(LEN + SPACING));
                for (int j = 0; j < LEN; j++){
                    System.out.print("▂");
                }
            }
        }
     }





}





