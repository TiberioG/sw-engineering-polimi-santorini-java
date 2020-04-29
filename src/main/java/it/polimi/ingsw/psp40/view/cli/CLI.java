package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Publisher;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.ViewInterface;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;

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




    /* METHODS*/

    /**
     * this class now just creates the match
     */

    private void showTitle() {
        out.println("Welcome to Santorini");
    }

    @Override
    public void displaySetup() {
        showTitle();
        out.println("IP address of server?");
        String ip = utils.readIp();
        System.out.println("Port number?");
        int port = utils.validateIntInput(MIN_PORT, MAX_PORT);
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

        out.println("Choose your username:");
        String username = in.nextLine();
        date = utils.readDate("birthdate");

        out.println("How many people do you want to play with?");
        numOfPlayers = utils.validateIntInput( 2, 3);

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
        List<String> colorsAvailable =  Arrays.asList(Colors.allNames()); //list of NAMES of all colors available

        //remove colors already used by other players
        playerList.forEach(player -> {
            if (player.getWorkers().size() != 0){ //check if there is at least one player with workers
                colorsAvailable.remove(player.getWorkers().get(0).getColor().name());
            }
        });
        String[] colorsAvailableArry = (String[]) colorsAvailable.toArray(); //conversion to string
        out.println("I's time to choose one color for your workers, choose from following list:");
        try {
            utils.singleTableCool("options", colorsAvailableArry, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int choice = utils.readNumbers(0,colorsAvailableArry.length - 1);
        out.println("Wooow, you have selected color " + colorsAvailableArry[choice]+ " for your workers");
        client.sendToServer(new Message(TypeOfMessage.SET_WORKERS_COLOR, Colors.valueOf(colorsAvailableArry[choice])));

        /* section to position the workers */
        this.showIsland();

        int[] position2;
        int[] position1;

        out.println(String.format("Now you can position your worker no. %d", 1));
        do{
            position1 = utils.readPosition(0,4);
        }while (!occupiedCoord().contains(position1));

        out.println(String.format("Now you can position your worker no. %d", 2));
        do{
             position2 = utils.readPosition(0,4);
        }while (!Arrays.equals(position1, position2) && !occupiedCoord().contains(position1));

        Tupla doublecoord = new Tupla(new CoordinatesMessage(position1[0], position1[1]), new CoordinatesMessage(position2[0], position2[1]));

        client.sendToServer(new Message(TypeOfMessage.GENERIC_MESSAGE, doublecoord) );

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


    private void showIsland() {
      Location location = client.getLocationCache();
      Cell[][] field = client.getFieldCache();

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
        utils.printMap(stringIsland);
    }


    public void moveWorker() {


        out.println(String.format("Now you can position your worker no. %d", 2));
        int[] position1 = utils.readPosition(0,4);



    }


    public void buildBlock(){

        //client.send2Server(new Message(username, TypeOfMessage.BUILD_BLOCK, coord) );
    }


    private List<int[]> occupiedCoord (){
        List<Cell> occupiedCells = client.getLocationCache().getAllOccupied();
        List<int[]> occupiedCoord = new ArrayList<>();
        if(occupiedCells.size() != 0) {
            for (Cell occupiedCell : occupiedCells) {
                occupiedCoord.add(new int[]{occupiedCell.getCoordX(), occupiedCell.getCoordY()});
            }
        }
        return occupiedCoord;
    }
}



