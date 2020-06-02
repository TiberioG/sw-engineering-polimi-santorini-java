package it.polimi.ingsw.psp40.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.JsonAdapter;
import it.polimi.ingsw.psp40.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MatchHistory {

    public synchronized static void saveMatch(Match match) {
        JsonArray jsonArray = null;
        try (JsonReader jsonReader =  new JsonReader(new FileReader("backupOfMatches.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while(iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    Integer oldMatchId = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("matchId"), new TypeToken<Integer>() {}.getType());
                    if (oldMatchId == match.getMatchID()) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {}

        if (jsonArray == null) jsonArray = new JsonArray();

        try (Writer writer = new FileWriter("backupOfMatches.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
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

    public synchronized static void deleteMatch(Integer matchId) {
        JsonArray jsonArray = null;
        try (JsonReader jsonReader =  new JsonReader(new FileReader("backupOfMatches.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while(iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    Integer oldMatchId = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("matchId"), new TypeToken<Integer>() {}.getType());
                    if (oldMatchId == matchId) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {}

        if (jsonArray == null) jsonArray = new JsonArray();

        try (Writer writer = new FileWriter("backupOfMatches.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static JsonObject retrieveMatchFromNames(List<String> names) {
        JsonObject jsonObjectOfOldMatch = null;
        try (JsonReader jsonReader =  new JsonReader(new FileReader("backupOfMatches.json"))) {
            JsonArray jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while(iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    List<Player> playerList = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("players"), new TypeToken<List<Player>>() {}.getType());

                    //check to compare names
                    if (playerList.stream().filter(player -> names.contains(player.getName())).collect(Collectors.toList()).size() == names.size())
                        jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
            }
        } catch (IOException e) {}

        return jsonObjectOfOldMatch;
    }

    public static Match restoreMatch(VirtualView virtualView, JsonObject oldMatch) {
        int matchId = JsonAdapter.getIntFromJsonObject(oldMatch, "matchId");
        return new Match(matchId, virtualView);
    }

    public static List<String> getPlayersFromBrokenMatch(JsonObject oldMatch) {
        List<Player> playerList = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("players"), new TypeToken<List<Player>>() {}.getType());
        return playerList.stream().map(Player::getName).collect(Collectors.toList());
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
                    List<Component> oldComponents = island.getCell(x,y).getTower().getComponents();
                    oldComponents.remove(Component.GROUND); // Ground is already present in the newCell
                    Cell newCell = match.getIsland().getCell(x,y);

                    for (Component component : oldComponents) {
                        match.getIsland().addComponent(component, newCell);
                    }

                } catch (CellOutOfBoundsException | BuildLowerComponentException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static void restoreCurrentPlayer(Match match, JsonObject oldMatch) {
        Player oldCurrentPlayer = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("currentPlayer"), new TypeToken<Player>() {}.getType());
        match.setCurrentPlayer(oldCurrentPlayer.getName());
    }


    public static void restoreMatchProperties(Match match, JsonObject oldMatch) {
        MatchProperties oldMatchProperties = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("matchProperties"), new TypeToken<MatchProperties>() {}.getType());
        MatchProperties newMatchProperties = match.getMatchProperties();

        newMatchProperties.setOthersCantLevelUp(oldMatchProperties.isOthersCantLevelUp());
    }

    public static void restoreLocation(Match match, JsonObject oldMatch) {
        Location oldLocation = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("location"), new TypeToken<Location>() {}.getType());
        List<Player> oldPlayerList = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("players"), new TypeToken<List<Player>>() {}.getType());

        List<Cell> oldOccupiedCellList = oldLocation.getAllOccupied();
        oldPlayerList.forEach(oldPlayer -> {
            oldPlayer.getWorkers().forEach(oldWorker -> {
                //TODO implementing equals on worker
                Worker newWorker = match.getPlayerByName(oldPlayer.getName()).getWorkers().stream().filter(worker -> worker.getId() == oldWorker.getId()).findFirst().orElse(null);
                Cell oldCell = oldOccupiedCellList.stream().filter(cell -> oldLocation.getOccupant(cell).getId() == newWorker.getId() && oldLocation.getOccupant(cell).getPlayerName().equals(newWorker.getPlayerName())).findFirst().orElse(null);

                if (newWorker != null && oldCell != null) {
                    try {
                        match.getLocation().setLocation(match.getIsland().getCell(oldCell.getCoordX(), oldCell.getCoordY()), newWorker);
                    } catch (WorkerAlreadyPresentException | CellOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

}
