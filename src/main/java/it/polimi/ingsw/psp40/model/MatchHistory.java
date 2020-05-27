package it.polimi.ingsw.psp40.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.JsonAdapter;
import it.polimi.ingsw.psp40.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.io.*;
import java.util.Date;
import java.util.List;
public class MatchHistory {

    public synchronized static void saveMatch(Match match) {

        try (Writer writer = new FileWriter("backupOfMatches.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("matchId", match.getMatchID());
            jsonObject.addProperty("dateOfBuckup", new Date().toString());
            jsonObject.add("players", JsonParser.parseString(JsonAdapter.toJsonClass(match.getPlayers())));
            jsonObject.add("island", JsonParser.parseString(JsonAdapter.toJsonClass(match.getIsland())));
            jsonObject.add("matchProperties", JsonParser.parseString(JsonAdapter.toJsonClass(match.getMatchProperties())));
            jsonObject.add("currentPlayer", JsonParser.parseString(JsonAdapter.toJsonClass(match.getCurrentPlayer())));
            jsonObject.add("location", JsonParser.parseString(JsonAdapter.toJsonClass(match.getLocation())));
            jsonArray.add(jsonObject);
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject retrieveNewestMatchFromNames(List<String> names) {
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader("backupOfMatches.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonArray jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
        JsonObject jsonObjectOfOldMatch = null;
        for (JsonElement element : jsonArray) {
            Gson gson = new GsonBuilder().setDateFormat(Configuration.formatDate).serializeNulls().create();
            List<Player> playerList = gson.fromJson(element.getAsJsonObject().get("players"), new TypeToken<List<Player>>() {}.getType());
            //check for comparision name
            if (true && jsonObjectOfOldMatch == null) jsonObjectOfOldMatch = element.getAsJsonObject();
        }
        return jsonObjectOfOldMatch;
    }

    public static Match restoreMatch(VirtualView virtualView, JsonObject oldMatch) {
        int matchId = JsonAdapter.getIntFromJsonObject(oldMatch, "matchId");
        return new Match(matchId, virtualView);
    }

    public static void restorePlayers(Match match, JsonObject oldMatch) {
        List<Player> playerList = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("players"), new TypeToken<List<Player>>() {}.getType());
        playerList.forEach(oldPlayer -> {
            String playerName = oldPlayer.getName();
            match.createPlayer(playerName, oldPlayer.getBirthday());
            match.getPlayerByName(playerName).setCurrentCard(CardManager.initCardManager().getCardById( oldPlayer.getCurrentCard().getId()));
            Player newPlayer = match.getPlayerByName(playerName);
            newPlayer.addWorker(oldPlayer.getWorkers().get(0).getColor());
            newPlayer.addWorker(oldPlayer.getWorkers().get(0).getColor());
        });
    }

    public static void restoreIsland(Match match, JsonObject oldMatch) {
        Island island = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("island"), new TypeToken<Island>() {}.getType());
        for (int x = 0; x <= Island.getMaxX(); x++) {
            for (int y = 0; y <= Island.getMaxX(); y++) {
                try {
                    Component oldComponent = island.getCell(x,y).getTower().getTopComponent();
                    Cell newCell = match.getIsland().getCell(x,y);

                    for (int i = 1;  i <= oldComponent.getComponentCode(); i++) {
                        match.getIsland().addComponent(Component.getComponent(i), newCell);
                    }
                } catch (CellOutOfBoundsException | BuildLowerComponentException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static void restoreCurrentPlayer(Match match, JsonObject oldMatch) {
        Player oldCurrentPlayer = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("player"), new TypeToken<Player>() {}.getType());
        match.setCurrentPlayer(oldCurrentPlayer.getName());
    }


    public static void restoreMatchProperties(Match match, JsonObject oldMatch) {
        MatchProperties oldMatchProperties = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("matchProperties"), new TypeToken<Player>() {}.getType());
        MatchProperties newMatchProperties = match.getMatchProperties();

        newMatchProperties.setOthersCantLevelUp(oldMatchProperties.isOthersCantLevelUp());
    }



    public static void main( String[] args ) {
        MatchHistory.saveMatch(new Match(1));
    }
}
