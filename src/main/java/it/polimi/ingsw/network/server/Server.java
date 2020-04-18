package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.Tupla;
import it.polimi.ingsw.commons.messages.TypeOfMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;


public class Server
{
  private static final int MIN_PORT = 1000;
  private static final int MAX_PORT = 50000;

  private VirtualView virtualView;

  private Map<String, ClientHandler> clientsMap = new HashMap<>();


  //todo creare lobby locale
  //todo ricordarsi check se ho player con nomi uguali

  public Server() {
    virtualView = new VirtualView(this);
  }


  public static void main(String[] args)
  {
    Scanner stdin = new Scanner(System.in);
    System.out.println("Port number?");
    int SOCKET_PORT = validateIntInput(stdin, MIN_PORT, MAX_PORT);
    ServerSocket socket;
    try {
      socket = new ServerSocket(SOCKET_PORT);
      System.out.println("Waiting for connections...");
    } catch (IOException e) {
      System.out.println("Cannot open server socket");
      System.exit(1);
      return;
    }

    Server server = new Server();
    while (true) {
      try {
        /* accepts connections; for every connection we accept,
         * create a new Thread executing a ClientHandler */
        Socket client = socket.accept();
        ClientHandler clientHandler = new ClientHandler(client, server);
        Thread thread = new Thread(clientHandler, "server_" + client.getInetAddress());
        thread.start();
      } catch (IOException e) {
        System.out.println("Connection dropped");
      }
    }
  }

  /**
   * Manages the insertion of an integer on command line input,
   * asking it again until it not a valid value.
   *
   * @param stdin          is the input scanner
   * @param minValue       is the minimum acceptable value of the input
   * @param maxValue       is the maximum acceptable value of the input
   * @return the value of the input
   */
  private static int validateIntInput(Scanner stdin, int minValue, int maxValue) {
    int output;
    try {
      output = stdin.nextInt();
    } catch (InputMismatchException e) {
      output = minValue - 1;
      stdin.nextLine();
    }
    while (output > maxValue || output < minValue) {
      System.out.println("Value must be between " + minValue + " and " + maxValue + ". Please, try again:");
      try {
        output = stdin.nextInt();
      } catch (InputMismatchException e) {
        output = minValue - 1;
        stdin.nextLine();
      }
    }
    return output;
  }

  public void receivedMessage(Message message) {
    switch (message.getTypeOfMessage()) {
      case ADD_PLAYER:
        // todo controllo sull'username
        System.out.println("Player " + message.getUsername() + " aggiunto!");
        sendToClient(new Message(message.getUsername(), TypeOfMessage.LOGIN_SUCCESSFUL, "Aggiunto"));
    }
  }

  public void sendToClient(Message message) {
    if(message.getUsername().equals("ALL")) {
       clientsMap.forEach((user, client) -> client.sendMessage(message));
    } else {
      // todo: sostituire username con uuid univoco. Vedi todo dentro a ClientHandler.java
      clientsMap.get(message.getUsername()).sendMessage(message);
    }
  }

  public void addClient(String username, ClientHandler client) {
    clientsMap.put(username, client);
  }

  /**
   * Checks if the user's client is already associated
   * @param username username t
   * @return true if an association exists
   */
  public boolean clientAssociationExists(String username) {
    return clientsMap.containsKey(username);
  }
}
