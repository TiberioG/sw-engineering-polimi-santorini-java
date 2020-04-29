package it.polimi.ingsw.psp40.network.server;

import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.LoginMessage;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sup3rgiu
 */
public class Server
{
  private static final int MIN_PORT = 1000;
  private static final int MAX_PORT = 50000;

  private static final int MIN_NUM_OF_PLAYERS = 2;
  private static final int MAX_NUM_OF_PLAYERS = 3;

  // todo: eventualmente fare una mappa apposita per gestire più partite in contemporanea

  // non molto bello, ma comunque mi assicuro che username e UUID siano in corrispondenza 1:1
  private Map<String, String> usernameToUUIDMap = new HashMap<>(); // key: username, value: UUID
  private Map<String, String> UUIDtoUsernameMap = new HashMap<>(); // key: UUID, value: username
  private Map<String, Date> birthdateMap = new HashMap<>(); // key: username, value: birthDate

  private List<ClientHandler> connectedClients = new ArrayList<>(); // List where will be stored all the connected clients, logged in or not (used for heartbeat messages)
  private Map<String, ClientHandler> UUIDtoClientMap = new HashMap<>(); // key: UUID, value: Client
  private Map<String, Integer> UUIDtoMatchMap = new HashMap<>(); // key: UUID, value: matchID
  private Map<Integer, List<String>> matchToUUIDsMap = new HashMap<>(); // key: matchID, value: UUID list
  private Map<Integer, VirtualView> matchToVirtualViewMap = new HashMap<>(); // key: matchID, value: VirtualView
  private List<String> lobby = new ArrayList<>(); // usernames
  // private Map<String, Date> matchUsers = new HashMap<>(); // key: username, value: birthDate // todo remove
  private int howManyPlayers = 0;
  // private boolean gameStarted = false; //todo remove and handle the same thing using lobby

  private static Logger LOGGER = Logger.getLogger("server");

