package it.polimi.ingsw.commons;

import com.google.gson.Gson;

import java.io.Serializable;

public class Message implements Serializable {
    private String creator;
    private TypeOfMessage typeOfMessage;
    private String jsonMessage;

    public Message(String creator, TypeOfMessage typeOfMessage, Object object) {
        this.creator = creator;
        this.typeOfMessage = typeOfMessage;
        this.jsonMessage = JsonAdapter.toJsonClass(object);
        System.out.println(this.jsonMessage + " prova json");
    }

    public TypeOfMessage getTypeOfMessage() {
        return typeOfMessage;
    }

    public Object getObjectFromJson(Class classType) {
        Gson gson = new Gson();
        return gson.fromJson(this.jsonMessage, classType);
    }
}
