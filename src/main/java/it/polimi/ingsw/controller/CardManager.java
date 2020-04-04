package it.polimi.ingsw.controller;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.JsonAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CardManager {
    private HashMap<Integer, Card> cardMap = new HashMap<>();

    public CardManager() {
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

                //load strategy settings, controllare valori null

                newCard.setInitialPhase(addInitialPhase(jsonObject));

                cardMap.put(newCard.getId(), newCard);
            }
            /*Gson gson = new Gson();
            cardList = gson.fromJson(jsonReader, new TypeToken<List<Card>>(){}.getType());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
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