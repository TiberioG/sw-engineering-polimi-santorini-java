package it.polimi.ingsw.commons;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.text.DateFormat;

/**
 * This is an adapter class for {@link JsonReader} class and {@link Gson} class
 * @author Vito96
 */
public class JsonAdapter {
    /* Attributes */
    private JsonReader jsonReader;
    private JsonObject mainJsonObject;
    private JsonArray mainJsonArray;

    /* Constructor(s) */

    /**
     * Constructor: init jsonReader class
     * @param pathOfFile the relative path of the json file
     */
    public JsonAdapter(String pathOfFile) {
        jsonReader = new JsonReader(new InputStreamReader(getClass().getResourceAsStream(pathOfFile)));
    }

    /**
     * Constructor: init jsonReader class and init the first structure of the json file
     * @param pathOfFile the relative path of the json file
     * @param typeOfFirstNode the type of the first structure of the json file
     */
    public JsonAdapter(String pathOfFile, String typeOfFirstNode) {
        this(pathOfFile);
        if (typeOfFirstNode.equals("array")) {
            mainJsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
        } else if (typeOfFirstNode .equals("object")) {
            mainJsonObject = new Gson().fromJson(jsonReader, JsonObject.class);
        }
    }

    /* Methods */

    /**
     * This method return the mainJsonObject
     * @return the mainJsonObject of json file
     */
    public JsonObject getMainJsonObject() {
        return mainJsonObject;
    }

    /**
     * This method return the mainJsonArray
     * @return the mainJsonArray of json file
     */
    public JsonArray getMainJsonArray() {
        return mainJsonArray;
    }

    /**
     * return from the {@link JsonObject} the string value of a specified key or return a defaultValue if the key is not present
     * @param jsonObject
     * @param keyValue the key of the desired value
     * @param defaultValue the return value if the key is not present
     * @return the value of the key
     */
    public static String getStringFromJsonObject(JsonObject jsonObject, String keyValue, String defaultValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsString();
        } else return defaultValue;
    };

    /**
     * return from the {@link JsonObject} the string value of a specified key
     * @param jsonObject
     * @param keyValue the key of the desired value
     * @return the value of the key
     */
    public static String getStringFromJsonObject(JsonObject jsonObject, String keyValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsString();
        } else return null;
    };

    /**
     * return from the {@link JsonObject} the int value of a specified key or return a defaultValue if the key is not present
     * @param jsonObject
     * @param keyValue the key of the desired value
     * @param defaultValue the return value if the key is not present
     * @return the value of the key
     */
    public static int getIntFromJsonObject(JsonObject jsonObject, String keyValue, int defaultValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsInt();
        } else return defaultValue;
    };

    /**
     * return from the {@link JsonObject} the int value of a specified key
     * @param jsonObject
     * @param keyValue the key of the desired value
     * @return the value of the key
     */
    public static int getIntFromJsonObject(JsonObject jsonObject, String keyValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsInt();
        } else return 0;
    };


    public static boolean getBooleanFromJson(JsonObject jsonObject, String keyValue) {
        JsonElement jsonElement = jsonObject.get(keyValue);
        if (jsonElement != null) {
            return jsonElement.getAsBoolean();
        } else return false;
    };

    public static String toJsonClass(Object object) {
        Gson gson = new GsonBuilder().setDateFormat(Configuration.formatDate).serializeNulls().create();
        return gson.toJson(object);
    }


}