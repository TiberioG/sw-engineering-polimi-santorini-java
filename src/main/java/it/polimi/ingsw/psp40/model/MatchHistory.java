package it.polimi.ingsw.psp40.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.psp40.commons.JsonAdapter;
import it.polimi.ingsw.psp40.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used for save match or restore an old match
 *
 * @author Vito96
 */
public class MatchHistory {

    /**
     * Method for create a backup of @{link Match}, if there is already a backup of the match it replaces it otherwise it creates it
     *
     * @param match the match to save
     */
    public synchronized static void saveMatch(Match match) {
        JsonArray jsonArray = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfMatches.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    Integer oldMatchId = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("matchId"), new TypeToken<Integer>() {
                    }.getType());
                    if (oldMatchId == match.getMatchID()) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {
        }

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


    /**
     * Method for delete the backup a match with a specific matchId
     *
     * @param matchId the matchId of the match to delete
     */
    public synchronized static void deleteMatch(Integer matchId) {
        JsonArray jsonArray = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfMatches.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    Integer oldMatchId = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("matchId"), new TypeToken<Integer>() {
                    }.getType());
                    if (oldMatchId.equals(matchId)) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {
        }

        if (jsonArray == null) jsonArray = new JsonArray();

        try (Writer writer = new FileWriter("backupOfMatches.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to retrieve the most recent match saved where the players of the match have specified names
     *
     * @param names a list of names to find in match
     */
    public synchronized static JsonObject retrieveMatchFromNames(List<String> names) {
        JsonObject jsonObjectOfOldMatch = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfMatches.json"))) {
            JsonArray jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    List<Player> playerList = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("players"), new TypeToken<List<Player>>() {
                    }.getType());

                    //check to compare names
                    if (playerList.stream().filter(player -> names.contains(player.getName())).collect(Collectors.toList()).size() == names.size())
                        jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
            }
        } catch (IOException e) {
        }

        return jsonObjectOfOldMatch;
    }

    /**
     * Method to create a new match from a JsonObject that contain an old match
     *
     * @param virtualView the {@link VirtualView} necessary for costructor of restored match
     * @param oldMatch    the {@link JsonObject} that contains the information of old match
     * @return the oldMatch restored
     */
    public static Match restoreMatch(VirtualView virtualView, JsonObject oldMatch) {
        int matchId = JsonAdapter.getIntFromJsonObject(oldMatch, "matchId");
        return new Match(matchId, virtualView);
    }

    /**
     * Method to retrieve the names of players from a {@link JsonObject} that contains the old match
     *
     * @param oldMatch the {@link JsonObject} that contains the information of old match
     * @return a list of players name of old match
     */
    public static List<String> getPlayersFromBrokenMatch(JsonObject oldMatch) {
        List<Player> playerList = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("players"), new TypeToken<List<Player>>() {
        }.getType());
        return playerList.stream().map(Player::getName).collect(Collectors.toList());
    }


    /**
     * Method to retrieve the players from a {@link JsonObject} that contains the old match and for restore it in a existing match
     *
     * @param match    the match where to recreate the players recovered from the old match
     * @param oldMatch the {@link JsonObject} that contains the information of old match
     */
    public static void restorePlayers(Match match, JsonObject oldMatch) {
        List<Player> playerList = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("players"), new TypeToken<List<Player>>() {
        }.getType());
        playerList.forEach(oldPlayer -> {
            String playerName = oldPlayer.getName();
            match.createPlayer(playerName, oldPlayer.getBirthday());
            match.getPlayerByName(playerName).setCurrentCard(CardManager.initCardManager().getCardById(oldPlayer.getCurrentCard().getId()));
            Player newPlayer = match.getPlayerByName(playerName);
            newPlayer.addWorker(oldPlayer.getWorkers().get(0).getColor());
            newPlayer.addWorker(oldPlayer.getWorkers().get(0).getColor());
        });
    }

    /**
     * Method to retrieve the {@link Island} from a {@link JsonObject} that contains the old match and for restore it in a existing match
     *
     * @param match    the match where to recreate the {@link Island} recovered from the old match
     * @param oldMatch the {@link JsonObject} that contains the information of old match
     */
    public static void restoreIsland(Match match, JsonObject oldMatch) {
        Island island = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("island"), new TypeToken<Island>() {
        }.getType());
        for (int x = 0; x <= Island.getMaxX(); x++) {
            for (int y = 0; y <= Island.getMaxX(); y++) {
                try {
                    List<Component> oldComponents = island.getCell(x, y).getTower().getComponents();
                    oldComponents.remove(Component.GROUND); // Ground is already present in the newCell
                    Cell newCell = match.getIsland().getCell(x, y);

                    for (Component component : oldComponents) {
                        match.getIsland().addComponent(component, newCell);
                    }

                } catch (CellOutOfBoundsException | BuildLowerComponentException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Method to retrieve the current {@link Player} from a {@link JsonObject} that contains the old match and for restore it in a existing match
     *
     * @param match    the match where to recreate current {@link Player} recovered from the old match
     * @param oldMatch the {@link JsonObject} that contains the information of old match
     */
    public static void restoreCurrentPlayer(Match match, JsonObject oldMatch) {
        Player oldCurrentPlayer = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("currentPlayer"), new TypeToken<Player>() {
        }.getType());
        match.setCurrentPlayer(oldCurrentPlayer.getName());
    }

    /**
     * Method to retrieve the {@link MatchProperties} from a {@link JsonObject} that contains the old match and for restore it in a existing match
     *
     * @param match    the match where to recreate {@link MatchProperties} recovered from the old match
     * @param oldMatch the {@link JsonObject} that contains the information of old match
     */
    public static void restoreMatchProperties(Match match, JsonObject oldMatch) {
        MatchProperties oldMatchProperties = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("matchProperties"), new TypeToken<MatchProperties>() {
        }.getType());
        MatchProperties newMatchProperties = match.getMatchProperties();

        newMatchProperties.setOthersCantLevelUp(oldMatchProperties.getOthersCantLevelUp());
    }

    /**
     * Method to retrieve the {@link Location} from a {@link JsonObject} that contains the old match and for restore it in a existing match
     *
     * @param match    the match where to recreate {@link Location} recovered from the old match
     * @param oldMatch the {@link JsonObject} that contains the information of old match
     */
    public static void restoreLocation(Match match, JsonObject oldMatch) {
        Location oldLocation = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("location"), new TypeToken<Location>() {
        }.getType());
        List<Player> oldPlayerList = JsonAdapter.getGsonBuilder().fromJson(oldMatch.get("players"), new TypeToken<List<Player>>() {
        }.getType());

        List<Cell> oldOccupiedCellList = oldLocation.getAllOccupied();
        oldPlayerList.forEach(oldPlayer -> {
            oldPlayer.getWorkers().forEach(oldWorker -> {
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
