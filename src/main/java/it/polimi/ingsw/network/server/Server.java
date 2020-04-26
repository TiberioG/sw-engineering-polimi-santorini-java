package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.messages.LoginMessage;
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

  private static final int MIN_NUM_OF_PLAYERS = 2;
  private static final int MAX_NUM_OF_PLAYERS = 3;

  private VirtualView virtualView;

  // todo: eventualmente fare una mappa apposita per gestire più partite in contemporanea

  // non molto bello, ma comunque mi assicuro che username e UUID siano in corrispondenza 1:1
  private Map<String, String> usernameToUUIDMap = new HashMap<>(); // key: username, value: UUID
  private Map<String, String> UUIDtoUsernameMap = new HashMap<>(); // key: UUID, value: username
  private Map<String, Date> birthdateMap = new HashMap<>(); // key: username, value: birthDate

  private List<ClientHandler> connectedClients = new ArrayList<>(); // List where will be stored all the connected clients, logged in or not (used for heartbeat messages)
  private Map<String, ClientHandler> clientsMap = new HashMap<>(); // key: UUID, value: Client
  private List<String> lobby = new ArrayList<>(); // usernames
  private Map<String, Date> matchUsers = new HashMap<>(); // key: username, value: birthDate
  private int howManyPlayers = 0;

  private static Logger LOGGER = Logger.getLogger("server");

  public Server() {
    virtualView = new VirtualView(this);
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
        client.setSoTimeout(20*1000); // milliseconds
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

      case SET_CARDS_TO_GAME:
        virtualView.update(message);
        break;

      default:
        break;
    }
  }

  /**
   * Sends a message to one or all clients, depending on {@link Message#getUsername()} or {@link Message#getUUID()} values.
   * Priority is given to {@link Message#getUUID()}, if it is null then {@link Message#getUsername()} will be used.
   * If one of these values is set to "ALL", the message will be sent to all clients (still considering the priority)
   *
   * @param message message to be sent
   */
  public void sendToClient(Message message) {
    String username = message.getUsername() != null ? message.getUsername() : "";
    String UUID = message.getUUID() != null ? message.getUUID() : "";
    if(!UUID.equals("") || !username.equals("")) {
      if (UUID.equals("ALL") || username.equals("ALL")) {
        clientsMap.forEach((uu_id, client) -> client.sendMessage(message));
      } else {
        String UUIDforUser = !UUID.equals("") ? UUID : usernameToUUIDMap.get(username);
        if (UUIDforUser != null && clientsMap.containsKey(UUIDforUser)) {
          clientsMap.get(UUIDforUser).sendMessage(message);
        } else {
          LOGGER.log(Level.WARNING, "We are trying to send a message to a User that doesn't exist");
        }
      }
    } else {
      LOGGER.log(Level.SEVERE, "Probably we are trying to send a message without an UUID or an username set\n" + message.toString());
    }
  }

  /**
   * Sends a message to all clients except for those whose UUID is in the uuidExcept parameter
   * @param message message to be sent
   * @param uuidExcept {@link List} of UUIDs corresponding to the clients to whom not to send the message
   */
  public void sendToClientExcept(Message message, List<String> uuidExcept) {
    HashMap<String, ClientHandler> tmpClientsMap = new HashMap<String, ClientHandler>(clientsMap);
    try {
      tmpClientsMap.keySet().removeAll(uuidExcept);
      tmpClientsMap.forEach((UUID, client) -> client.sendMessage(message));
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "You passed a null list");
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
    }, 1000, 5*1000); // this must be lower than (half should be ok) the value used client side in setSoTimeout()
  }

  /**
   * Associates a client to a UUID
   *
   * @param UUID UUID of the player to be added
   * @param client {@link ClientHandler} corresponding to the UUID
   */
  protected void associateClient(String UUID, ClientHandler client) {
    clientsMap.put(UUID, client);
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
    if(username.isBlank()) {
      LOGGER.log(Level.INFO, "Trying to register an empty username");
      messageToSend = new Message(TypeOfMessage.LOGIN_FAILURE, "I'm sorry, this is not a valid username. Please try with a different username:");
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      clientsMap.remove(uuid);
    } else if(usernameAlreadyExists) {
      LOGGER.log(Level.INFO, "Username " + username + " already exists");
      messageToSend = new Message(TypeOfMessage.LOGIN_FAILURE, "I'm sorry, this username is already taken. Please try with a different username:");
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      clientsMap.remove(uuid);
    } else if(message.getNumOfPlayers() < MIN_NUM_OF_PLAYERS || message.getNumOfPlayers() > MAX_NUM_OF_PLAYERS) { // server side check
      LOGGER.log(Level.WARNING, "Username " + username + " tried to create a " + message.getNumOfPlayers() + "-player match. Not allowed!");
      messageToSend = new Message(TypeOfMessage.NUM_PLAYERS_FAILURE, "I'm sorry, this number of players is not allowed. It must be between " + MIN_NUM_OF_PLAYERS + " and " + MAX_NUM_OF_PLAYERS);
      messageToSend.setUUID(uuid);
      sendToClient(messageToSend);
      clientsMap.remove(uuid);
    } else {
      clientsMap.get(uuid).setLogged(true);
      addUser(uuid, username, message.getBirthDate());
      LOGGER.log(Level.INFO, "Player " + username + " has been added!");
      messageToSend = new Message(username, TypeOfMessage.LOGIN_SUCCESSFUL);
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
      messageToSend = new Message(TypeOfMessage.USER_JOINED, details);
      sendToClientExcept(messageToSend, Arrays.asList(usernameToUUIDMap.get(username)));

      LOGGER.log(Level.INFO, "Notifying the user about he has been added to a queue");
      details = "There was already a lobby created. You joined a " + howManyPlayers + "-player match.\n";
      details += howManyPlayers == lobby.size() ? "You were the last player required! Match starting soon..." : "Waiting for " + (howManyPlayers - lobby.size()) + " other(s) player(s)...";
      messageToSend = new Message(username, TypeOfMessage.ADDED_TO_QUEUE, details);
      sendToClient(messageToSend);

      if (lobby.size() == howManyPlayers) {
        LOGGER.log(Level.INFO, "The desired number of players has been reached. Starting the match!");
        // todo: controllare se nel frattempo qualcuno si è disconnesso
        messageToSend = new Message("ALL", TypeOfMessage.START_MATCH);
        sendToClient(messageToSend);
        matchUsers.clear();
        List<String> copyLobby = new ArrayList<>(lobby);
        copyLobby.forEach( _username -> matchUsers.put(_username, birthdateMap.get(_username)));
        virtualView.initGame(matchUsers);
      }
    }
  }

  /**
   * Called when the server detects a disconnection
   * @param client disconnected client
   */
  protected void clientDisconnected(ClientHandler client) {
    if(client.isLogged()) { // user was logged, so in a lobby or in game
      String UUID = client.getUUID();
      ClientHandler removedClient = clientsMap.remove(UUID);
      String disconnectedUser = UUIDtoUsernameMap.get(UUID);
      connectedClients.remove(removedClient);
      removeUser(UUID);
      String details = "I'm sorry, " + disconnectedUser + " left the game.\nWe can't continue this match :(";
      disconnectAllPlayers(details);
    } else { // user was only connected to the server, but not logged
      connectedClients.remove(client);
    }
  }

  private void resetServer() {
    connectedClients.clear();
    clientsMap.clear();
    usernameToUUIDMap.clear();
    UUIDtoUsernameMap.clear();
    birthdateMap.clear();
    lobby.clear();
    matchUsers.clear();
    howManyPlayers = 0;
  }

  private synchronized void disconnectAllPlayers(String details) {
    disconnectPlayers(new ArrayList<>(clientsMap.keySet()), details);
    resetServer();
  }

  private void removeUser(String UUID) {
    String username = UUIDtoUsernameMap.get(UUID);
    usernameToUUIDMap.remove(username);
    birthdateMap.remove(username);
    UUIDtoUsernameMap.remove(UUID);
  }

  private void addUser(String UUID, String username, Date birthdate) {
    UUIDtoUsernameMap.put(UUID, username);
    usernameToUUIDMap.put(username, UUID);
    birthdateMap.put(username, birthdate);
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
        if (clientsMap.containsKey(UUID)) {
          ClientHandler clientToBeRemoved = clientsMap.remove(UUID);
          connectedClients.remove(clientToBeRemoved);
          clientToBeRemoved.closeConnection(details); // close connection
          removeUser(UUID);
        }
      });
    }
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
        if (clientsMap.containsKey(UUID)) {
          ClientHandler clientToBeRemoved = clientsMap.remove(UUID);
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
