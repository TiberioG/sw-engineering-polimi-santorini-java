package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.CardManager;
import it.polimi.ingsw.controller.TurnProperties;
import it.polimi.ingsw.controller.strategies.strategyBuild.DefaultBuild;
import it.polimi.ingsw.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualView;

import java.io.IOException;
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
    private static Frame left = new Frame(new int[]{7,0}, new int[]{99, 58});

    private Utils utils = new Utils(in, out);

    IslandAdapter2 myisland;

    //roba per test
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


    /* METHODS*/

    /**
     * just a method to build a ame to play for testing
     */
    public void init() throws WorkerAlreadyPresentException, CellOutOfBoundsException, ParseException, BuildLowerComponentException {
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

        initCellWorker1_1 = match.getIsland().getCell(0, 1);
        initCellWorker1_2 = match.getIsland().getCell(3, 3);
        initCellWorker2_1 = match.getIsland().getCell(1, 2);
        initCellWorker2_2 = match.getIsland().getCell(1, 0);


        match.getLocation().setLocation(initCellWorker1_1, worker1_1);
        match.getLocation().setLocation(initCellWorker1_2, worker1_2);
        match.getLocation().setLocation(initCellWorker2_1, worker2_1);
        match.getLocation().setLocation(initCellWorker2_2, worker2_2);


        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(0, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 0));
        match.getIsland().addComponent(Component.THIRD_LEVEL, match.getIsland().getCell(1, 0));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 2));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(3, 3));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(3, 3));
        match.getIsland().addComponent(Component.THIRD_LEVEL, match.getIsland().getCell(3, 3));
        match.getIsland().addComponent(Component.DOME, match.getIsland().getCell(3, 3));

        strategyBuild = new DefaultBuild(match);
        turnProperties = new TurnProperties();
        TurnProperties.resetAllParameter();

    }


    /**
     * just a method to build first blocks to test
     */
    public void build(int x, int y) throws CellOutOfBoundsException, BuildLowerComponentException {
        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(x, y));

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
    }

    public void cardSelection() throws InterruptedException {
        //get list of card names
        //todo invocare il controller qui, ho bisogno che mmi arrivi una copia della hashmap
        CardManager myCardManager = CardManager.initCardManager();
        cardMap = myCardManager.getCardMap();

        String[] names = IntStream.range(0, cardMap.size()).mapToObj(i -> Colors.randomColor() + cardMap.get(i).getName() + Colors.reset()).toArray(String[]::new);

        utils.singleTableCool("Cards Available", names, 100);
        System.out.println("Select " + 3 + " cards");

        List<Integer> selections = utils.readNotSameNumbers(0, names.length - 1, 3 );
        String[] strSelections = new String[3];

       for (int i =0 ; i< selections.size()  ; i++){
           strSelections[i] = names[selections.get(i)];
       }
        utils.singleTableCool("Card selected", strSelections, 100);

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


    public void showIsland() throws  IOException, InterruptedException {

        Cell[][] field = match.getIsland().getField();
        Location location =match.getLocation();

        myisland = new IslandAdapter2(match.getIsland().getField(), match.getLocation());
        myisland.print();


    }



    public void moveWorker() {
        left.clear();
        left.writeln("Scegli dove muoverti usando le frecce,\n usa il tasto B per costruire un primo livello, \n Q per continuare");
        Terminal.hideCursor();
        int curRow = 0;
        int curCol = 0;
        try {
            this.showIsland();
        } catch ( IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Terminal.moveAbsoluteCursor(7, 60);
            myisland.higlight(curRow, curCol);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING Q to quit
                    if (c == 113) {   // if press Q -> quit this visualization
                        break;
                    }

                    //getting an ARROW KEY
                    else if (c == 27) { // first part of arrow key == ESC
                        int next1 = System.in.read();
                        int next2 = System.in.read();
                        if (next1 == 91) { //  read [
                            if (next2 == 65) {                     //UP  arrow
                                if (curRow > 0 && curRow <= 5){
                                    curRow --;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (curRow >= 0 && curRow < 4){
                                    curRow ++;
                                }
                            } else if (next2 == 67) {              //RIGHT arrow
                                if (curCol >= 0 && curCol < 4){
                                    curCol ++;
                                }
                            } else if (next2 == 68) {               //LEFT  arrow
                                if (curCol > 0 && curCol <= 5){
                                    curCol --;
                                }
                            }
                        }
                        myisland.higlight(curRow, curCol);
                    }//end arrow management

                    else if (c == 98){
                        build(curRow, curCol);
                        this.showIsland();
                    }

                } //end system in available
            } catch (IOException | InterruptedException e) {
            } catch (CellOutOfBoundsException e) {
            } catch (BuildLowerComponentException e) {
            }
        }// end while true

    }



    public static void main(String[] args) throws ParseException, InterruptedException, IOException, CellOutOfBoundsException, BuildLowerComponentException, WorkerAlreadyPresentException {
        CoolCLI thiscli = new CoolCLI();
        thiscli.init();

        Terminal.resize(110, 150);

        Utils.maketitle();

        left.print();
        thiscli.userLogin();
        left.clear();
        thiscli.cardSelection();

        Terminal.noBuffer();
        thiscli.showIsland();
        thiscli.moveWorker();
        Terminal.showCursor();

       left.clear();
       left.print();
       Terminal.yesBuffer();
       thiscli.cardSelection();

        TimeUnit.MILLISECONDS.sleep(20000);

    }//end MAIN



}
