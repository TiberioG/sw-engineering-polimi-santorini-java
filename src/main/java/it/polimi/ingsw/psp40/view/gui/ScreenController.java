package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;

public abstract class ScreenController {
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
