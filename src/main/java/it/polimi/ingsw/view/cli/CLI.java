package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Location;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
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
    private Client client;
    private String username;
    private Date date;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private List<Publisher> subscribers = new ArrayList<>(); //todo serve sta roba?
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);


    private Utils utils = new Utils(in, out);

    Location location;



    /* METHODS*/

    /**
     * this class now just creates the match
     */

    public void startGame() {
        out.println("Welcome to Santorini");

    }

    /**
     * method to read from console the players and add them to the match
     * @throws ParseException
     */
    public void userLogin() throws ParseException {

        out.println("nome?");
        username = in.nextLine();
        out.println("Birthdate format dd/MM/yyyy"); //todo ustils per parsare in modo fico
        date = dateFormat.parse(in.nextLine());

        //client.send2Server(new Message(username, TypeOfMessage.ADD_PLAYER, new Tupla(username, date)));
    }


    /**
     * This method is used to display all the cards available to the user and make him choose a subset of them of cardinality equals to the number of players
     * @param cards a list of  {@link Card>}
     * @param numPlayers an int which is the number of player in ame which should equals to the number of selected cards
     * @throws InterruptedException
     */
    public void cardShowAndSelection(List<Card> cards, int numPlayers) throws InterruptedException {

        String[] names = cards.stream().map(Card::getName).toArray(String[]::new);
        utils.singleTableCool("Cards Available", names, 100);
        System.out.println("Select " + numPlayers + " cards");

        //todo cannot be the same card
        String[] selectedCards = IntStream.range(0, numPlayers).mapToObj(i -> names[utils.readNumbers(0, names.length - 1)]).toArray(String[]::new);

        //client.send2Server(new Message(username, TypeOfMessage.CARDS_SET_GAME, selectedCards));
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
}



