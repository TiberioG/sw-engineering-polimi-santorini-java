package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.controller.Card;
import it.polimi.ingsw.controller.CardManager;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import it.polimi.ingsw.model.*;

import java.awt.*;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sta classe serve solo per testare il flusso del gioco senza tutta la sbatta di usare MVC
 */
public class CLIRunnable {
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);
    private List<String > usernames  = new ArrayList<>();
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<Date> dates = new ArrayList<>();

    private HashMap<Integer, Card> cardMap ;

    private int loggedUsers = 0;
    private static int  MAXPLAYERS = 3;
    private static int MINPLAYER = 2;
    private Random rand = new Random();

    private Match match;

    private Utils utils = new Utils(in, out);


    /* METHODS*/


     public void startGame(){
        out.println("Welcome to Santorini");
    }

     public void userLogin() throws ParseException {
        out.println("How many players?");
        int askedPlayers = Integer.parseInt(in.nextLine());

        //TODO fix this for clients and better idea of using counter
        while (loggedUsers < askedPlayers) {
            out.println("nome?");
            usernames.add(in.nextLine());
            out.println("birthdate format dd/MM/yyyy");
            dates.add( dateFormat.parse(in.nextLine()) );
            loggedUsers++;
        }

        // only when I hve all the player => I can create the match
        for (int i = 0; i<loggedUsers; i++){

        }

    }

     public Player setRandomFirstUser() throws PlayerNotPresentException{
       //TODO andrà messa nel controler secondo me sta roba, ed esporre alla view solo il current player
        int randomNum = rand.nextInt(loggedUsers);
        match.setCurrentPlayer(match.getPlayers().get(randomNum)); // set current player a random one
         return match.getCurrentPlayer();

    }

    public void cardSelection() throws InterruptedException {
        //get list of card names
        //todo invocare il controller qui, ho bisogno che mmi arrivi una copia della hashmap
        CardManager myCardManager = CardManager.initCardManager();
        cardMap = myCardManager.getCardMap();

        String[] cardNames = new String[cardMap.size()];


        //print all card names
        for(int i=0; i<cardMap.size(); i++) {
            cardNames[i] = Colors.randomColor() + cardMap.get(i).getName() + Colors.reset();
        }

        utils.singleTableCool("Cards Available", cardNames, 100);


    }


    public void setInitialPosition( ) throws InterruptedException {
        out.println("I's time to choose one color for your workers, choose from following list:");
        utils.singleTableCool("options", Colors.allNamesColored(), 100);
        int choice = utils.readNumbers(0,Colors.allNamesColored().length);

        out.println("Wooow, you have selected color " + Colors.allNamesColored()[choice]+ " for your workers");

        //todo chiamata al controller per settare player e colore
        //Controller.setWorkerColor(player);

         out.println("Now put your first worker in one spot of the board format");

         utils.readPosition(0,5);

        //todo chiamata al controller per settare posizione iniziale
        //Controller.setWorkerColor(player); //questo deve tirare un porco se stai mettendo il worker dove c'è già uno di un tuo avversario


    }


    public void showIsland(){
         //todo cambiare questi e usare i corretti riferimenti
        Cell[][] field = match.getIsland().getField();  //I need the matrix of cells
        Location location = match.getLocation(); //And the location

        String[][] stringIsland = new String[field.length][field.length]; //initialize string version of the fields

        //let's fill in the new matrix
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                stringIsland[i][j] = "  LEV" + field[i][j].getTower().getTopComponent().getComponentCode() + " ";
                if (location.getOccupant(field[i][j]) != null) {
                    //case cell is with worker
                    String owner = location.getOccupant(field[i][j]).getOwner().getName();
                    String trimOwner = owner.substring(0, Math.min(owner.length(), 3)); // trim to 3 chars the name of player
                    String workerCol = location.getOccupant(field[i][j]).getColor().getAnsiCode(); //get color of worker
                    stringIsland[i][j] = Colors.reset() + workerCol + stringIsland[i][j] + trimOwner + Colors.reset() + "  ";
                } else {
                    //case cell WITHOUT worker
                    stringIsland[i][j] = "  " + stringIsland[i][j] + "   ";
                }
            }
        }

        //boring stuff to print things
        String lineSplit = "";
        StringJoiner splitJoiner = new StringJoiner("┼", "|", "|");
        for (int i= 0; i <5 ; i++) {
            splitJoiner.add(String.format("%14s", "").replace(" ", "-"));
        }
        lineSplit = splitJoiner.toString();
        for (String[] row : stringIsland) {
            StringJoiner sj = new StringJoiner(" | ", "| ", " |");
            for (String col : row) {
                sj.add(col);
            }
            System.out.println(lineSplit);
            System.out.println(sj.toString());
        }
        System.out.println(lineSplit);

    }


    /**
     * dumb version , the user just selects where he wants to move,
     */
    public void moveWorkerDumb() {
        System.out.println("Scegli il tuo worker");
        System.out.println("muovi il tuo worker: formato destinazione:  x,y ");


    }



    public static void main( String[] args ) throws ParseException, InterruptedException {
        CLIRunnable thiscli = new CLIRunnable();
        /*
        System.out.println("Loading");
        for (int i =0; i<101; i++) {
            TimeUnit.MILLISECONDS.sleep(10);
            System.out.print("\u001b[200D" + i + "%");
            System.out.flush();
        }
        System.out.println(" ");
        thiscli.userLogin();
        thiscli.cardSelection(); */

        thiscli.setInitialPosition();





    }


}



