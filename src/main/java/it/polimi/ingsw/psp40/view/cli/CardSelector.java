package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Card;
import org.davidmoten.text.utils.WordWrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to show the list of the names of the cards and the description in CLI
 *
 * @author TiberioG
 */
public class CardSelector {
    private final static int SPACING = 3;
    private int width = 20;
    private int extended = width + 5;

    private String title;
    private List<Card> cards = new ArrayList<>();
    private int toSelect;

    private Frame fLeft;
    private Frame fRight;

    /**
     * Constructo private used by the two public ones
     *
     * @param toSelect  number of card to select
     * @param container {@link Frame}
     */
    private CardSelector(int toSelect, Frame container) {
        fLeft = new Frame(new int[]{container.getInit()[0], (container.getColSpan() - (width + SPACING + extended)) / 2}, container.getAbsEnd(), container.getIn(), container.getOut());
        fRight = new Frame(new int[]{container.getInit()[0], ((container.getColSpan() - (width + SPACING + extended)) / 2) + width + SPACING}, container.getAbsEnd(), container.getIn(), container.getOut());

        this.toSelect = toSelect;
        switch (toSelect) {
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


    /**
     * Constructor using an hashmap
     *
     * @param hashMapCards a map of id and {@link Card}
     * @param toSelect
     * @param container
     */
    public CardSelector(HashMap<Integer, Card> hashMapCards, int toSelect, Frame container) {
        this(toSelect, container);
        for (int i = 0; i < hashMapCards.size(); i++) {
            this.cards.add(hashMapCards.get(i));
        }

    }

    /**
     * Constructor if using only a list of  {@link Card}
     *
     * @param availableCards a list of {@link Card}
     * @param toSelect
     * @param container
     */
    public CardSelector(List<Card> availableCards, int toSelect, Frame container) {
        this(toSelect, container);
        this.cards = availableCards;

    }

    /**
     * method that allows to select multiple cards, as much as defined as parameter in the constructor
     *
     * @return an array of the ids of the selected cards
     */
    int[] selectionMultiple() {
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        int position = 0;
        List<Integer> positionSelectedList = new ArrayList<Integer>();

        Terminal.hideCursor();
        print(position, positionSelectedList);
        showText(position);
        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                        if (positionSelectedList.size() < toSelect) {
                            if (!positionSelectedList.contains(position)) {
                                positionSelectedList.add(position);
                                print(position, positionSelectedList);
                            }
                            if (positionSelectedList.size() == toSelect) {
                                break;
                            }
                        } else break;
                    }

                    //getting an ARROW KEY
                    else if (c == 27) { // first part of arrow key == ESC
                        int next1 = System.in.read();
                        int next2 = System.in.read();
                        if (next1 == 91) { //  read [
                            if (next2 == 65) {                     //UP  arrow
                                if (position > 0 && position <= cards.size() - 1) {
                                    position--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (position >= 0 && position < cards.size() - 1) {
                                    position++;
                                }
                            }
                        }
                        print(position, positionSelectedList);
                        showText(position);
                    }//end arrow management

                } //end system in available
            } catch (IOException e) {
            }
        }// end while true

