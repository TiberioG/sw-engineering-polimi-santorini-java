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
     * Getter of id of the card
     *
     * @return id of the card
     */
    public int getId() {
        return id;
    }

    /**
     * Setter of id of the card
     *
     * @param id id of the card
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter of the name of the card
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of name of the card
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of type of card
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Setter of type of the card
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter of card's description
     *
     * @return
     */
    public String getDescription() {
        return description;
    }


    /**
     * Setter of card's description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter of initial phase associated to this card
     *
     * @return initial phase
     */
    public Phase getInitialPhase() {
        return initialPhase;
    }

    /**
     * Setter of the initial phase associated to this card
     *
     * @param initialPhase initial phase to be associated to this card
     */
    public void setInitialPhase(Phase initialPhase) {
        this.initialPhase = initialPhase;
    }

    /**
     * Getter of {@link StrategySettings} instance associated to this card
     *
     * @return
     */
    public StrategySettings getStrategySettings() {
        return strategySettings;
    }

    /**
     * Setter of {@link StrategySettings} instance associated to this card
     *
     * @param strategySettings {@link StrategySettings} instance to be associated to this card
     */
    public void setStrategySettings(StrategySettings strategySettings) {
        this.strategySettings = strategySettings;
    }

}
