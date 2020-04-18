package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.messages.Message;

public interface ServerObserver
{
  void handleMessage(Message message);
}
