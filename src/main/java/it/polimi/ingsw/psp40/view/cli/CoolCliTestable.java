package it.polimi.ingsw.psp40.view.cli;

import com.google.gson.Gson;
import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.DefaultMove;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.DefaultBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.network.server.Server;
import it.polimi.ingsw.psp40.network.server.VirtualView;
import it.polimi.ingsw.psp40.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CoolCliTestable {

    public CoolCliTestable(){
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("clitext.json"));

            // convert JSON file to map
            Map<String , String> messages = gson.fromJson(reader, Map.class);

            // print map entries
            for (Map.Entry<?, ?> entry : messages.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String OS = System.getProperty("os.name").toLowerCase();

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
    private static Frame left = new Frame(new int[]{7,0}, new int[]{99, 58}, in, out);

    private Utils utils = new Utils(in, out);

    private IslandAdapter myisland;



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
    StrategyMove strategyMove;


    /* METHODS*/

    /**
     * just a method to build a ame to play for testing
     */
    public void init() throws WorkerAlreadyPresentException, CellOutOfBoundsException, ParseException, BuildLowerComponentException {
        VirtualView virtualView = new VirtualView(new Server());
        match = new Match(66666, virtualView);
        virtualView.setMatch(match);

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
        strategyMove = new DefaultMove(match);
        match.getMatchProperties().resetAllParameter();

    }



    public void init2() throws WorkerAlreadyPresentException, CellOutOfBoundsException, ParseException, BuildLowerComponentException {
        VirtualView virtualView = new VirtualView(new Server());
        match = new Match(66666, virtualView);
        virtualView.setMatch(match);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String birthDate1 = "22/03/1998";
        String birthDate2 = "26/07/1997";
        Date date1 = dateFormat.parse(birthDate1);
        Date date2 = dateFormat.parse(birthDate2);
        player1 = match.createPlayer("Mariossssss", date1);
        player2 = match.createPlayer("Luigi", date2);

        worker1_1 = player1.addWorker(Colors.RED);
        worker1_2 = player1.addWorker(Colors.RED);
        worker2_1 = player2.addWorker(Colors.BLUE);
        worker2_2 = player2.addWorker(Colors.BLUE);

        initCellWorker1_1 = match.getIsland().getCell(2, 2);
        initCellWorker1_2 = match.getIsland().getCell(3, 3);
        initCellWorker2_1 = match.getIsland().getCell(0, 0);
        initCellWorker2_2 = match.getIsland().getCell(1, 1);
        match.getLocation().setLocation(initCellWorker1_1, worker1_1);
        match.getLocation().setLocation(initCellWorker1_2, worker1_2);
        match.getLocation().setLocation(initCellWorker2_1, worker2_1);
        match.getLocation().setLocation(initCellWorker2_2, worker2_2);

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(0, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 0));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 2));

        strategyMove = new DefaultMove(match);
    }




    /**
     * just a method to build first blocks to test
     */
    public void build(int x, int y) throws CellOutOfBoundsException, BuildLowerComponentException {
        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(x, y));

    }
