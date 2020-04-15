package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.controller.Card;
import it.polimi.ingsw.controller.CardManager;
import it.polimi.ingsw.controller.TurnProperties;
import it.polimi.ingsw.controller.strategies.strategyBuild.DefaultBuild;
import it.polimi.ingsw.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CoolCLI {
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

    public Player setRandomFirstUser() throws PlayerNotPresentException {
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

        String[] names = IntStream.range(0, cardMap.size()).mapToObj(i -> Colors.randomColor() + cardMap.get(i).getName() + Colors.reset()).toArray(String[]::new);


        utils.singleTableCool("Cards Available", names, 100);

        String[] selected = IntStream.range(0, 3).mapToObj(i -> names[utils.readNumbers(0, names.length - 1)]).toArray(String[]::new);

        utils.singleTableCool("Cards Selected", selected, 100);


    }


    public void setInitialPosition( ) throws InterruptedException {
        out.println("I's time to choose one color for your workers, choose from following list:");
        utils.singleTableCool("options", Colors.allNamesColored(), 100);
        int choice = utils.readNumbers(0,Colors.allNamesColored().length - 01);

        out.println("Wooow, you have selected color " + Colors.allNamesColored()[choice]+ " for your workers");


        out.println(String.format("Now you can position your worker no. %d", 1));

        int[] position = utils.readPosition(0,4);
        CoordinatesMessage coord = new CoordinatesMessage(position[0], position[1]);


    }


    public void showIsland() throws WorkerAlreadyPresentException, CellOutOfBoundsException, BuildLowerComponentException, ParseException, IOException, InterruptedException {

        Player player1;
        Player player2;
        Worker worker1_1;
        Worker worker1_2;
        Worker worker2_1;
        Worker worker2_2;
        Cell initCellWorker1_1;
        Cell initCellWorker1_2;
        Cell initCellWorker2_1;
        Cell initCellWorker2_2;
        StrategyBuild strategyBuild;
        TurnProperties turnProperties;


        match = new Match(1234, new VirtualView(new Server() ));

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String birthDate1 = "22/03/1998";
        String birthDate2 = "26/07/1997";
        Date date1 = dateFormat.parse(birthDate1);
        Date date2 = dateFormat.parse(birthDate2);
        player1 = match.createPlayer("Mariossssss", date1);
        player2 = match.createPlayer("Luigi", date2);

        worker1_1 = player1.addWorker(Colors.RED);
        worker1_2 = player1.addWorker(Colors.YELLOW);
        worker2_1 = player2.addWorker(Colors.BLUE);
        worker2_2 = player2.addWorker(Colors.GREEN);

        initCellWorker1_1 = match.getIsland().getCell(2, 2);
        initCellWorker1_2 = match.getIsland().getCell(3, 3);
        initCellWorker2_1 = match.getIsland().getCell(0, 0);
        initCellWorker2_2 = match.getIsland().getCell(1, 1);


        match.getLocation().setLocation(initCellWorker1_1, worker1_1);
        match.getLocation().setLocation(initCellWorker1_2, worker1_2);
        match.getLocation().setLocation(initCellWorker2_1, worker2_1);
        match.getLocation().setLocation(initCellWorker2_2, worker2_2);


        match.getIsland().getCell(0, 1).getTower().addComponent(Component.FIRST_LEVEL);
        match.getIsland().getCell(0, 1).getTower().addComponent(Component.SECOND_LEVEL);

        match.getIsland().getCell(1, 0).getTower().addComponent(Component.FIRST_LEVEL);
        match.getIsland().getCell(1, 0).getTower().addComponent(Component.SECOND_LEVEL);
        match.getIsland().getCell(1, 0).getTower().addComponent(Component.THIRD_LEVEL);

        match.getIsland().getCell(1, 2).getTower().addComponent(Component.FIRST_LEVEL);

        match.getIsland().getCell(4, 4).getTower().addComponent(Component.FIRST_LEVEL);
        match.getIsland().getCell(4, 4).getTower().addComponent(Component.SECOND_LEVEL);
        match.getIsland().getCell(4, 4).getTower().addComponent(Component.THIRD_LEVEL);
        match.getIsland().getCell(4, 4).getTower().addComponent(Component.DOME);

        strategyBuild = new DefaultBuild(match);
        turnProperties = new TurnProperties();
        TurnProperties.resetAllParameter();

        Cell[][] field = match.getIsland().getField();
        Location location =match.getLocation();

        String[][] stringIsland = new String[field.length][field.length]; //initialize string version of the fields
/*
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
*/
        //utils.printMap(stringIsland);

        IslandAdapter myisland = new IslandAdapter(match.getIsland().getField(), match.getLocation(), 10, 6);
        myisland.print();


    }


    /**
     * dumb version , the user just selects where he wants to move,
     */
    public void moveWorkerDumb() {
        System.out.println("Scegli il tuo worker");
        System.out.println("muovi il tuo worker: formato destinazione:  x,y ");


    }



    public static void main(String[] args) throws ParseException, InterruptedException, IOException, CellOutOfBoundsException, BuildLowerComponentException, WorkerAlreadyPresentException {
        CoolCLI thiscli = new CoolCLI();
        Terminal.resize(100, 200);
        Utils.maketitle();
        thiscli.cardSelection();

        Terminal.noBuffer();

        Terminal.moveAbsoluteCursor(60, 8);


        thiscli.showIsland();

        TimeUnit.MILLISECONDS.sleep(20000);


/*

            System.out.println("Loading");
            for (int i =0; i<101; i++) {
                TimeUnit.MILLISECONDS.sleep(10);
                System.out.print("\u001b[200D" + i + "%");
                System.out.flush();
            }

            System.out.print("\u001b[H"); //set cursor at top left
            System.out.print("\u001b[J"); //clear

            thiscli.userLogin();

            System.out.print("\u001b[H"); //set cursor at top left
            System.out.print("\u001b[J"); //clear

            thiscli.cardSelection();
            System.out.print("\u001b[H"); //set cursor at top left
            System.out.print("\u001b[J"); //set cursor at top left

            thiscli.setInitialPosition();
            System.out.print("\u001b[H"); //set cursor at top left
            System.out.print("\u001b[J"); //set cursor at top left

            //System.out.print("\u001b[2J" );

        Terminal.noBuffer();

        ArrayList<Integer> row = new ArrayList<Integer>();
        ArrayList<Integer> col = new ArrayList<Integer>();

        int ri = 0;
        int ci = 0;


        while (true) {
            if (System.in.available() != 0) {
                int c = System.in.read();  //read one char at a time in ascii code

                //GETTING Q to quit
                if (c == 113) {   // if press Q -> quit this visualization
                    break;
                }

                //GETTING I for island
                else if (c == 105) { // if press I -> show island
                    System.out.print("\u001b[H"); //set cursor at top left
                    System.out.print("\u001b[J"); //clear
                    System.out.print("\u001b[H"); //set cursor at top left
                    thiscli.showIsland();
                }

                //Getting C to clear
                else if (c == 99) { // if I press C -> clear all
                    System.out.print("\u001b[H"); //set cursor at top left
                    System.out.print("\u001b[J"); //clear
                    System.out.print("\u001b[H"); //set cursor at top left
                }

                //getting an ARROW KEY
                else if (c == 27) { // first part of arrow key == ESC
                    int next1 = System.in.read();
                    int next2 = System.in.read();

                    if (next1 == 91) { //  read [
                        if (next2 == 65) {                   //UP  arrow
                            System.out.print("\u001b[A");   //set cursor UP
                        } else if (next2 == 66) {              //DOWN arrow
                            System.out.print("\u001b[B");   //set cursor DOWN
                        } else if (next2 == 67) {              //RIGHT arrow
                            System.out.print("\u001b[C");   //set cursor RIGHT
                        } else if (next2 == 68) {              //LEFT  arrow
                            System.out.print("\u001b[D");   //set cursor LEFT
                        }
                    }

                }//end arrow management

                // K to request current cursor position
                else if (c == 107) {
                    System.out.print("\u001b[6n"); //Sending this i receive ESC[row;colR
                    int next1 = System.in.read();
                    int next2 = System.in.read();

                    if (next1 == 27 && next2 == 91) { //parsing ESC[
                        do {
                            row.add( System.in.read());
                        } while (row.get(row.size() - 1) != 59); // ;
                        do {
                            col.add( System.in.read());
                        } while (col.get(col.size() - 1) != 82); // R

                        break;
                    }//endif

                }//end last elsif

            } //end system in available
        }// end while true

        System.out.print("\u001b[H"); //set cursor at top left
        System.out.print("\u001b[J"); //clear
        System.out.print("\u001b[H"); //set cursor at top left


        Terminal.yesBuffer();
        //in = new Scanner(System.in);

        int colonna = 0;
        int riga = 0;


        //conversion from ASCII -> String -> Int -> DECimal int
        for(int i = 0; i < col.size()-1; i++){
            String cifra = Character.toString(col.get(i));
            colonna = (int) (Integer.parseInt(cifra) * Math.pow(10, i) + colonna);
        }

        for(int i = 0; i < row.size()-1; i++){
            String cifra = Character.toString(row.get(i));
            riga = (int) (Integer.parseInt(cifra) * Math.pow(10, i) + riga);
        }



        System.out.println("Colonna è: " + colonna);
        System.out.println("Riga è: " + riga);

        thiscli.userLogin();
*/

    }//end MAIN



}
