package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.*;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Location;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private Client client;
    private Date date = null;
    private int numOfPlayers = 0;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private List<Publisher> subscribers = new ArrayList<>(); //todo serve sta roba?
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private static final int MIN_PORT = 1000; // todo usare quelli del server. Possibile?
    private static final int MAX_PORT = 50000;

    private Utils utils = new Utils(in, out);

    Location location;


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
        String ip = readIp(in);

        System.out.println("Port number?");
        int port = validateIntInput(in, MIN_PORT, MAX_PORT);

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
     * @throws ParseException
     */
    @Override
    public void displayLogin() {

        out.println("Choose your username:");
        String username = in.nextLine();
        out.println("Birthdate format dd/MM/yyyy"); //todo ustils per parsare in modo fico
        try {
            date = dateFormat.parse(in.nextLine());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        out.println("How many people do you want to play with?");
        numOfPlayers = validateIntInput(in, 2, 3);
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
     * @param cards a list of  {@link Card>}
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

        //todo cannot be the same card
        //String[] selectedCards = IntStream.range(0, numPlayers).mapToObj(i -> names[utils.readNumbers(0, names.length - 1)]).toArray(String[]::new);
        List<Integer> selections = utils.readNotSameNumbers(0, names.length - 1, numPlayers );
        String[] strSelections = new String[numPlayers];

        for (int i =0 ; i< selections.size()  ; i++){
            strSelections[i] = names[selections.get(i)];
        }

        //client.send2Server(new Message(username, TypeOfMessage.CARDS_SET_GAME, strSelections));
    }

    @Override
    public void displaySetInitialPosition(List<CoordinatesMessage> coordinatesMessageList) {

    }


    public void setWorkerColor() throws InterruptedException {
        out.println("I's time to choose one color for your workers, choose from following list:");
        utils.singleTableCool("options", Colors.allNamesColored(), 100);
        int choice = utils.readNumbers(0,Colors.allNamesColored().length - 1 );

        out.println("Wooow, you have selected color " + Colors.allNamesColored()[choice]+ " for your workers");

        String colorName = Colors.allNames()[choice];
        //client.send2Server(new Message(username, TypeOfMessage.SET_WORKER_COLOR, colorName) ); //passo una stringa col nome del color, poichè i color non possono avere costruttore pubblico per settare il colore del worker bisognerà fare switch case

    }
    public void setWorkerPosition(int workNumb){
        out.println(String.format("Now you can position your worker no. %d", workNumb));

        int[] position = utils.readPosition(0,4);
        CoordinatesMessage coord = new CoordinatesMessage(position[0], position[1]);

        //client.send2Server(new Message(username, TypeOfMessage.SET_WORKER_POSITION, coord) );

    }

    public void updateIsland(Cell[][] field, Location location) {

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
        //utils.readPosition();



    }


    public void buildBlock(){

        //client.send2Server(new Message(username, TypeOfMessage.BUILD_BLOCK, coord) );
    }

    /*
    //TODO e che ci sta qua?
    @Override
    public void update(Message message) {
        TypeOfMessage type = message.getTypeOfMessage();

        switch (type) {
            case LOCATION_UPDATED: //se la location è cambiata, modifico la cache locale in ogni client
                this.location = (Location) message.getObjectFromJson(Location.class);

        }
    }*/

    private String readIp(Scanner stdin) {
        String ip;
        ip = stdin.nextLine();

        while (!isValidIp(ip)) {
            System.out.println("This is not a valid IPv4 address. Please, try again:");
            ip = stdin.nextLine();
        }
        return ip;
    }

    private static boolean isValidIp(String input) {
        return input.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$") || input.equals("localhost");
    }

    /**
     * Manages the insertion of an integer on command line input,
     * asking it again until it not a valid value.
     *
     * @param stdin          is the input scanner
     * @param minValue       is the minimum acceptable value of the input
     * @param maxValue       is the maximum acceptable value of the input
     * @return the value of the input
     */
    private static int validateIntInput(Scanner stdin, int minValue, int maxValue) {
        int output;
        try {
            output = stdin.nextInt();
        } catch (InputMismatchException e) {
            output = minValue - 1;
            stdin.nextLine();
        }
        while (output > maxValue || output < minValue) {
            System.out.println("Value must be between " + minValue + " and " + maxValue + ". Please, try again:");
            try {
                output = stdin.nextInt();
            } catch (InputMismatchException e) {
                output = minValue - 1;
                stdin.nextLine();
            }
        }

        stdin.nextLine(); // handle nextInt()
        return output;
    }
}



