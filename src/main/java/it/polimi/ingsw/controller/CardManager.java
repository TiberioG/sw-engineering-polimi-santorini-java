package it.polimi.ingsw.controller;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.JsonAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a Singleton class for retrieve the card information
 * @author Vito96
 */
public class CardManager {
    /* Attributes */
    private static CardManager instance = null;
    private static HashMap<Integer, Card> cardMap = new HashMap<>();

    /* Constructor(s) */

    /**
     * Constructor: retrieve information of alla card from a json file and put all card in cardMap
     */
    private CardManager() {
        try {
            JsonAdapter jsonAdapter = new JsonAdapter("/Cards.json", "array");
            JsonArray jsonArray = jsonAdapter.getMainJsonArray();
            for(JsonElement element: jsonArray){
                JsonObject jsonObject = element.getAsJsonObject();
                Card newCard = new Card();
                newCard.setId(JsonAdapter.getIntFromJsonObject(jsonObject, "id"));
                newCard.setName(JsonAdapter.getStringFromJsonObject(jsonObject,"name"));
                newCard.setDescription(JsonAdapter.getStringFromJsonObject(jsonObject, "description"));
                newCard.setType(JsonAdapter.getStringFromJsonObject(jsonObject, "type"));

                newCard.setStrategySettings(addStrategySettings(jsonObject));

                newCard.setInitialPhase(addInitialPhase(jsonObject));

                cardMap.put(newCard.getId(), newCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Methods */

    /**
     * This method initialize and return the singleton instance or only return the already created instance
     * @return the instance of CardManager
     */
    public static CardManager initCardManager() {
        if (instance == null) instance = new CardManager();
        return instance;
    }

    /**
     * This method create @link StrategySetting object from a {@link JsonObject} instance
     * @param jsonObject the json with the information needed to create the {@link StrategySettings} object
     * @return the strategy setting created with the jsonObject
     */
    private StrategySettings addStrategySettings(JsonObject jsonObject) {
        JsonObject strategySettingsJsonObject = jsonObject.getAsJsonObject("strategySettings");
        StrategySettings strategySettings = new StrategySettings();
        if (strategySettingsJsonObject != null) {
            strategySettings.setStrategyMove(JsonAdapter.getStringFromJsonObject(strategySettingsJsonObject, "strategyMove", "DefaultMove"));
            strategySettings.setStrategyBuild(JsonAdapter.getStringFromJsonObject(strategySettingsJsonObject, "strategyBuild", "DefaultBuild"));
            strategySettings.setStrategyWin(JsonAdapter.getStringFromJsonObject(strategySettingsJsonObject, "strategyWin", "DefaultWin"));
        } else {
            strategySettings.setStrategyMove("DefaultMove");
            strategySettings.setStrategyBuild("DefaultBuild");
            strategySettings.setStrategyWin("DefaultWin");
        }
        return strategySettings;
    }

    /**
     * This method create @link Phase object from a @link JsonObject instance
     * @param jsonObject the json with the information needed to create the @link Phase object
     * @return the Phase created with the jsonObject
     */
    private Phase addInitialPhase(JsonObject jsonObject) {
        JsonObject permittedPhasesJsonObject = jsonObject.getAsJsonObject("permittedPhases");
        if (permittedPhasesJsonObject != null) {
            String type = JsonAdapter.getStringFromJsonObject(permittedPhasesJsonObject, "type");
            JsonArray nextPhases = permittedPhasesJsonObject.getAsJsonArray("nextPhases");
            List<Phase> nextPhasesTreeSet = null;
            if (nextPhases != null) nextPhasesTreeSet = buildTreeOfList(nextPhases);
            return new Phase(type, nextPhasesTreeSet);
        } else return null;
    }

    /**
     * This recursive method create a tree structure that cointains a {@link LinkedList} of phases
     * @param currentPhases {@link JsonArray} which cointains the currentPhases
     * @return list of phases
     */
    private List<Phase> buildTreeOfList(JsonArray currentPhases) {
        List<Phase> currentPhasesList = new LinkedList<>();
        for(JsonElement nextPhaseElement: currentPhases){
            JsonObject phaseObject = nextPhaseElement.getAsJsonObject();
            String type = JsonAdapter.getStringFromJsonObject(phaseObject, "type");
            JsonArray nextPhases = phaseObject.getAsJsonArray("nextPhases");
            List<Phase> nextPhasesList = null;
            if (nextPhases != null) nextPhasesList = buildTreeOfList(nextPhases);
            currentPhasesList.add(new Phase(type, nextPhasesList));
        }
        return  currentPhasesList;
    }

    /**
     * Method for retrieve card information
     * @param id the unique id of the card
     * @return the card object
     */
    public Card getCardById(int id){
        return cardMap.get(id);
    }

    /**
     * Method to get the hashmap of all the cards
     * @return the cardMap int, Card
     */
    public static HashMap<Integer, Card> getCardMap(){
        return cardMap;
    }


}
