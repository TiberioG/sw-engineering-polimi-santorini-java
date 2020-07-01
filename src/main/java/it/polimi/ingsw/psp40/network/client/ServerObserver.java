package it.polimi.ingsw.psp40.network.client;

import it.polimi.ingsw.psp40.commons.messages.Message;

/**
 * @author sup3rgiu
 */
public interface ServerObserver {
    void handleMessage(Message message);
}
