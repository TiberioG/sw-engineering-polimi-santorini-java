package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.Subscriber;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.view.CLI;

public class Controller implements Subscriber {

    private Match match;
    private CLI view; //TODO questa divernterà la virtualview!!!!
    private TurnManager turnManager;

  public Controller(CLI view){
      this.view = view;
  }




    @Override
    public void update(Publisher cli) { // quello che manda i messaggi è il publisher cioè la nostra virtualview che per ora è solo cli

    }
}
