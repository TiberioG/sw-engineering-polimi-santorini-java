package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.exceptions.OldUserException;
import it.polimi.ingsw.psp40.exceptions.YoungUserException;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.ViewInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * @author TiberioG
 */
public class CoolCLI implements ViewInterface {
    private int DELAY;
    private final Client client;
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private Colors colorWorker; //this is the color of the workers of the player
    private int currentWorkerId = 0; //temp var used to store the selected worker before move/build
    private Card thiscard;

    private Date date = null; //
    private int numOfPlayers = 0;

    private static final int MIN_PORT = 1000; // todo usare quelli del server. Possibile?
    private static final int MAX_PORT = 50000;
    private static final int ROWS = 50;  //number of rows of terminal window
    private static final int COLS = 160; //number of columns of terminal window
    private static final int SPEED = 1000; //seconds to keep showing message before going on

    private final Utils utils = new Utils(in, out);

    /* Frames */
    private static Frame upper = new Frame(new int[]{0,0}, new int[]{10, COLS}, in, out);
    private static Frame center = new Frame(new int[]{10,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame center2 = new Frame(new int[]{16,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame center3 = new Frame(new int[]{24,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame lower = new Frame (new int[]{ROWS - 6 ,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame lower2 = new Frame (new int[]{ROWS - 2 ,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame left = new Frame(new int[]{10,0}, new int[]{99, 58}, in, out);
    private static Frame islandFrame = new Frame(new int[]{8,80}, new int[]{99, 58}, in, out);
    private static Frame lowerLeft = new Frame(new int[]{ROWS -15,0}, new int[]{99, 58}, in, out);

    private boolean fastboot = false;

    private IslandAdapter myisland;
    private Hourglass hour;
    private ExecutorService executor;

    /**
     * Constructor
     * @param client
     */
    public CoolCLI(Client client) {

        if(!fastboot) {
            DELAY = 100;
        }
        else {
            DELAY = 0;
        }


        this.client = client; //associate with client
        /* Clear Terminal, a bit overkill but I'm sure terminal window is clean with no history */
        Terminal.resize(ROWS, COLS);  //force terminal window size
        Terminal.superClear();   //this clears all the previous history of terminal
        Terminal.clearAll(); //this clear
        Terminal.clearScreen();
        center.clear();

        try {
            maketitle();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to Read the server ip/URL and port
     */
    @Override
    public void displaySetup() {
        int port = 0;
        String ip ;
        center.clear();
        /* Real working  mode: gets input from user */
        if(!fastboot) {
            /* reading ip/URL of server */

            center.center(utils.form("enter address of server", 30), DELAY); //print form
            Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
            ip = in.nextLine();
            while (!Utils.isValidIp(ip)) {
                lower.print("This is not a valid IPv4 address. Please, try again:");
                center.center(utils.form("enter address of server", 30), DELAY);
                Terminal.moveRelativeCursor(-1, -29);
                ip = in.nextLine();
            }
            lower.clear(); // used to clear lower part where are displayed errors

            /* reading port of server */
            center2.center(utils.form("Enter port number", 30), DELAY);
            Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
            try {
                port = Integer.parseInt(in.nextLine()); // better use nextLine and parse int than using nextInt
            } catch (NumberFormatException e) {
                center2.center(utils.form("enter port number", 30), DELAY);
                Terminal.moveRelativeCursor(-1, -29);
                in.nextLine();
            }

            while (port < MIN_PORT || port > MAX_PORT) {
                lower.print("Value must be between " + MIN_PORT + " and " + MAX_PORT + ". Please, try again:");
                center2.center(utils.form("enter port number", 30), DELAY);
                Terminal.moveRelativeCursor(-1, -29);
                try {
                    port = Integer.parseInt(in.nextLine()); // better use nextLine and parse int than using nextInt
                } catch (NumberFormatException e) {
                    center2.center(utils.form("enter port number", 30), DELAY);
                    Terminal.moveRelativeCursor(-1, -29);
                    in.nextLine();
                }
            }
        }//end real use mode
        /* DEBUG MODE aka fastboot: connects to localhost port 1234 */
        else{
            ip = "localhost";
            port = 1234;
            out.println("DEBUG server localhost:1234");
        }

        /* send data to server */
        client.setServerIP(ip);
        client.setServerPort(port);
        client.connectToServer();
    }

    /**
     * Method used to show server not reachable
     */
    @Override
    public void displaySetupFailure() { //todo better
        left.clear();
        out.println("Can not reach the server, please try again");
        displaySetup();
    }

    //todo javadoc
    @Override
    public void displayLogin() {
        String username;
        Date date = null;
        center.clear();
        center2.clear();
        left.clear();//importante sia l'ultimo clear

        if(!fastboot) {
            /* reading username */
            center.center(utils.form("Enter username ", 30), DELAY); //print form
            Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
            username = in.nextLine();
            while (!Utils.isValidUsername(username)) {
                lower.print("This is not a valid username");
                center.center(utils.form("Enter username ", 30), 0); //print form
                Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
                username = in.nextLine();
            }
            lower.clear(); // used to clear lower part where are displayed errors

            //reading birthdate
            do{
                try{
                    center2.center(utils.formPrefilled("Enter birthdate ", 30, "dd/mm/yyyy"), DELAY); //print form
                    Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
                    date = utils.isValidDate(in.nextLine());
                }
                catch (ParseException e) {
                    lower.print("Wrong format of date");
                } catch (YoungUserException e) {
                    lower.print("You're too young to play this game");
                } catch (OldUserException e) {
                    lower.print("You're too old to play this game");
                }
                center2.center(utils.formPrefilled("Enter birthdate ", 30, "dd/mm/yyyy"), 0); //print form
                Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form

            }while (date == null);

            lower.clear(); // used to clear lower part where are displayed errors


            /* reading port of server */
            center3.center(utils.form("How many players?", 30), DELAY);
            Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
            try {
                numOfPlayers = Integer.parseInt(in.nextLine()); // better use nextLine and parse int than using nextInt
            } catch (NumberFormatException e) {
                center3.center(utils.form("how many players?", 30), 0);
                Terminal.moveRelativeCursor(-1, -29);
                in.nextLine();
            }

            while (numOfPlayers < 2 || numOfPlayers > 3) {
                lower.print("Value must be between " + 2 + " and " + 3 + ". Please, try again:");
                center3.center(utils.form("how many players?", 30), 0);
                Terminal.moveRelativeCursor(-1, -29);
                try {
                    numOfPlayers = Integer.parseInt(in.nextLine()); // better use nextLine and parse int than using nextInt
                } catch (NumberFormatException e) {
                    center3.center(utils.form("how many players?", 30), 0);
                    Terminal.moveRelativeCursor(-1, -29);
                    in.nextLine();
                }
            }

        }//if fastboot enabled
        else {
            username = new Date().toString();
            DateFormat dateFormat = new SimpleDateFormat(Configuration.formatDate);
            try {
                date =  dateFormat.parse("01/01/1900");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            numOfPlayers = 2;
        }

        client.setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, date, numOfPlayers, TypeOfMessage.LOGIN);
        client.sendToServer(loginMessage);

    }

    @Override
    public void displayLoginSuccessful() {
        /*left.clear();
        left.printWrapped("You have been logged in successfully");

         */
    }

    @Override
    public void displayLoginFailure(String details) {
        lower.print(details);
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        displayLogin();
    }

    @Override
    public void displayUserJoined(String details) {
       /* left.clear();
        left.printWrapped(details);

        */
    }

    @Override
    public void displayAddedToQueue(String details) {
        hour = new Hourglass(center, lower);
        executor = Executors.newFixedThreadPool(1);
        executor.execute(hour);
        left.clear();
        left.printWrapped(details);
    }

    @Override
    public void displayStartingMatch() {
        hour.cancel();
        executor.shutdownNow();
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        center.clear();
        lower.clear();

        try {
            lower.center(URLReader(getClass().getResource("/ascii/starting")), 100);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void displayGenericMessage(String message) {
        left.clear();
        lower.center(message,100);
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }

    }

    @Override
    public void displayDisconnected(String details) {
        left.printWrapped(details);
        client.close();
    }


    @Override
    public void displayLobbyCreated(String playersWaiting) {
        hour = new Hourglass(center, lower);
        executor = Executors.newFixedThreadPool(1);
        executor.execute(hour);
    }

    /**
     * Method used to show all the cards available and get from the user the selection of cards he want to use in the game
     * @param cards, an hashmap containing the {@link Card} instances indexed by ID
     * @param numPlayers number the player in game must be equal to number of cards to be selected
     */
    @Override
    public void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers) {
        hour.cancel();
        executor.shutdownNow();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        center.clear();
        lower.clear();

        left.clear(); // must be last clear

        CardSelector cardSelector = new CardSelector(cards, numPlayers, center);

        int[] selection = cardSelector.selection();

        /* sending to server */
        client.sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, selection));

    }

    @Override
    public void displayChoicePersonalCard(List<Card> availableCards) {
        hour.cancel();
        executor.shutdownNow();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        center.clear();
        lower.clear();

        left.clear(); // must be last clear
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }

        CardSelector cardSelector = new CardSelector(availableCards, 1,  center);
        int[] personalIdCard = cardSelector.selection();
        client.sendToServer(new Message(TypeOfMessage.SET_CARD_TO_PLAYER, personalIdCard[0]));

        center.clear();
        try {
            center.center(URLReader(getClass().getResource("/ascii/cards/" + personalIdCard[0])), 100);
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    @Override
    public void displayCardInGame(List<Card> cardInGame) {
        left.clear();
        CardSelector cardSelector = new CardSelector(cardInGame, 0,  center);
        //non printa nulla
        left.clear();
    }

    @Override
    public void displayForcedCard(Card card) {
        thiscard = card;
        center.clear();
        try {
            center.center(URLReader(getClass().getResource("/ascii/cards/" + card.getId())), 100);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        lower2.center("This is your card:" + card.getName(), 100);
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }


    }

    @Override
    public void displaySetInitialPosition(List<Player> playerList) {
        center.clear();
        left.clear();

        List<String> colorAlreadyUsed = playerList.stream().flatMap(player -> player.getWorkers().stream()).map(worker -> worker.getColor().toString()).distinct().collect(Collectors.toList());
        List<String> colorsAvailable = Arrays.asList(Colors.allNames()).stream().filter(colorAvailable -> colorAlreadyUsed.indexOf(colorAvailable) == -1).collect(Collectors.toList());


        center.center("Choose with keyboard arrows one color for your workers, confirm with SPACEBAR", 100);
        ColorSelector colorSelector = new ColorSelector(colorsAvailable, center2);
        int selection = colorSelector.selection();
        colorWorker = Colors.valueOf(colorsAvailable.get(selection));
        client.sendToServer(new Message(TypeOfMessage.SET_WORKERS_COLOR, colorWorker));

        center.clear();
        center2.clear();

        //START ISLAND

        left.clear();

        try {
            Terminal.noBuffer();
            this.updateIsland();
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        List<int[]> occupy = cellAdapter(client.getLocationCache().getAllOccupied()) ;

        left.printWrapped("Use arrow keys to select where you want to position your worker, confirm with SPACEBAR");
        int[] work1 =position(occupy, new int[]{0,0});

        left.clear();
        left.printWrapped("Use arrow keys to select where you want to position your worker, confirm with SPACEBAR");

        occupy.add(work1);
        int[] work2 = position(occupy, work1);
        left.clear();

        List<CoordinatesMessage> workercord = new ArrayList<>();

        workercord.add(new CoordinatesMessage(work1[0], work1[1]));
        workercord.add(new CoordinatesMessage(work2[0], work2[1]));

        client.sendToServer(new Message(TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(colorWorker, workercord)) );
    }

    @Override
    public void displayAskFirstPlayer(List<Player> allPlayers) {
        left.clear();
        center.clear();
        PlayerSelector playerSelector = new PlayerSelector(allPlayers, center);

        String playerselected = playerSelector.selection();
        client.sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, playerselected));
    }

    @Override
    public void displayChoiceOfAvailablePhases() {
        List<Phase> phaseList = client.getListOfPhasesCache();
        left.clear();
        try {
            updateIsland();
            myisland.clearMovable();
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Phase selectedPhase = null;
        if (phaseList.size() == 1) {
            selectedPhase = phaseList.get(0);
            left.println("there is only available this phase: " + selectedPhase.getType().toString());
            try {
                TimeUnit.MILLISECONDS.sleep(SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            String[] phases = phaseList.stream().map(phase -> phase.getType().toString()).toArray(String[]::new);

            left.clear();
            left.println(utils.tableString("Phases available", phases)); //todo set position absolute

            int index = utils.readNumbers(0, phaseList.size());
            selectedPhase = phaseList.get(index);
        }

        switch (selectedPhase.getType()) {
            case SELECT_WORKER:
                displayChoiceSelectionOfWorker();
                break;
            case MOVE_WORKER:
                client.sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
                left.println("passo fase succ");
                break;
            case BUILD_COMPONENT:
                client.sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_BUILD));
                left.println("passo fase succ");
                break;
        }

    }

    @Override
    public void displayChoiceOfAvailableCellForMove() {

        left.clear();
        List<Cell> availableCells = client.getAvailableMoveCells();

        try {
            updateIsland();
            myisland.clearMovable();
            myisland.setMovable(availableCells);
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        List<int[]> availableCellsCoord = cellAdapter(availableCells) ;
        left.println("These are the cells available for move");
        availableCellsCoord.forEach(cell ->  out.println(cell[0] + "," + cell[1]));

        displayMoveWorker();
    }

    @Override
    public void displayChoiceSelectionOfWorker() {
        left.clear();
        left.printWrapped("Choose worker using TAB, confirm with SPACEBAR, after selection press B if you want to go back to the selection of worker");
        Integer[] starting = getMyWorkers().get(currentWorkerId);

        try {
            this.updateIsland();
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        currentWorkerId = swapWorker();
        client.sendToServer(new Message(TypeOfMessage.SELECT_WORKER, currentWorkerId));
    }


    @Override
    public void displayMoveWorker() {
        Integer[] starting = getMyWorkers().get(currentWorkerId);

        int[] position = positionAllowed(starting, cellAdapter(client.getAvailableMoveCells()), 'm');

        if(position[0] >= 0 && position[1] >= 0) {
            CoordinatesMessage moveCoord = new CoordinatesMessage(position[0], position[1]);
            client.sendToServer(new Message(TypeOfMessage.MOVE_WORKER, moveCoord));
        }
        else{
            displayChoiceSelectionOfWorker();
        }
    }

    @Override
    public void displayChoiceOfAvailableCellForBuild() {
        left.clear();
        List<Cell> availableCells = new ArrayList<>(client.getAvailableBuildCells().keySet());

        try {
            updateIsland();
            myisland.clearMovable();
            myisland.setMovable(availableCells);
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        /*
        List<int[]> availableCellsCoord = cellAdapter(availableCells) ;
        left.println("These are the cells available for build");
        availableCellsCoord.forEach(cell ->  out.println(cell[0] + "," + cell[1]));
         */
        displayBuildBlock();

    }

    @Override
    public void displayBuildBlock() {
        left.clear();
        left.println("what cell would you like to build in?");

        Integer[] starting = getMyWorkers().get(currentWorkerId); //this this are the coordinates of the current worker
        List<Cell> availableBuildCell = client.getAvailableBuildCells().keySet().stream().collect(Collectors.toList()); // i get te list of all the Cells that can be built by

        int[] position = positionAllowed(starting, cellAdapter(availableBuildCell), 'b'); // read the coordinates where the user wants to build
        Cell cellToBuild = availableBuildCell.stream().filter(cell -> cell.getCoordX() == position[0] && cell.getCoordY() == position[1]).findFirst().orElse(null); // use this to get the corresponding Cell obj

        CoordinatesMessage buildCoord = new CoordinatesMessage(position[0], position[1]);

        List<Integer> listOfAvailableComponents = client.getAvailableBuildCells().get(cellToBuild); // getting the components buildable in the selected cell
        List<String> listOfStringComponent = Arrays.asList(Component.allNames());

        //using this to get the names of the components form all the names available
        String[] nameOfAvailableComponents = new String[listOfAvailableComponents.size()];
        for(int i=0; i<listOfAvailableComponents.size(); i++){
            nameOfAvailableComponents[i] = listOfStringComponent.get(listOfAvailableComponents.get(i));
        }

        if(listOfAvailableComponents.size()==1){
            myisland.clearMovable();
            myisland.clearSelected();
            myisland.setTempLevel(position[0], position[1], Component.valueOf(nameOfAvailableComponents[0]).getComponentCode());
            try {
                myisland.print();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.valueOf(nameOfAvailableComponents[0]), buildCoord)));

        }
        else {
            left.printWrapped("Choose one of the following blocks to build using TAB, press SPACEBAR when ready:");
            left.append(utils.tableString("Blocks available", nameOfAvailableComponents));

            int blockSelected = chooseblock(listOfAvailableComponents, position);

            client.sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.valueOf(nameOfAvailableComponents[blockSelected]), buildCoord)));
        }
    }

    private void updateIsland() {
        myisland = new IslandAdapter(client.getFieldCache(), client.getLocationCache(), islandFrame );
    }

    private int[] position( List<int[]> occupied, int[] starting ){
        boolean debug = false;
        int curRow = starting[0];
        int curCol = starting[1];
        myisland.setSelected(curRow, curCol);
        try {
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                        if (!contains(occupied, curRow, curCol)) {
                            myisland.setWorker(curRow, curCol, colorWorker);
                            myisland.clearSelected();
                            myisland.print();
                            break;
                        }
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
                //todo frame per except
            }
        }// end while true

        return new int[]{curRow, curCol};
    }

    private int[] positionAllowed(Integer[] starting, List<int[]> allowed , char kind){
        boolean debug = false;
        int curRow = starting[0];
        int curCol = starting[1];

        myisland.setSelected(curRow, curCol);
        try {
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {   // if press P -> quit this visualization
                        if(contains(allowed, curRow, curCol)) {
                            if(kind == 'm') {
                                myisland.setWorker(curRow, curCol, colorWorker);
                                myisland.clearSelected();
                                myisland.print();
                            }
                            else {
                                myisland.clearMovable();
                                myisland.setSelected(curRow, curCol);
                                myisland.print();
                            }
                            break;
                        }
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

                    // getting B for back
                    else if (c == 98){
                        curCol = -1;
                        curRow = -1;
                        break;
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
                //todo frame per except
            }
        }// end while true

        return new int[]{curRow, curCol};
    }


    private int chooseblock(List<Integer> listOfAvailableComponents, int[] position){
        boolean debug = false;
        int curRow = position[0];
        int curCol = position[1];

        int curBlk = 0;
        myisland.setSelected(curRow, curCol);
        try {
            myisland.print();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                        myisland.clearSelected();
                        myisland.print();
                        break;
                    }

                    //GETTING tab
                    if (c == 9){
                        if (curBlk < listOfAvailableComponents.size() -1){
                            curBlk ++;
                        }
                        else if (curBlk == listOfAvailableComponents.size() -1 ){
                            curBlk = 0;
                        }
                        left.append(Integer.toString(curBlk));
                        myisland.setTempLevel(position[0], position[1], listOfAvailableComponents.get(curBlk));
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
                //todo frame per except
            }
        }// end while true

        return curBlk;
    }

    private int swapWorker(){
        boolean debug = false;
        int curWorkId = 0;
        myisland.setSelected(getMyWorkers().get(curWorkId)[0], getMyWorkers().get(curWorkId)[1]);
        try {
            myisland.print();
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
        }
        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACE_BAR to positiom
                    if (c == 32) {
                        break;
                    }

                    //GETTING tab
                   if (c == 9){
                       if(curWorkId == 0){
                           curWorkId = 1;
                       }else {
                           curWorkId = 0;
                       }
                       myisland.setSelected(getMyWorkers().get(curWorkId)[0], getMyWorkers().get(curWorkId)[1]);
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
                //todo frame per except
            }
        }// end while true

        return curWorkId;
    }

    private void maketitle() throws InterruptedException, IOException {

        Terminal.hideCursor();
        String welcome = URLReader(getClass().getResource("/ascii/welcome"));
        String to = URLReader(getClass().getResource("/ascii/to"));
        String santorini = URLReader(getClass().getResource("/ascii/santorini"));

        upper.clear();
        upper.center(welcome, DELAY);
        TimeUnit.MILLISECONDS.sleep(DELAY);
        upper.clear();
        upper.center(to, DELAY);
        TimeUnit.MILLISECONDS.sleep(DELAY);
        upper.clear();
        upper.center(santorini, DELAY);
        TimeUnit.MILLISECONDS.sleep(DELAY);
        Terminal.showCursor();
    }


    @Override
    public void displayWinnerMessage() {
        left.println("Congratulations, you won!");
    }

    @Override
    public void displayLoserMessage() {
        left.println("I'm sorry, you lose!");
    }



    private List<int[]> cellAdapter(List<Cell> cellList){
        List<int[]> coord = new ArrayList<>();
        if(cellList.size() != 0) {
            coord = cellList.stream().map(Cell::getCoordXY).collect(Collectors.toList());
        }
        return coord;
    }

    private HashMap<Integer, Integer[]> getMyWorkers() {
        Location location = client.getLocationCache();
        Cell[][] field = client.getFieldCache();

        HashMap<Integer, Integer[]> workerInfo = new HashMap<>();

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                Worker occupant = location.getOccupant(i, j);
                if (occupant != null && occupant.getPlayerName().equals(client.getUsername())){
                    workerInfo.put(occupant.getId(), new Integer[]{i, j});
                }
            }
        }
        return workerInfo;
    }


    private boolean contains (List<int[]> lista, int x, int y){
        boolean bool = false;
        for (int[] ints : lista) {
            if (ints[0] == x && ints[1] == y) {
                bool = true;
                break;
            }
        }

        return bool;
    }

    public static String URLReader(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }


}



