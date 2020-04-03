package it.polimi.ingsw.commons;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;

public class JsonAdapter {
    private JsonReader jsonReader;
    private JsonObject mainJsonObject;
    private JsonArray mainJsonArray;

    public JsonAdapter(String pathOfFile) {
        jsonReader = new JsonReader(new InputStreamReader(getClass().getResourceAsStream(pathOfFile)));
    }

    public JsonAdapter(String pathOfFile, String typeOfFirstNode) {
        this(pathOfFile);
        if (typeOfFirstNode.equals("array")) {
            mainJsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
        } else if (typeOfFirstNode .equals("object")) {
            mainJsonObject = new Gson().fromJson(jsonReader, JsonObject.class);
        }
    }

    public JsonObject getMainJsonObject() {
        return mainJsonObject;
    }

    public JsonArray getMainJsonArray() {
        return mainJsonArray;
    }

    public static String getStringFromJsonObject(JsonObject jsonObject, String keyValue, String defaultValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.toString();
        } else return defaultValue;
    };

    public static String getStringFromJsonObject(JsonObject jsonObject, String keyValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.toString();
        } else return null;
    };

    public static int getIntFromJsonObject(JsonObject jsonObject, String keyValue, int defaultValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsInt();
        } else return defaultValue;
    };

    public static int getIntFromJsonObject(JsonObject jsonObject, String keyValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsInt();
        } else return 0;
    };
}
