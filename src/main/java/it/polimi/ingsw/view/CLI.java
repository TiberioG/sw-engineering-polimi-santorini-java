package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.Subscriber;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI implements Publisher {
    /* ATTRIBUTES */

    private List<Subscriber> subscribers = new ArrayList<>();
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);



    /* METHODS*/
    public void startGame(){
        out.println("Welcome to Santorini");


    }

    public void userLogin(){
        out.println("Quanti  siete?");



    }


    public void startMatch(){



    }

    public void showIsland(){

    }




    @Override
    public void addSubscriber(Subscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(Subscriber subscriber) {
        this.subscribers.remove(subscriber) ;

    }

    @Override
    public void notify(Subscriber subscriber) {


    }
}
