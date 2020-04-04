package it.polimi.ingsw.controller;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.JsonAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CardManager {
    private static CardManager instance = null;
    private static HashMap<Integer, Card> cardMap = new HashMap<>();

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

    public static CardManager initCardManager() {
        if (instance == null) instance = new CardManager();
        return instance;
    }

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

    public Card getCardById(int id){
        return cardMap.get(id);
    }
}
