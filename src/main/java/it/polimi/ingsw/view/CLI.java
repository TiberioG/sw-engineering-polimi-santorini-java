package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.Tupla;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.controller.Card;
import it.polimi.ingsw.model.Location;
import it.polimi.ingsw.network.client.Client;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * now this class does both virtualview and cli
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

    private List<Publisher> subscribers = new ArrayList<>(); //todo serve sta roba?
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private int loggedUsers = 0;

    Location location;



    /* METHODS*/

    /**
     * this class now just creates the match
     */

    public void startGame() {
        out.println("Welcome to Santorini");

    }

    /**
     * method to read the players and add them to the match
     * @throws ParseException
     */
    public void userLogin() throws ParseException {

        Date date;

        out.println("nome?");
        username = in.nextLine();
        out.println("birthdate format dd/MM/yyyy");
        date = dateFormat.parse(in.nextLine());
        loggedUsers++;

        //chiamo il client e mando messaggio

        client.send2Server(new Message(username, TypeOfMessage.ADD_PLAYER, new Tupla(username, date)));
    }

    /**
     * method used to show all card available
     */
    public void cardSelection(List<Card> cards) {
        //todo voglio solo le stringhe

        utils.singleTableCool("Cards Available", cards, 100);

    }


    public void showIsland() {

    }


    //TODO fixare perchè i messaggi ala cli arriveranno dal server, non dalla virtual view
    @Override
    public void update(Message message) {
        TypeOfMessage type = message.getTypeOfMessage();

        switch (type) {
            case LOCATION_UPDATED: //se la location è cambiata, modifico la cache locale in ogni client
                this.location = (Location) message.getObjectFromJson(Location.class);

        }
    }
}



