package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.controller.Card;
import it.polimi.ingsw.controller.CardManager;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import it.polimi.ingsw.model.*;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
            match = new Match(1191991); //TODO gestire in modo automatico i nomi univoci dei macth salvando su disco
            match.createPlayer(usernames.get(i),dates.get(i));
        }


    }

     public Player setRandomFirstUser() throws PlayerNotPresentException{
       //TODO andrà messa nel controler secondo me sta roba, ed esporre alla view solo il current player
        int randomNum = rand.nextInt(loggedUsers);
        match.setCurrentPlayer(match.getPlayers().get(randomNum)); // set current player a random one
         return match.getCurrentPlayer();

    }

    public void cardSelection()  {
        //get list of card names
        //todo invocare il controller qui
        cardMap = CardManager.getCardMap();

        //print all card names
        for(int i=0; i<cardMap.size(); i++) {
            out.println(cardMap.get(i).getName());
        }
    }


    public void setInitialPosition() {


        out.println("Select your worker color:");
        out.println("\n\n╔═══════════════════════╗\n" +
                "║        OPTIONS        ║\n" +
                "╠═══════════════════════╣\n" +
                "║ 1. WHITE              ║\n" +
                "║ 2. BLACK              ║\n" +
                "║ 3. BLUE               ║\n" +
                "║ 4. RED                ║\n" +
                "║ 5. GREEN              ║\n" +
                "║ 6. YELLOW             ║\n" +
                "╚═══════════════════════╝");
        int choice = utils.readNumbers(1,6);

        //todo chiamata al controller per settare player e colore
        //Controller.setWorkerColor(player);

         out.println("Put you worker no1, format x, y ");

        //todo chiamata al controller per settare posizione iniziale
        //Controller.setWorkerColor(player); //questo deve tirare un porco se stai mettendo il worker dove c'è già uno di un tuo avversario


    }

    public void utilsTester(){
         //String arry = new String[]{}

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



    public static void main( String[] args ) throws ParseException {
        CLIRunnable thiscli = new CLIRunnable();

        //utils.listPrinter("Ciao", );


    }


}



