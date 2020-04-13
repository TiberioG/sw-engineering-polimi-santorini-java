package it.polimi.ingsw.network.client;

public interface ServerObserver
{
  void didReceiveConvertedString(String oldStr, String newStr);
}