  public Server() {
    startHeartbeat();
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
        client.setSoTimeout(Configuration.serverTimeout *1000); // milliseconds
        ClientHandler clientHandler = new ClientHandler(client, server);
        Thread thread = new Thread(clientHandler, "server_" + client.getInetAddress());
        thread.start();
      } catch (IOException e) {
        System.out.println(e.getClass().getCanonicalName());
        System.out.println("Connection dropped");
      }
    }
  }

  /**
   * Called when the server receive a message from a client
   *
   * @param message message received
   */
  public void receivedMessage(Message message) {
    switch (message.getTypeOfMessage()) {
      case LOGIN:
        handleLogin((LoginMessage)message);
        break;

      case HEARTBEAT:
        break;

      default:
        try {
          int matchID = UUIDtoMatchMap.get(message.getUUID());
          VirtualView view = matchToVirtualViewMap.get(matchID);
          view.handleMessage(message);
        } catch (NullPointerException e) {
        }
        break;
    }
  }

  /**
   * Sends a message to one client or to all clients in a match, depending on {@link Message#getUsername()} or {@link Message#getUUID()} values.
   * Priority is given to {@link Message#getUUID()}, if it is null then {@link Message#getUsername()} will be used.
   * If one of these values is set to "ALL" (still considering the priority), the message will be sent to all the clients in the match specified by {@link Message#getMatchID()}
   *
   * @param message message to be sent
   */
  public void sendToClient(Message message) {
    String username = message.getUsername() != null ? message.getUsername() : "";
    String UUID = message.getUUID() != null ? message.getUUID() : "";
    int matchID = message.getMatchID();
    if(!UUID.equals("") || !username.equals("")) {
      if ((UUID.equals("ALL") || username.equals("ALL")) && matchID != 0) {
        matchToUUIDsMap.get(matchID).forEach( uuid -> {
          UUIDtoClientMap.get(uuid).sendMessage(message);
        });
      } else {
        String UUIDforUser = !UUID.equals("") ? UUID : usernameToUUIDMap.get(username);
        if (UUIDforUser != null && UUIDtoClientMap.containsKey(UUIDforUser)) {
          UUIDtoClientMap.get(UUIDforUser).sendMessage(message);
        } else {
          LOGGER.log(Level.WARNING, "We are trying to send a message to a User that doesn't exist");
        }
      }
    } else {
      LOGGER.log(Level.SEVERE, "Probably we are trying to send a message without an UUID or an username set\n" + message.toString());
    }
  }

  /**
   * Sends a message to all clients in a match except for those whose UUID is in the uuidExcept parameter
   * @param message message to be sent
   * @param matchID matchID corresponding to the desired clients
   * @param uuidExcept {@link List} of UUIDs corresponding to the clients to whom not to send the message
   */
  public void sendToClientExcept(Message message, Integer matchID, List<String> uuidExcept) {
    HashMap<String, ClientHandler> tmpClientsMap = new HashMap<>(UUIDtoClientMap);
    try {
      List<String> tmpUUIDinMatchMap = new ArrayList<>(matchToUUIDsMap.get(matchID));
      tmpUUIDinMatchMap.removeAll(uuidExcept);
      tmpUUIDinMatchMap.forEach( uuid -> {
        tmpClientsMap.get(uuid).sendMessage(message);
      });
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "You passed a null list or an invalid match ID");
    }
  }

  // Used to notify connection to the clients
  private void startHeartbeat() {
    Timer timer = new Timer();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Message msg = new Message(TypeOfMessage.HEARTBEAT);
        List<ClientHandler> copyConnectedClients = new ArrayList<>(connectedClients);
        copyConnectedClients.forEach( client -> {if(client != null) { client.sendMessage(msg); }});
      }
    }, 1000, Configuration.clientTimeout / 2 *1000); // this must be lower than (half should be ok) the value used client side in setSoTimeout()
  }

  /**
   * Associates a client to a UUID
   *
   * @param UUID UUID of the player to be added
   * @param client {@link ClientHandler} corresponding to the UUID
   */
  protected void associateClient(String UUID, ClientHandler client) {
    UUIDtoClientMap.put(UUID, client);
  }

  /**
   * Adds a client
   *
   * @param client {@link ClientHandler} to be added
   */
  protected void addClient(ClientHandler client) {
    connectedClients.add(client);
  }

  private synchronized void handleLogin(LoginMessage message) {
    String uuid = message.getUUID();
    String username = message.getUsername();
    boolean usernameAlreadyExists = usernameToUUIDMap.containsKey(username);
    Message messageToSend;
/*    if(this.gameStarted) {
      LOGGER.log(Level.INFO, message.getUsername() + "is trying to login while there is already a game in progress");
      ClientHandler clientToBeRemoved = UUIDtoClientMap.remove(uuid);
      connectedClients.remove(clientToBeRemoved);
      String details = "I'm sorry, there is already a game in progress. Try later :(";
      clientToBeRemoved.closeConnection(details);
    } else */if(username.isBlank()) {
      LOGGER.log(Level.INFO, "Trying to register an empty username");
      messageToSend = new Message(TypeOfMessage.LOGIN_FAILURE, "I'm sorry, this is not a valid username. Please try with a different username:");
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      UUIDtoClientMap.remove(uuid);
    } else if(usernameAlreadyExists) {
      LOGGER.log(Level.INFO, "Username " + username + " already exists");
      messageToSend = new Message(TypeOfMessage.LOGIN_FAILURE, "I'm sorry, this username is already taken. Please try with a different username:");
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      UUIDtoClientMap.remove(uuid);
    } else if(message.getNumOfPlayers() < MIN_NUM_OF_PLAYERS || message.getNumOfPlayers() > MAX_NUM_OF_PLAYERS) { // server side check
      LOGGER.log(Level.WARNING, "Username " + username + " tried to create a " + message.getNumOfPlayers() + "-player match. Not allowed!");
      messageToSend = new Message(TypeOfMessage.NUM_PLAYERS_FAILURE, "I'm sorry, this number of players is not allowed. It must be between " + MIN_NUM_OF_PLAYERS + " and " + MAX_NUM_OF_PLAYERS);
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      UUIDtoClientMap.remove(uuid);
    } else {
      UUIDtoClientMap.get(uuid).setLogged(true);
      addUser(uuid, username, message.getBirthDate());
      LOGGER.log(Level.INFO, "Player " + username + " has been added!");
      messageToSend = new Message(TypeOfMessage.LOGIN_SUCCESSFUL);
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      handleLobby(message);
    }
  }

  private synchronized void handleLobby(LoginMessage message) {
    String username = message.getUsername();
    lobby.add(username);
    Message messageToSend;
    String details;
    if(howManyPlayers == 0) {
      howManyPlayers = message.getNumOfPlayers();
      LOGGER.log(Level.INFO, "Notifying the first user that the lobby has been created and is waiting for new players...");
      details = "Lobby created! Waiting for " + (howManyPlayers-1) + " other(s) player(s)...";
      messageToSend = new Message(username, TypeOfMessage.GENERIC_MESSAGE, details);
      sendToClient(messageToSend);
    } else {
      LOGGER.log(Level.INFO, "Notifying other users about new user joined to the queue");
      details = username + " joined!\n";
      details += howManyPlayers == lobby.size() ? "Match starting soon..." : "Waiting for " + (howManyPlayers - lobby.size()) + " other(s) player(s)...";

      List<String> tmpLobby = new ArrayList<>(lobby);
      tmpLobby.remove(username);
      String finalDetails = details; // damn lambda
      tmpLobby.forEach(_username -> {
        sendToClient(new Message(_username, TypeOfMessage.USER_JOINED, finalDetails));
      });

      LOGGER.log(Level.INFO, "Notifying the user about he has been added to a queue");
      details = "There was already a lobby created. You joined a " + howManyPlayers + "-player match.\n";
      details += howManyPlayers == lobby.size() ? "You were the last player required! Match starting soon..." : "Waiting for " + (howManyPlayers - lobby.size()) + " other(s) player(s)...";
      messageToSend = new Message(username, TypeOfMessage.ADDED_TO_QUEUE, details);
      sendToClient(messageToSend);

      if (lobby.size() == howManyPlayers) {
        startMatch();
      }
    }
  }

  // todo: controllare se nel frattempo qualcuno si è disconnesso
  private void startMatch() {
    // create VirtualView for the current match
    int matchID = new Date().hashCode();
    VirtualView virtualView = new VirtualView(this);
    matchToVirtualViewMap.put(matchID, virtualView);

    // populate hashmaps
    Map<String, Date> matchUsers = new HashMap<>();
    List<String> UUIDinMatch = new ArrayList<>();
    List<String> copyLobby = new ArrayList<>(lobby);
    copyLobby.forEach( username -> {
      matchUsers.put(username, birthdateMap.get(username));
      UUIDtoMatchMap.put(usernameToUUIDMap.get(username), matchID);
      UUIDinMatch.add(usernameToUUIDMap.get(username));
    });
    matchToUUIDsMap.put(matchID, UUIDinMatch);

    // reset variables in order to accept new players for a simultaneous match
    howManyPlayers = 0;
    lobby.clear();

    // notify users
    LOGGER.log(Level.INFO, "The desired number of players has been reached. Starting the match with id: " + matchID);
    Message messageToSend = new Message("ALL", TypeOfMessage.START_MATCH);
    messageToSend.setMatchID(matchID);
    sendToClient(messageToSend);

    // create new match server side
    Message createMatchMessage = new Message(TypeOfMessage.START_MATCH, matchUsers);
    createMatchMessage.setMatchID(matchID); /* IMPORTANT */
    virtualView.handleMessage(createMatchMessage);
  }

  /**
   * Called when the server detects a disconnection
   * @param client disconnected client
   */
  protected void clientDisconnected(ClientHandler client) {
    if(client.isLogged()) { // user was logged, so in a lobby or in game
      String UUID = client.getUUID();
      Integer matchID = UUIDtoMatchMap.get(UUID);
      ClientHandler clientToBeRemoved = UUIDtoClientMap.get(UUID);
      connectedClients.remove(clientToBeRemoved);
      String disconnectedUser = UUIDtoUsernameMap.get(UUID);
      removeUser(UUID);
      LOGGER.log(Level.INFO, disconnectedUser + " left the game. Notifying other users...");
      String details = "I'm sorry, " + disconnectedUser + " left the game.\nWe can't continue this match :(";
      disconnectAllPlayers(matchID, details);
    } else { // user was only connected to the server, but not logged
      connectedClients.remove(client);
    }
  }

  private void resetServer() {
    connectedClients.clear();
    UUIDtoClientMap.clear();
    usernameToUUIDMap.clear();
    UUIDtoUsernameMap.clear();
    birthdateMap.clear();
    lobby.clear();
    UUIDtoMatchMap.clear();
    matchToUUIDsMap.clear();
    matchToVirtualViewMap.clear();
    howManyPlayers = 0;
  }

  /**
   * Disconnect all the players in the match corresponding to the matchID parameter.
   * If matchID is null, all players in the lobby will be disconnected
   *
   * @param matchID
   * @param details details about the disconnection. Will be sent to the clients
   */
  private synchronized void disconnectAllPlayers(Integer matchID, String details) {
    List<String> playersUUIDToDisconnect = new ArrayList<>();
    if(matchID == null) { // lobby
      List<String> tmpLobby = new ArrayList<>(lobby);
      tmpLobby.forEach( username -> playersUUIDToDisconnect.add(usernameToUUIDMap.get(username)));
    } else { // match
      if(matchToUUIDsMap.containsKey(matchID)) {
        playersUUIDToDisconnect.addAll(matchToUUIDsMap.get(matchID));
      }
    }
    disconnectPlayers(playersUUIDToDisconnect, details);
  }

  /**
   * Disconnects one or more players and notify those with a message
   *
   * @param playersUUID {@link List<String>} of UUID corresponding to the players to disconnect
   * @param details details to send to disconnected players (e.g.: reason about server-side disconnection)
   */
  private synchronized void disconnectPlayers(List<String> playersUUID, String details) {
    if(playersUUID != null) {
      playersUUID.forEach(UUID -> {
        if (UUIDtoClientMap.containsKey(UUID)) {
          ClientHandler clientToBeRemoved = UUIDtoClientMap.get(UUID);
          connectedClients.remove(clientToBeRemoved);
          clientToBeRemoved.closeConnection(details); // close connection
          removeUser(UUID);
        }
      });
    }
  }

  /**
   * Check if the match/lobby contains users and if not cleans/empties it
   *
   * @param matchID matchID corresponding to the match to be cleaned. 0 to clean the lobby
   * @return true if the match/lobby has been removed/emptied
   */
  private synchronized boolean cleanMatch(int matchID) {
    if(matchID != 0) {
      if(matchToUUIDsMap.containsKey(matchID)) {
        if (matchToUUIDsMap.get(matchID).size() == 0) {
          matchToUUIDsMap.remove(matchID);
          matchToVirtualViewMap.remove(matchID);
          return true;
        }
      }
    } else {
      if(lobby.size() == 0) {
        howManyPlayers = 0;
        return true;
      }
    }
    return false;
  }

  private synchronized void removeUser(String UUID) {
    String username = UUIDtoUsernameMap.get(UUID);
    usernameToUUIDMap.remove(username);
    birthdateMap.remove(username);
    UUIDtoUsernameMap.remove(UUID);

    UUIDtoClientMap.remove(UUID);
    if(UUIDtoMatchMap.containsKey(UUID)) {
      Integer matchID = UUIDtoMatchMap.get(UUID);
      matchToUUIDsMap.get(matchID).remove(UUID);
      UUIDtoMatchMap.remove(UUID);
      cleanMatch(matchID); // clean match
    } else {
      cleanMatch(0); // clean lobby
    }
  }

  private synchronized void addUser(String UUID, String username, Date birthdate) {
    UUIDtoUsernameMap.put(UUID, username);
    usernameToUUIDMap.put(username, UUID);
    birthdateMap.put(username, birthdate);
  }

  /**
   * Disconnects a player and notify him with a message
   *
   * @param username username of to the player to disconnect
   * @param details details to send to disconnected player (e.g.: player has lost)
   */
  public synchronized void disconnectPlayer(String username, String details) {
    if(username != null) {
      String UUID = usernameToUUIDMap.get(username);
      if(UUID != null) {
        if (UUIDtoClientMap.containsKey(UUID)) {
          ClientHandler clientToBeRemoved = UUIDtoClientMap.get(UUID);
          connectedClients.remove(clientToBeRemoved);
          clientToBeRemoved.closeConnection(details); // close connection
          removeUser(UUID);
        }
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