/*
    public void userLogin() throws ParseException {
        left.start();
        out.println("How many players?");
        int askedPlayers = Integer.parseInt(in.nextLine());


        while (loggedUsers < askedPlayers) {
            out.println("nome?");
            usernames.add(in.nextLine());
            out.println("birthdate format dd/MM/yyyy");
            dates.add( dateFormat.parse(in.nextLine()) );
            loggedUsers++;
        }
        left.clear();
    }
*/
/*
    public void cardSelection() throws InterruptedException {
        left.start();
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
       left.clear();
    }
*/

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

        myisland = new IslandAdapter(match.getIsland().getField(), match.getLocation());
        myisland.print();


    }



    public void moveWorker() {

        boolean debug = false;
        List<Cell> availableCells ;
        left.clear();
        if (!OS.contains("win")) {
            left.printWrapped("Scegli dove muoverti usando le frecce, usa il tasto B per costruire un primo livello, usa i numeri per sceliere il worker che vuoi muovere, in blu compariranno le celle disponibili per muoverti, D per attivare modalità debug, Q per uscire");
        }
        else {
            left.printWrapped("Scegli dove muoverti usando WASD, usa il tasto B per costruire un primo livello, usa i numeri per sceliere il worker che vuoi muovere, in blu compariranno le celle disponibili per muoverti, shift D per attivare modalità debug, Q per uscire");
        }

        Terminal.hideCursor();
        int curRow = 0;
        int curCol = 0;
        try {
            this.showIsland();
        } catch ( IOException | InterruptedException e) {
            left.printWrapped(Arrays.toString(e.getStackTrace()));
        }

        if (!OS.contains("win")) {
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
                                    if (curRow > 0 && curRow <= 5) {
                                        curRow--;
                                    }
                                } else if (next2 == 66) {              //DOWN arrow
                                    if (curRow >= 0 && curRow < 4) {
                                        curRow++;
                                    }
                                } else if (next2 == 67) {              //RIGHT arrow
                                    if (curCol >= 0 && curCol < 4) {
                                        curCol++;
                                    }
                                } else if (next2 == 68) {               //LEFT  arrow
                                    if (curCol > 0 && curCol <= 5) {
                                        curCol--;
                                    }
                                }
                            }
                            myisland.setSelected(curRow, curCol);
                            myisland.print();
                        }//end arrow management

                        //getting B for build
                        else if (c == 98) {
                            myisland.setWorker(curRow, curCol, Colors.BLUE);
                            this.showIsland();
                        } else if (c == 49) {
                            availableCells = strategyMove.getAvailableCells(worker1_1);
                            curRow = match.getLocation().getLocation(worker1_1).getCoordX();
                            curCol = match.getLocation().getLocation(worker1_1).getCoordY();
                            myisland.setSelected(curRow, curCol);
                            myisland.clearMovable();
                            myisland.setMovable(availableCells);
                            myisland.print();
                        } else if (c == 50) {
                            availableCells = strategyMove.getAvailableCells(worker1_2);
                            curRow = match.getLocation().getLocation(worker1_2).getCoordX();
                            curCol = match.getLocation().getLocation(worker1_2).getCoordY();
                            myisland.setSelected(curRow, curCol);
                            myisland.clearMovable();
                            myisland.setMovable(availableCells);
                            myisland.print();
                        } else if (c == 51) {
                            availableCells = strategyMove.getAvailableCells(worker2_1);
                            curRow = match.getLocation().getLocation(worker2_1).getCoordX();
                            curCol = match.getLocation().getLocation(worker2_1).getCoordY();
                            myisland.setSelected(curRow, curCol);
                            myisland.clearMovable();
                            myisland.setMovable(availableCells);
                            myisland.print();
                        } else if (c == 52) {
                            availableCells = strategyMove.getAvailableCells(worker2_2);
                            curRow = match.getLocation().getLocation(worker2_2).getCoordX();
                            curCol = match.getLocation().getLocation(worker2_2).getCoordY();
                            myisland.setSelected(curRow, curCol);
                            myisland.clearMovable();
                            myisland.setMovable(availableCells);
                            myisland.print();
                        }

                        // gettind D for debug option
                        else if (c == 100) {
                            debug = !debug;
                            if (!debug) {
                                myisland.print();
                            }
                        }

                        if (debug) {
                            myisland.debug();
                        }

                    } //end system in available
                } catch (IOException | InterruptedException e) {
                }
            }// end while true
        }// end os not windsws
        else{
            while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING Q to quit
                    if (c == 113) {   // if press Q -> quit this visualization
                        break;
                    }

                    else if (c == 119) {                     //UP  == W
                        if (curRow > 0 && curRow <= 5) {
                                 curRow--;
                        }
                        myisland.setSelected(curRow, curCol);
                        myisland.print();
                    }

                    else if (c == 115) {              //DOWN == S
                        if (curRow >= 0 && curRow < 4) {
                                 curRow++;
                        }
                        myisland.setSelected(curRow, curCol);
                        myisland.print();
                    }

                    else if (c == 100) {              //RIGHT == D
                        if (curCol >= 0 && curCol < 4) {
                            curCol++;
                        }
                        myisland.setSelected(curRow, curCol);
                        myisland.print();
                    }

                    else if (c == 97) {               //LEFT  == A
                        if (curCol > 0 && curCol <= 5) {
                                 curCol--;
                        }
                        myisland.setSelected(curRow, curCol);
                        myisland.print();
                    }

                //end WASD management

                    //getting B for build
                    else if (c == 98) {
                        build(curRow, curCol);
                        this.showIsland();

                    } else if (c == 49) {
                        availableCells = strategyMove.getAvailableCells(worker1_1);
                        curRow = match.getLocation().getLocation(worker1_1).getCoordX();
                        curCol = match.getLocation().getLocation(worker1_1).getCoordY();
                        myisland.setSelected(curRow, curCol);
                        myisland.clearMovable();
                        myisland.setMovable(availableCells);
                        myisland.print();
                    } else if (c == 50) {
                        availableCells = strategyMove.getAvailableCells(worker1_2);
                        curRow = match.getLocation().getLocation(worker1_2).getCoordX();
                        curCol = match.getLocation().getLocation(worker1_2).getCoordY();
                        myisland.setSelected(curRow, curCol);
                        myisland.clearMovable();
                        myisland.setMovable(availableCells);
                        myisland.print();
                    } else if (c == 51) {
                        availableCells = strategyMove.getAvailableCells(worker2_1);
                        curRow = match.getLocation().getLocation(worker2_1).getCoordX();
                        curCol = match.getLocation().getLocation(worker2_1).getCoordY();
                        myisland.setSelected(curRow, curCol);
                        myisland.clearMovable();
                        myisland.setMovable(availableCells);
                        myisland.print();
                    } else if (c == 52) {
                        availableCells = strategyMove.getAvailableCells(worker2_2);
                        curRow = match.getLocation().getLocation(worker2_2).getCoordX();
                        curCol = match.getLocation().getLocation(worker2_2).getCoordY();
                        myisland.setSelected(curRow, curCol);
                        myisland.clearMovable();
                        myisland.setMovable(availableCells);
                        myisland.print();
                    }

                    // gettind shift D for debug option
                    else if (c == 68) {
                        debug = !debug;
                        if (!debug) {
                            myisland.print();
                        }
                    }

                    if (debug) {
                        myisland.debug();
                    }

                } //end system in available
            } catch (IOException | InterruptedException e) {
            } catch (CellOutOfBoundsException e) {
            } catch (BuildLowerComponentException e) {
                left.printWrapped(Arrays.toString(e.getStackTrace()));
            }
            }// end while true
        }// end windows

    }//end moveworker



    public static void main(String[] args) throws ParseException, InterruptedException, IOException, CellOutOfBoundsException, BuildLowerComponentException, WorkerAlreadyPresentException {
        CoolCliTestable thiscli = new CoolCliTestable();
        thiscli.init();
        Terminal.resize(110, 150);
        //Utils.maketitle();

        //left.border();
        //thiscli.userLogin();
        //thiscli.cardSelection();

        Terminal.noBuffer();
        thiscli.showIsland();
        thiscli.moveWorker();
        Terminal.showCursor();


       Terminal.yesBuffer();
       //thiscli.cardSelection();

        TimeUnit.MILLISECONDS.sleep(20000);

    }//end MAIN



}
