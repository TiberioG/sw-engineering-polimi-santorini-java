package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Locale;

/**
 * l'idea è che con questa strategy io passo una lista di componenti e di celle dove voglio costruire con un dato worker
 * è importante specificare che si costruisce il componente i-esimo della lista di componenti nella Cella con la stessa i-esmia posizione nella lista
 * nei casi delle carte semplici si può costruire comunque o una o due volte, mai di più
 *
 * a livello di turno e di vista mi immagino che si controlli se il player possiede una strategia che gli permette di costruire due volte e di conseguenza la view dà sta possibilità e costruisce la lista di cose da
 */
public interface StrategyBuild {
    void build(ArrayList<Component> listCompToBuild, ArrayList<Cell> listWhereToBuild, Match match, Worker worker);
}
