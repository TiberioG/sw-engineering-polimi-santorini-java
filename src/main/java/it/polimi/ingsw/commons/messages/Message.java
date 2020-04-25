package it.polimi.ingsw.commons.messages;

import com.google.gson.Gson;
import it.polimi.ingsw.commons.JsonAdapter;

import java.io.Serializable;

public class Message implements Serializable {
    private String username = null;
    private TypeOfMessage typeOfMessage;
    private String jsonMessage = null;
    private String UUID = null;

    public Message(String username, TypeOfMessage typeOfMessage, Object object) {
        this(username, typeOfMessage);
        this.jsonMessage = JsonAdapter.toJsonClass(object);
        //System.out.println(this.jsonMessage + " prova json");
    }

    public Message(String username, TypeOfMessage typeOfMessage) {
        this.username = username;
        this.typeOfMessage = typeOfMessage;
    }

    //todo quanti bei costruttori
    public Message(TypeOfMessage typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public Message(TypeOfMessage typeOfMessage, Object object) {
        this.typeOfMessage = typeOfMessage;
        this.jsonMessage = JsonAdapter.toJsonClass(object);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public TypeOfMessage getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setPayload(Object object) {
        this.jsonMessage = JsonAdapter.toJsonClass(object);
    }

    public Object getPayload(Class classType) {
        Gson gson = new Gson();
        return gson.fromJson(this.jsonMessage, classType);
    }

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", typeOfMessage=" + typeOfMessage +
                ", jsonMessage='" + jsonMessage + '\'' +
                ", UUID='" + UUID + '\'' +
                '}';
    }
}