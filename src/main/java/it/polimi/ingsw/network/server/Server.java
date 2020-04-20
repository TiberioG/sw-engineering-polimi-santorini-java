package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.TypeOfMessage;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server
{
  private static final int MIN_PORT = 1000;
  private static final int MAX_PORT = 50000;

  private VirtualView virtualView;

  // todo: eventualmente fare una mappa apposita per gestire più partite in contemporanea
  private Map<String, String> usernameUUIDMap = new HashMap<>();
  private Map<String, ClientHandler> clientsMap = new HashMap<>();
  private List<String> lobby = new ArrayList<>();
  private List<String> matchUsers = new ArrayList<>();
  private int howManyPlayers = 0;

  private static Logger LOGGER = Logger.getLogger("server");

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

  public void receivedMessage(Message message) {
    switch (message.getTypeOfMessage()) {
      case LOGIN:
        handleLogin(message);
        break;

      case HOW_MANY_PLAYERS:
        howManyPlayers = (int)message.getPayload(Integer.class);
        sendToClient(new Message(message.getUUID(), TypeOfMessage.GENERIC_MESSAGE, "Waiting for " + (howManyPlayers-1) + " other(s) player(s)..."));
        break;
    }
  }

  /**
   * Sends a message to one or all clients, depending on the {@link Message#getUsername()} value. If this value equals to "ALL", then the message will be sent to all the clients
   * @param message message to be sent
   */
  public void sendToClient(Message message) {
    if(message.getUUID() != null) {
      if (message.getUUID().equals("ALL")) {
        clientsMap.forEach((UUID, client) -> client.sendMessage(message));
      } else {
        if (clientsMap.containsKey(message.getUUID())) {
          clientsMap.get(message.getUUID()).sendMessage(message);
        } else {
          LOGGER.log(Level.WARNING, "We are trying to send a message to a UUID that doesn't exist");
        }
      }
    } else {
      LOGGER.log(Level.SEVERE, "Probably we are trying to send a message without an UUID set\n" + message.toString());
    }
  }

  /**
   * Sends a message to all clients except for those whose UUID is in uuidExcept parameter
   * @param message message to be sent
   * @param uuidExcept {@link List} of UUIDs corresponding to the clients to whom not to send the message
   */
  public void sendToClient(Message message, List<String> uuidExcept) {
    HashMap<String, ClientHandler> tmpClientsMap = new HashMap<String, ClientHandler>(clientsMap);
    try {
      tmpClientsMap.keySet().removeAll(uuidExcept);
      tmpClientsMap.forEach((UUID, client) -> client.sendMessage(message));
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "You passed a null list");
    }

  }

  public void addClient(String UUID, ClientHandler client) {
    clientsMap.put(UUID, client);
  }

  private void handleLogin(Message message) {
    boolean usernameAlreadyExists = usernameUUIDMap.containsValue(message.getUsername());
    String uuid = message.getUUID();
    Message messageToSend;
    if(usernameAlreadyExists) {
      LOGGER.log(Level.INFO, "Username " + message.getUsername() + " already exists");
      messageToSend = new Message(TypeOfMessage.LOGIN_FAILURE, "I'm sorry, this username is already taken");
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);

      clientsMap.remove(uuid);
    } else {
      usernameUUIDMap.put(uuid, message.getUsername());
      LOGGER.log(Level.INFO, "Player " + message.getUsername() + " has been added!");
      messageToSend = new Message(TypeOfMessage.LOGIN_SUCCESSFUL);
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      handleLobby(uuid);
    }
  }

  //todo sistemare lobby nel caso di disconnessioni o giocatore fermo su "how many players" che non risponde
  private void handleLobby(String uuid) {
    lobby.add(uuid);
    Message messageToSend;
    if(lobby.size() == 1) {
      LOGGER.log(Level.INFO, "Asking to the first user with how many players play the match");
      messageToSend = new Message(TypeOfMessage.HOW_MANY_PLAYERS);
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
    } else {
      LOGGER.log(Level.INFO, "Notifying other users about new user joined to the queue");
      String details = usernameUUIDMap.get(uuid) + " joined!\n";
      details += howManyPlayers == lobby.size() ? "Match starting soon..." : "Waiting for " + (howManyPlayers-lobby.size()) + " other(s) player(s)...";
      messageToSend = new Message(TypeOfMessage.USER_JOINED, details);
      sendToClient(messageToSend, Arrays.asList(uuid));

      LOGGER.log(Level.INFO, "Notifying the user about he has been added to a queue");
      details = "You joined a " + howManyPlayers + "-player match.\n";
      details += howManyPlayers == lobby.size() ? "You were the last player required! Match starting soon..." : "Waiting for " + (howManyPlayers-lobby.size()) + " other(s) player(s)...";
      messageToSend = new Message(TypeOfMessage.ADDED_TO_QUEUE, details);
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);

      if(lobby.size() == howManyPlayers) {
        LOGGER.log(Level.INFO, "The desired number of players has been reached. Starting the match!");
        // todo: controllare se nel frattempo qualcuno si è disconnesso
        messageToSend = new Message(TypeOfMessage.START_MATCH);
        messageToSend.setUsername("ALL");
        sendToClient(messageToSend);
        matchUsers.clear();
        matchUsers.addAll(lobby);
        virtualView.initGame(matchUsers);
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

}
