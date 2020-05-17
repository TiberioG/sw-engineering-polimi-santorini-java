package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;

public abstract class ScreenController {
    private Client client;

    protected void setClient(Client client) {
        this.client = client;
    }

    protected Client getClient() {
        return client;
    }
}
