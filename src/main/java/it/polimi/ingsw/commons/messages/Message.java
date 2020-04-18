package it.polimi.ingsw.commons.messages;

import com.google.gson.Gson;
import it.polimi.ingsw.commons.JsonAdapter;

import java.io.Serializable;

public class Message implements Serializable {
    private String username;
    private TypeOfMessage typeOfMessage;
    private String jsonMessage;

    public Message(String username, TypeOfMessage typeOfMessage, Object object) {
        this.username = username;
        this.typeOfMessage = typeOfMessage;
        this.jsonMessage = JsonAdapter.toJsonClass(object);
        //System.out.println(this.jsonMessage + " prova json");
    }

    public String getUsername() {
        return username;
    }

    public TypeOfMessage getTypeOfMessage() {
        return typeOfMessage;
    }

    public Object getObjectFromJson(Class classType) {
        Gson gson = new Gson();
        return gson.fromJson(this.jsonMessage, classType);
    }
}
