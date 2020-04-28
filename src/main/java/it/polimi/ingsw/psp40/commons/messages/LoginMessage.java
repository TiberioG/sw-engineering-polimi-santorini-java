package it.polimi.ingsw.psp40.commons.messages;

import java.util.Date;

public class LoginMessage extends Message {

    private final Date birthDate;
    private final int numOfPlayers;

    public LoginMessage(String username, Date birthDate, int numOfPlayers, TypeOfMessage typeOfMessage) {
        super(username, typeOfMessage);
        this.birthDate = birthDate;
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public Date getBirthDate() {
        return birthDate;
    }
}