        //conversion to array of IDs
        int[] ret = new int[toSelect];
        for (int i = 0; i < toSelect; i++) {
            ret[i] = cards.get(positionSelectedList.get(i)).getId();
        }
        return ret;

    }

    /**
     * method that allows to select only one card
     *
     * @return the ID of the card
     */
    int selectionSingol() {
        try {
            Terminal.noBuffer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        int position = 0;

        Terminal.hideCursor();
        print(position, null);
        showText(position);
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
                                if (position > 0 && position <= cards.size() - 1) {
                                    position--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (position >= 0 && position < cards.size() - 1) {
                                    position++;
                                }
                            }
                        }
                        print(position, null);
                        showText(position);
                    }//end arrow management

                } //end system in available
            } catch (IOException e) {
            }
        }// end while true

        return cards.get(position).getId();

    }

    /**
     * prints in terminal the cardSelector
     *
     * @param current
     * @param selected
     */
    private void print(int current, List<Integer> selected) {
        int height = cards.size();
        int innerwidth = width - 4;

        String titleString = Utils.centerString(width, title);
        StringBuilder title = new StringBuilder();

        //top line
        Terminal.moveAbsoluteCursor(fLeft.getInit()[0], fLeft.getInit()[1]);
        System.out.print("╔");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        Terminal.moveAbsoluteCursor(fLeft.getInit()[0] + 1, fLeft.getInit()[1]); // goo down one line

        //title line
        title.append("║").append(titleString.replaceAll("\n", " ").toUpperCase()).append("║");
        System.out.print(title);

        Terminal.moveAbsoluteCursor(fLeft.getInit()[0] + 2, fLeft.getInit()[1]); // goo down one line

        //close tile line
        System.out.print("╠");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╣");

        Terminal.moveAbsoluteCursor(fLeft.getInit()[0] + 3, fLeft.getInit()[1]); // goo down one line

        //middle item lines
        for (int i = 0; i < height; i++) {
            String nonewline = cards.get(i).getName().replaceAll("\n", " "); //remove newlines in text field
            String output = String.format(" %-" + innerwidth + "s", nonewline);

            System.out.print("║ ");

            if (selected != null && selected.contains(i)) {
                if (i == current) {
                    System.out.print("\u001b[48;5;" + 22 + "m"); //verdino scuro == current&&selected
                    System.out.print(" ");
                    System.out.print(output);
                    System.out.print(Colors.reset());
                } else {
                    System.out.print("\u001b[48;5;" + 35 + "m"); //verdino
                    System.out.print(" ");
                    System.out.print(output);
                    System.out.print(Colors.reset());
                }

            } else if (i == current) {
                System.out.print("\u001b[48;5;" + 243 + "m"); //grigio
                System.out.print(" ");
                System.out.print(output);
                System.out.print(Colors.reset());
            } else {
                System.out.print(" ");
                System.out.print(output);
            }
            System.out.print(" ║");

            Terminal.moveAbsoluteCursor(fLeft.getInit()[0] + 4 + i, fLeft.getInit()[1]);
        }

        //closeline
        System.out.print("╚");
        for (int i = 0; i < (width); i++) {
            System.out.print("═");
        }
        System.out.print("╝");
    }


    /**
     * method to print the description of the card
     *
     * @param cardId
     */
    private void showText(int cardId) {
        fRight.clearRight(); //ued to odelete previous box
        String titleString = Utils.centerString(width, "Card description"); //title
        StringBuilder title = new StringBuilder();
        String wrapped =  //content
                WordWrap.from(cards.get(cardId).getDescription())
                        .maxWidth(width)
                        .wrap();
        String[] lines = wrapped.split("\\r?\\n"); //split in lines

        //top line
        Terminal.moveAbsoluteCursor(fRight.getInit()[0], fRight.getInit()[1]);
        System.out.print("╔");
        for (int i = 0; i < extended; i++) {
            System.out.print("═");
        }
        System.out.print("╗");

        //title line
        Terminal.moveAbsoluteCursor(fRight.getInit()[0] + 1, fRight.getInit()[1]); // goo down one line
        title.append("║  ").append(titleString.replaceAll("\n", " ").toUpperCase()).append("   ║");
        System.out.print(title);

        //close tile line
        Terminal.moveAbsoluteCursor(fRight.getInit()[0] + 2, fRight.getInit()[1]); // goo down one line
        System.out.print("╠");
        for (int i = 0; i < (extended); i++) {
            System.out.print("═");
        }
        System.out.print("╣");

        Terminal.moveAbsoluteCursor(fRight.getInit()[0] + 3, fRight.getInit()[1]); // goo down one line

        //middle item lines
        for (int i = 0; i < lines.length; i++) {
            String output = String.format("  %-" + width + "s", lines[i]);
            System.out.print("║ ");
            System.out.print(output);
            System.out.print("  ║");
            Terminal.moveAbsoluteCursor(fRight.getInit()[0] + 4 + i, fRight.getInit()[1]);

        }

        //closeline
        System.out.print("╚");
        for (int i = 0; i < (extended); i++) {
            System.out.print("═");
        }
        System.out.print("╝");

    }
}
