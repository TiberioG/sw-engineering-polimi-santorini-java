package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.ViewInterface;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 *
 */
public class CLI implements ViewInterface {

    /**
     * Constructor
     * @param client where the CLI runs
     */
    public CLI(Client client) {
        this.client = client;
    }

    /* ATTRIBUTES */
    private final Client client;
    private Date date = null;
    private int numOfPlayers = 0;

    private static final PrintWriter out = new PrintWriter(System.out, true);
    private static final Scanner in = new Scanner(System.in);

    private static final int MIN_PORT = 1000; // todo usare quelli del server. Possibile?
    private static final int MAX_PORT = 50000;

    private final Utils utils = new Utils(in, out);

    private boolean debug = Configuration.DEBUG;




    /* METHODS*/

    /**
     * this class now just creates the match
     */

    private void showTitle() {
        out.println("Welcome to Santorini");
    }

    @Override
    public void displaySetup() {
        int port;
        String ip;
        showTitle();

        if (debug){
            ip = "localhost";
            port = 1234;
            out.println("DEBUG server localhost:1234");
        }else {
            out.println("IP address of server?");
            ip = utils.readIp();
            System.out.println("Port number?");
            port = utils.validateIntInput(MIN_PORT, MAX_PORT);
        }
        client.setServerIP(ip);
        client.setServerPort(port);
        client.connectToServer();
    }

    @Override
    public void displaySetupFailure() {
        out.println("Can not reach the server, please try again");
        displaySetup();
    }


    /**
     * method to read from console the players and add them to the match
     */
    @Override
    public void displayLogin() {
        String username;

        out.println("Choose your username:");
        if (debug){
            username = new Date().toString();
            DateFormat dateFormat = new SimpleDateFormat(Configuration.formatDate);
            try {
                date =  dateFormat.parse("01/01/1900");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            numOfPlayers = 2;
        }else{
            username = in.nextLine();
            date = utils.readDate("birthdate");

            out.println("How many people do you want to play with?");
            numOfPlayers = utils.validateIntInput( 2, 3);
        }


        client.setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, date, numOfPlayers, TypeOfMessage.LOGIN);
        client.sendToServer(loginMessage);
    }

    @Override
    public void displayLoginSuccessful() {
        out.println("You have been logged in successfully");
    }

