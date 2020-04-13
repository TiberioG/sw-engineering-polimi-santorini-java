package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.*;
import it.polimi.ingsw.controller.CardManager;
import it.polimi.ingsw.model.Location;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * now this class does both virtualview and cli
 * it's both publicher ->controller and Listaner <- Model
 */
public class CLI extends Publisher<Message> implements Listener<Message> {
    /* ATTRIBUTES */

    private List<Publisher> subscribers = new ArrayList<>();
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private int loggedUsers = 0;

    Location location;



    /* METHODS*/

    /**
     * this class now just creates the match
     */
    public void startGame(){
        out.println("Welcome to Santorini");
        publish(new Message("ALL", TypeOfMessage.CREATE_MATCH, null)); //just send a message to controller to create the match
    }

    /**
     * method to read the players and add them to the match
     * @throws ParseException
     */
    public void userLogin() throws ParseException {
        String username ;
        Date date ;
        out.println("How many players?");
        int askedPlayers = Integer.parseInt(in.nextLine());

        while (loggedUsers < askedPlayers) {
            out.println("nome?");
            username = in.nextLine(); //todo chech se username non esiste già, idea, fare cache in ogni client ogni volata che viene creato un nuovo player, il server notifica tutti i client del nome nuovo
            out.println("birthdate format dd/MM/yyyy");
            date = dateFormat.parse(in.nextLine()) ;
            loggedUsers++;
           publish(new Message("ALL", TypeOfMessage.ADD_PLAYER, new Tupla(username, date))); //todo mettere qui chiamata a SERVERo client
        }

    }

    public void cardSelection()  {
        //get list of card names
        //todo invocare il controller qui
        cardMap = CardManager.getCardMap();

        //print all card names
        for(int i=0; i<cardMap.size(); i++) {
            out.println(cardMap.get(i).getName());
        }
    }





    public void showIsland(){

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
