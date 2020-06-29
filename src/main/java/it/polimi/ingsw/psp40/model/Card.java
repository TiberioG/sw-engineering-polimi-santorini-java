package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.controller.StrategySettings;

public class Card {
    private int id;
    private String name;
    private String type;
    private String description;
    private Phase initialPhase;
    private StrategySettings strategySettings;


    /**
     * getter of id of the card
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * setter of id of the card
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Geter of the name of the card
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * setter of name of the card
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter of type of card
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * setter of type of the card
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Phase getInitialPhase() {
        return initialPhase;
    }

    public void setInitialPhase(Phase initialPhase) {
        this.initialPhase = initialPhase;
    }

    public StrategySettings getStrategySettings() {
        return strategySettings;
    }

    public void setStrategySettings(StrategySettings strategySettings) {
        this.strategySettings = strategySettings;
    }

}