    @Override
    public void displayLoginFailure(String details) {
        out.println(details);
        String username = in.nextLine();
        client.setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, date, numOfPlayers, TypeOfMessage.LOGIN);
        client.sendToServer(loginMessage);
    }

    @Override
    public void displayUserJoined(String details) {
        out.println(details);
    }

    @Override
    public void displayAddedToQueue(String details) {
        out.println(details);
    }

    @Override
    public void displayStartingMatch() {
        out.println("MATCH IS STARTING!!!!");
    }

    @Override
    public void displayGenericMessage(String message) {
        out.println(message);
    }

    @Override
    public void displayDisconnected(String details) {
        out.println(details);
        client.close();
    }

    /**
     * This method is used to display all the cards available to the user and make him choose a subset of them of cardinality equals to the number of players
     * @param cards a list of  {@link Card >}
     * @param numPlayers an int which is the number of player in ame which should equals to the number of selected cards
     */
    @Override
    public void displayCardSelection(HashMap<Integer,Card> cards, int numPlayers) {
        String[] names = cards.values().stream().map(Card::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Cards Available", names, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Select " + numPlayers + " cards");

        //String[] selectedCards = IntStream.range(0, numPlayers).mapToObj(i -> names[utils.readNumbers(0, names.length - 1)]).toArray(String[]::new);
        List<Integer> selections = utils.readNotSameNumbers(0, names.length - 1, numPlayers );
        List<Integer> listOfIdCardSelected = new ArrayList<>();

        for (Integer selection : selections) {
            String nameSelected = names[selection];
            for (HashMap.Entry<Integer, Card> entry : cards.entrySet()) {
                if (nameSelected.equals(entry.getValue().getName())) {
                    listOfIdCardSelected.add(entry.getKey());
                }
            }
        }
        client.sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, listOfIdCardSelected));
    }

    @Override
    public void displayChoicePersonalCard(List<Card> availableCards) {
        String[] names = availableCards.stream().map(Card::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Cards Available", names, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Choice your personal card");

        int numberSelected = utils.readNumbers(0, names.length - 1);
        int cardIdSelected = -1;
        for (Card card : availableCards) {
            if (card.getName().equals(names[numberSelected])) {
                cardIdSelected = card.getId();
            }
        }

        client.sendToServer(new Message(TypeOfMessage.SET_CARD_TO_PLAYER, cardIdSelected));

    }

    @Override
    public void displayCardInGame(List<Card> cardInGame) {
        String[] names = cardInGame.stream().map(Card::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Card selected", names, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displaySetInitialPosition(List<Player> playerList) {
        List<String> colorAlreadyUsed = playerList.stream().flatMap(player -> player.getWorkers().stream()).map(worker -> worker.getColor().toString()).distinct().collect(Collectors.toList());
        List<String> colorsAvailable = Arrays.asList(Colors.allNames()).stream().filter(colorAvailable -> colorAlreadyUsed.indexOf(colorAvailable) == -1).collect(Collectors.toList());

        String[] colorsAvailableArray = colorsAvailable.toArray(new String[0]);//conversion to string
        out.println("I's time to choose one color for your workers, choose from following list:");
        try {
            utils.singleTableCool("options", colorsAvailableArray, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int choice = utils.readNumbers(0,colorsAvailableArray.length - 1);
        out.println("Wooow, you have selected color " + colorsAvailableArray[choice]+ " for your workers");

        /* section to position the workers */
        this.showIsland();

        int[] position2;
        int[] position1;

        List<int[]> occupy = cellAdapter(client.getLocationCache().getAllOccupied()) ;

        do{
            out.println("Now you can position your worker no. 1");
            position1 = utils.readPosition(0,4);

        }while (occupy.contains(position1)); //todo debuggami

        do{
            out.println("Now you can position your worker no. 2");
             position2 = utils.readPosition(0,4);
             if(Arrays.equals(position1, position2)){
                 out.println("You cannot use the same position");
             }
        }while (occupy.contains(position2) || Arrays.equals(position2, position1));

        List<CoordinatesMessage> workercord = new ArrayList<>();

        workercord.add(new CoordinatesMessage(position1[0], position1[1]));
        workercord.add(new CoordinatesMessage(position2[0], position2[1]));

        client.sendToServer(new Message(TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(Colors.valueOf(colorsAvailableArray[choice]), workercord)) );
        out.println("done");

    }

    @Override
    public void displayAskFirstPlayer(List<Player> allPlayers)  {
        String[] names = allPlayers.stream().map(Player::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Players available", names, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //todo double column for also card display

        int selection = utils.readNumbers(0, names.length -1);
        client.sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, names[selection]));
    }

    @Override
    public void displayChoiceOfAvailablePhases(List<Phase> phaseList) {
        Phase selectedPhase = null;
        if (phaseList.size() == 1) {
            selectedPhase = phaseList.get(0);
            out.println("there is only available this phase: " + selectedPhase.getType().toString());
        } else {
            String[] phases = new String[phaseList.size()];
            for (int i = 0; i < phaseList.size(); i++) {
                phases[i] = phaseList.get(i).toString();
            }
            try {
                utils.singleTableCool("Phases available", phases, 100 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int index = utils.readNumbers(0, phaseList.size());
            selectedPhase = phaseList.get(index);
        }

        switch (selectedPhase.getType()) {
            case SELECT_WORKER:
                displayChoiceSelectionOfWorker();
                break;
            case MOVE_WORKER:
                client.sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
                break;
        }
    }
    @Override
    public void displayChoiceSelectionOfWorker() {
        showIsland();

        out.println("Choose worker selecting the id:");

        getMyWorkers().entrySet().forEach(entry -> {
            out.println("id: " + entry.getKey() + ", coordinates: " + entry.getValue()[0] + "," + entry.getValue()[1]);
        });

       int id = utils.readNumbers(0, getMyWorkers().size() -1);

        client.sendToServer(new Message(TypeOfMessage.SELECT_WORKER, id));

    }

    @Override
    public void displayChoiceOfAvailableCellForMove() {
        List<int[]> available = cellAdapter(client.getAvailableMoveCells()) ;

        out.println("These are the cells available for move");
        for(int i= 0; i<available.size(); i++){
            out.println(available.get(i)[0] + "," + available.get(i)[1] );
        }


    }


    @Override
    public void displayMoveWorker() {
        int[] position;

        List<int[]> available = cellAdapter(client.getAvailableMoveCells()) ;
        do{
            out.println("Now you can mover your worker");
            position = utils.readPosition(0,4);

        }while (available.contains(position));

        CoordinatesMessage moveCoord = new CoordinatesMessage(position[0], position[1]);
        client.sendToServer(new Message(TypeOfMessage.MOVE_WORKER, moveCoord));
    }


    @Override
    public void displayBuildBlock(){
        int[] position;
        out.println("Choose one of the following blocks to build:");

        try {
            utils.singleTableCool("Blocks available", Component.allNames(), 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int componentCode = utils.readNumbers(0, Component.allNames().length -1 );

        out.println("Now you can mover your worker");
        position = utils.readPosition(0,4);
        CoordinatesMessage buildCoord = new CoordinatesMessage(position[0], position[1]);

        TuplaGenerics<Component, CoordinatesMessage> message = new TuplaGenerics<>(Component.valueOf(Component.allNames()[componentCode]), buildCoord);


    }

    private void showIsland() {
        Location location = client.getLocationCache();
        Cell[][] field = client.getFieldCache();

        String[][] stringIsland = new String[field.length][field.length]; //initialize string version of the fields

        //let's fill in the new matrix
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                stringIsland[i][j] = "  LEV" + field[i][j].getTower().getTopComponent().getComponentCode() + " ";
                Worker occupant = location.getOccupant(i, j);
                if (occupant != null) {
                    //case cell is with worker
                    String owner = location.getOccupant(i, j).getPlayerName();
                    String trimOwner = owner.substring(0, Math.min(owner.length(), 3)); // trim to 3 chars the name of player
                    String workerCol = location.getOccupant(i,j).getColor().getAnsiCode(); //get color of worker
                    stringIsland[i][j] = Colors.reset() + workerCol + stringIsland[i][j] + trimOwner + Colors.reset() + "  ";
                } else {
                    //case cell WITHOUT worker
                    stringIsland[i][j] = "  " + stringIsland[i][j] + "   ";
                }
            }
        }
        utils.printMap(stringIsland);
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



    /**
     * This private method is used to convert a list of Cells into a list of thei location as arrays of CoordX,CoordY
     * @param cellList
     * @return
     */
    private List<int[]> cellAdapter(List<Cell> cellList){
        List<int[]> coord = new ArrayList<>();
        if(cellList.size() != 0) {
            coord = cellList.stream().map(Cell::getCoordXY).collect(Collectors.toList());
        }
        return coord;
    }
}



