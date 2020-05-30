package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.PhaseType;
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
 * This is the class used to display a beautiful and cool cli.
 * Works with UNIX terminals, need support of Unicode and 255-colors
 *
 * "We made the buttons on the screen look so good you'll want to lick them" S.Jobs
 * @author TiberioG
 */
public class CoolCLI implements ViewInterface {
    private int DELAY; // used to set delay of animations
    private final Client client;
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private int currentWorkerId = 0; //temp var used to store the selected worker before move/build
    private Card thiscard;

    private Date date = null; //
    private int numOfPlayers = 0;

    private static final int MIN_PORT = Client.MIN_PORT;
    private static final int MAX_PORT = Client.MAX_PORT;
    private static final int ROWS = 50;  //number of rows of terminal window
    private static final int COLS = 160; //number of columns of terminal window
    private static final int SPEED = 1000; //seconds to keep showing message before going on

    private final Utils utils = new Utils(in, out);

    /* Frames */
    private static Frame upper = new Frame(new int[]{0,0}, new int[]{10, COLS}, in, out);
    private static Frame center = new Frame(new int[]{10,0}, new int[]{ROWS-2, COLS}, in, out);
    private static Frame center2 = new Frame(new int[]{16,0}, new int[]{ROWS-3, COLS}, in, out);
    private static Frame center3 = new Frame(new int[]{24,0}, new int[]{ROWS-3, COLS}, in, out);
    private static Frame lower = new Frame (new int[]{ROWS - 6 ,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame lower2 = new Frame (new int[]{ROWS - 2 ,0}, new int[]{ROWS, COLS}, in, out);
    private static Frame left = new Frame(new int[]{10,0}, new int[]{ROWS -3, 58}, in, out);
    private static Frame islandFrame = new Frame(new int[]{8,80}, new int[]{ROWS -3, 58}, in, out);

    private boolean fastboot = false;
    private boolean debug = false;

    private IslandAdapter myisland;
    private Hourglass hourbig;
    private Hourglass hourlat;
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
        } catch (IOException e) {
            //e.printStackTrace();
        }

        hourbig = new Hourglass(center, lower, false);
        executor = Executors.newFixedThreadPool(1);
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
                lower.center("This is not a valid IPv4 address. Please, try again:", 0);
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
                lower.center("Value must be between " + MIN_PORT + " and " + MAX_PORT + ". Please, try again:", 0);
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
     * Method used to show server is not reachable
     */
    @Override
    public void displaySetupFailure() {
        lower.center("Can not reach the server, please try again", DELAY);
        displaySetup();
    }

    /**
     * Method used to ask username and birthday
     */
    @Override
    public void displayLogin() {
        String username;
        center.clear();
        center2.clear();
        left.clear();

        if(!fastboot) {
            /* reading username */
            center.center(utils.form("Enter username ", 30), DELAY); //print form
            Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
            username = in.nextLine();
            while (!Utils.isValidUsername(username)) {
                lower.center("This is not a valid username", 0);
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
                    lower.center("Wrong format of date",0);
                    center2.center(utils.formPrefilled("Enter birthdate ", 30, "dd/mm/yyyy"), 0); //print form delay 0!!
                    Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
                } catch (YoungUserException e) {
                    lower.center("You're too young to play this game", 0);
                    center2.center(utils.formPrefilled("Enter birthdate ", 30, "dd/mm/yyyy"), 0); //print form delay 0!!
                    Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
                } catch (OldUserException e) {
                    lower.center("You're too old to play this game", 0);
                    center2.center(utils.formPrefilled("Enter birthdate ", 30, "dd/mm/yyyy"), 0); //print form delay 0!!
                    Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
                }
            }while (date == null);
            lower.clear(); // used to clear lower part where are displayed errors

            /* reading port of server */
            center3.center(utils.form("How many players?", 30), DELAY);
            Terminal.moveRelativeCursor(-1, -29); //this is used to force the cursor inside the form
            try {
                numOfPlayers = Integer.parseInt(in.nextLine()); // better use nextLine and parse int than using nextInt
            } catch (NumberFormatException e) {
                center3.center(utils.form("how many players?", 30), 0); //only the first has delay, this is the following (case first not valid) and should be displayed immediatel
                Terminal.moveRelativeCursor(-1, -29);
                in.nextLine();
            }

            while (numOfPlayers < 2 || numOfPlayers > 3) {
                lower.center("Value must be between " + 2 + " and " + 3 + ". Please, try again:", 0);
                center3.center(utils.form("how many players?", 30), 0); //only the first has delay, this is the following (case first not valid) and should be displayed immediatel
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
                date =  dateFormat.parse(Configuration.minDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            numOfPlayers = 2;
        }

        //send to server
        client.setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, date, numOfPlayers, TypeOfMessage.LOGIN);
        client.sendToServer(loginMessage);
    }

    /**
     * Method to just show login was completed
     */
    @Override
    public void displayLoginSuccessful() {
        /*
        left.clear();
        lower.center("You have been logged in successfully");
        */
    }

    /**
     * Method to just show login was not completed and to restart the DisplayLogin
     */
    @Override
    public void displayLoginFailure(String details) {
        lower.center(details, 0);
        Utils.doTimeUnitSleep(SPEED); //show user message 1 sec before wiping out
        displayLogin(); //let's do it again
    }

    /**
     * Method to show details when a user has joined match
     * @param nameOfOPlayer
     * @param remainingPlayer
     */
    @Override
    public void displayUserJoined(String nameOfOPlayer, Integer remainingPlayer) {
       /*
       left.clear();
        left.printWrapped(details); //I think user doesn't really care about this
        */
    }

    /**
     * Method to show "waiting for other players "
     * @param playersWaiting I'm not showing this
     */
    @Override
    public void displayLobbyCreated(String playersWaiting) {
        executor.execute(hourbig); //starts beautiful hourglass
    }

    /**
     * Method to show "waiting for other players "
     * @param otherPlayer
     * @param remainingPlayer
     */
    @Override
    public void displayAddedToQueue(List<String> otherPlayer, Integer remainingPlayer) {
        center.clear();
        executor.execute(hourbig);
        left.clear();
        //left.printWrapped(details);
    }


    @Override
    public void displayProposeRestoreMatch() {
         killHourglass();
        center.clear();
        lower2.clear();
        lower.clear();
        List<String> optionList = new ArrayList<>();
        optionList.add("Yes");
        optionList.add("No");
        DefaultSelector defaultSelector = new DefaultSelector(center2, "A game was found broken, you want to restore it?", optionList, true);
        int indexOfSelection = defaultSelector.getSelectionIndex();
        client.sendToServer(new Message(TypeOfMessage.RESTORE_MATCH, optionList.get(indexOfSelection).equals("Yes")));
    }

    /**
     * This method kills the hourglass and tells players match is starting!!
     */
    @Override
    public void displayStartingMatch() {
        killHourglass();

        center.clear();
        lower.clear();

        try {
            lower.center(URLReader(getClass().getResource("/ascii/starting")), DELAY);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Utils.doTimeUnitSleep(SPEED);

    }


    /**
     * Method used to show generic messages, mainly for debug purposes
     * @param message the String from server
     */
    @Override
    public void displayGenericMessage(String message) {
        left.clear();
        lower.center(message,DELAY);
        Utils.doTimeUnitSleep(SPEED);
    }

    /**
     * Method used to show disconnection of user
     * @param details it's the string received from Server
     */
    @Override
    public void displayDisconnected(String details) {
        left.printWrapped(details);
        client.close();
    }

    /**
     * Method used to show all the cards available and get from the user the selection of cards he want to use in the game
     * @param cards, an hashmap containing the {@link Card} instances indexed by ID
     * @param numPlayers number the player in game must be equal to number of cards to be selected
     */
    @Override
    public void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers) {
        killHourglass();
        Utils.doTimeUnitSleep(DELAY);
        center.clear();
        lower.clear();
        left.clear(); // must be last clear

        CardSelector cardSelector = new CardSelector(cards, numPlayers, center);

        int[] selection = cardSelector.selectionMultiple();

        /* sending to server */
        client.sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, selection));

    }

    @Override
    public void displayChoicePersonalCard(List<Card> availableCards) {
        killHourglass();

        center.clear();
        lower.clear();

        left.clear(); // must be last clear
        Utils.doTimeUnitSleep(DELAY);

        CardSelector cardSelector = new CardSelector(availableCards, 1,  center);
        int personalIdCard = cardSelector.selectionSingol();
        client.sendToServer(new Message(TypeOfMessage.SET_CARD_TO_PLAYER, personalIdCard));

        center.clear();
        try {
            center.center(URLReader(getClass().getResource("/ascii/cards/" + personalIdCard)), DELAY);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        String nameCard = availableCards.stream().filter(card -> card.getId() == personalIdCard).map(card -> card.getName()).findFirst().orElse(null);

        lower2.center(client.getUsername() + " your card is: " + nameCard, DELAY);

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
            center.center(URLReader(getClass().getResource("/ascii/cards/" + card.getId())), DELAY);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        lower2.center(client.getUsername() + " your card is: "+ card.getName(), DELAY);
        Utils.doTimeUnitSleep(2000);

    }

    @Override
    public void displaySetInitialPosition(List<Player> playerList) {
        killHourglass();
        center.clear();
        left.clear();

        List<String> colorAlreadyUsed = playerList.stream().flatMap(player -> player.getWorkers().stream()).map(worker -> worker.getColor().toString()).distinct().collect(Collectors.toList());
        List<String> colorsAvailable = Arrays.asList(Colors.allNames()).stream().filter(colorAvailable -> colorAlreadyUsed.indexOf(colorAvailable) == -1).collect(Collectors.toList());


        center.center("Choose with keyboard arrows one color for your workers, confirm with SPACEBAR", DELAY);
        ColorSelector colorSelector = new ColorSelector(colorsAvailable, center2);
        int selection = colorSelector.selection();
        Colors colorWorker = Colors.valueOf(colorsAvailable.get(selection));
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
           // e.printStackTrace();
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

        left.clear();
        hourlat = new Hourglass(left, center, true);
        executor = Executors.newFixedThreadPool(1);
        executor.execute(hourlat);
    }

    @Override
    public void displayAskFirstPlayer(List<Player> allPlayers) {
        killHourglass();
        left.clear();
        center.clear();
        PlayerSelector playerSelector = new PlayerSelector(allPlayers, center);

        String playerSelected = playerSelector.selection();
        client.sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, playerSelected));
    }

    @Override
    public void displayChoiceOfAvailablePhases() {
    killHourglass();

        List<Phase> phaseList = client.getListOfPhasesCache();
        left.clear();
        try {
            updateIsland();
            myisland.clearMovable();
            myisland.print();
        } catch (IOException | InterruptedException e) {
           // e.printStackTrace();
        }

        Phase selectedPhase;
        if (phaseList.size() == 1) {
            selectedPhase = phaseList.get(0);
            if (selectedPhase.getType() != PhaseType.SELECT_WORKER) {
                left.println("There is only available this phase: " + selectedPhase.getType().toString());
                Utils.doTimeUnitSleep(SPEED);
            }

        } else {
            DefaultSelector defaultSelector = new DefaultSelector(left, "Select Phase", phaseList.stream().map(phase -> phase.getType().toString()).collect(Collectors.toList()), false);
            int indexOfSelection = defaultSelector.getSelectionIndex();
            selectedPhase = phaseList.get(indexOfSelection);
        }

        switch (selectedPhase.getType()) {
            case SELECT_WORKER:
                displayChoiceSelectionOfWorker();
                break;
            case MOVE_WORKER:
                client.sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
                break;
            case BUILD_COMPONENT:
                client.sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_BUILD));
                break;
            case END_TURN:
                client.sendToServer(new Message(TypeOfMessage.REQUEST_END_TURN));
                break;
        }

    }

    @Override
    public void displayChoiceOfAvailableCellForMove() {
    killHourglass();
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
        left.printWrapped("These are the cells available for move, go back to selection of worker pressing B ");
        if(debug) {
            availableCellsCoord.forEach(cell -> left.append(cell[0] + "," + cell[1]));
        }

        displayMoveWorker();
    }

    @Override
    public void displayChoiceSelectionOfWorker() {
  killHourglass();
        left.clear();
        center.clear();
        lower.clear();
        lower2.clear();
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


    public void displayMoveWorker() {
      killHourglass();
        left.clear();
        left.printWrapped("These are the cells available for move, go back to selection of worker pressing B ");
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
      killHourglass();
        left.clear();

        List<Cell> availableCells = new ArrayList<>(client.getAvailableBuildCells().keySet());
        if (availableCells.size() > 0) {
            left.printWrapped("These are the cells available for build");
            if (debug){
               // availableCells.forEach(cell ->  left.append(cell[0] + "," + cell[1]));
            }
            try {
                updateIsland();
                myisland.clearMovable();
                myisland.setMovable(availableCells);
                myisland.print();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            displayBuildBlock();
        } else {
            left.printWrapped("There are no cells available to build at this stage! Select another phase.");
            Utils.doTimeUnitSleep(SPEED);
            displayChoiceOfAvailablePhases();
        }
    }

    public void displayBuildBlock() {
        killHourglass();
        left.clear();
        left.printWrapped("What cell would you like to build in? Use arrow to select and confirm with SPACEBAR");

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
            left.printWrapped("Choose one of the following blocks to build using TAB \n press SPACEBAR when ready:");
            left.append(utils.tableString("Blocks available", nameOfAvailableComponents));

            int blockSelected = chooseblock(listOfAvailableComponents, position);

            client.sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.valueOf(nameOfAvailableComponents[blockSelected]), buildCoord)));
        }
    }


    /**
     * Method to show a message to the winner
     */
    @Override
    public void displayWinnerMessage() {
       killHourglass();

        upper.clear();
        islandFrame.clear();
        center.clear();
        left.clear();
        lower.clear();
        try {
            center.centerFixed(URLReader(getClass().getResource("/ascii/wincup")), 40, DELAY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lower.center("Congratulations " +  client.getUsername() + ", you won!", DELAY);
    }

    /**
     * Method to show a message to a loser
     * @param winningPlayer winning player if someone won or null if you lost
     */
    @Override
    public void displayLoserMessage(Player winningPlayer) {
       killHourglass();

        upper.clear();
        islandFrame.clear();
        center.clear();
        left.clear();
        lower.clear();
        try {
            center.centerFixed(URLReader(getClass().getResource("/ascii/loser")), 60, DELAY);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        lower.center("You lost the game " ,  DELAY);
    }


    @Override
    public void displayEndTurn() {
        left.clear();
        hourlat = new Hourglass(left, center, true);
        executor = Executors.newFixedThreadPool(1);
        executor.execute(hourlat);
    }

    @Override
    public void displayLoserPlayer(Player player) {

    }

    @Override
    public void displayCellUpdated(Cell cell) {
    }

    @Override
    public void displayLocationUpdated() {
    }

    /* Private heleper methods starting here */

    /**
     * Helper method used to instantiate the island adapter and recreate the island for the view using the one stored in the client's cache
     */
    private void updateIsland() {
        myisland = new IslandAdapter(client.getFieldCache(), client.getLocationCache(), islandFrame );
    }

    /**
     * Method used to position the first time the workers
     * @param occupied list of coordinates already occupied by a worker, so nt allowed
     * @param starting coordinates where to start displaying the selector
     * @return the coordinates of the chosen cell
     */
    private int[] position( List<int[]> occupied, int[] starting ){
        int curRow = starting[0];
        int curCol = starting[1];
        myisland.setSelected(curRow, curCol);
        try {
            myisland.print();
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
        }

        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                        if (!contains(occupied, curRow, curCol)) {
                            myisland.setWorker(curRow, curCol, client.getMyColor());
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
            }
        }// end while true

        return new int[]{curRow, curCol};
    }

    /**
     * Method used when a player can select a cell for build or to move
     * @param starting coordinates of the cell where start displaying the selector
     * @param allowed List of coordinates allowed to choose
     * @param kind m for a move else it'sa build
     * @return the coordinates choosen for move or for build, if user wants to back to selection of workers it returns a negative coordinate -1,-1
     */
    private int[] positionAllowed(Integer[] starting, List<int[]> allowed , char kind){
        int curRow = starting[0];
        int curCol = starting[1];

        myisland.setSelected(curRow, curCol);
        try {
            myisland.print();
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
        }

        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING SPACEBAR to positiom
                    if (c == 32) {
                        if(contains(allowed, curRow, curCol)) {
                            if(kind == 'm') {
                                myisland.setWorker(curRow, curCol, client.getMyColor());
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

                    // getting B for back only for move
                    else if (c == 98){
                        if(kind == 'm') { // if this it means I want to go back
                            curCol = -1;
                            curRow = -1;
                            break;
                        }
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

        return new int[]{curRow, curCol};
    }


    /**
     * Method used when a payer can position two or more different kind of blocks in a  cell.
     * This allows to choose using the keyboard and displays a preview of the block to build
     * @param listOfAvailableComponents list ok the levels availabel to build (as inteer)
     * @param position coordinates on map where to show the block
     * @return the level to build as integer
     */
    private int chooseblock(List<Integer> listOfAvailableComponents, int[] position){
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

                    //GETTING SPACEBAR to confirm block
                    if (c == 32) {
                        myisland.clearSelected();
                        myisland.print();
                        break;
                    }

                    //GETTING tab to change build block
                    if (c == 9){
                        if (curBlk < listOfAvailableComponents.size() -1){
                            curBlk ++;
                        }
                        else if (curBlk == listOfAvailableComponents.size() -1 ){
                            curBlk = 0;
                        }
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
            } catch (IOException | InterruptedException ignored) {

            }
        }// end while true

        return curBlk;
    }


    /**
     * Private helper method used to choose between workers using TAB
     * @return id of worker
     */
    private int swapWorker(){
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

                    //GETTING SPACE_BAR to confirm
                    if (c == 32) {
                        break;
                    }

                    //GETTING tab
                   if (c == 9){
                       if(curWorkId == 0){ //swap selection
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
            }
        }// end while true

        return curWorkId;
    }

    /**
     * Helper method used to show the title of "SANTORINI" game
     * @throws IOException
     */
    private void maketitle() throws IOException {
        Terminal.hideCursor();
        String welcome = URLReader(getClass().getResource("/ascii/welcome"));
        String to = URLReader(getClass().getResource("/ascii/to"));
        String santorini = URLReader(getClass().getResource("/ascii/santorini"));

        upper.clear();
        upper.center(welcome, DELAY);           //WELCOME
        Utils.doTimeUnitSleep(DELAY);

        upper.clear();
        upper.center(to, DELAY);                //TO
        Utils.doTimeUnitSleep(DELAY);

        upper.clear();
        upper.center(santorini, DELAY);        //SANTORINI
        Utils.doTimeUnitSleep(DELAY);
        Terminal.showCursor();
    }

    /**
     * Helper method to convert a list of cells into a list of array coordinates
     * @param cellList intput list of Cells
     * @return list of int[] of coordinates x,y
     */
    private List<int[]> cellAdapter(List<Cell> cellList){
        List<int[]> coord = new ArrayList<>();
        if(cellList.size() != 0) {
            coord = cellList.stream().map(Cell::getCoordXY).collect(Collectors.toList());
        }
        return coord;
    }

    /**
     * Helper method to het a map of all the workers of a player with their location
     * @return a map with for each id of worker it's location as array of integer [x,y]
     */
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


    /**
     * Helper method to check if in a list of coordinates array there are the specified coordinates x, y
     * @param intsList input list of coordinates as int[]
     * @param x coordinate x
     * @param y coordinate y
     * @return true if x,y is contained in the list of coordinates
     */
    private boolean contains (List<int[]> intsList, int x, int y){
        return intsList.stream().anyMatch(ints -> ints[0] == x && ints[1] == y);
    }

    /**
     * Method to read from resources a file as String
     * @param url path in resources folder
     * @return the string contained in the searched file
     * @throws IOException
     */
    private static String URLReader(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }



private void killHourglass(){
    if (hourbig!= null){
        hourbig.cancel();
    }
    if(hourlat != null) {
        hourlat.cancel();
    }
    if (executor != null) {
        executor.shutdownNow();
    }

    Utils.doTimeUnitSleep(500);
}

}
