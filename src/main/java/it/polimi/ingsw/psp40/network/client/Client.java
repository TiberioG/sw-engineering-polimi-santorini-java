package it.polimi.ingsw.psp40.network.client;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.network.server.Server;
import it.polimi.ingsw.psp40.view.ViewInterface;
import it.polimi.ingsw.psp40.view.cli.CLI;
import it.polimi.ingsw.psp40.view.gui.GUI;
import it.polimi.ingsw.psp40.view.cli.CoolCLI;
import javafx.application.Application;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Client implements ServerObserver {


  /* Attributes */

  public static final int MIN_PORT = Server.MIN_PORT;
  public static final int MAX_PORT = Server.MAX_PORT;

  private ViewInterface view;

  private String serverIP;

  private int serverPort;

  private String UUID = null;

  private String username = null;

  private Boolean waiting = false;

  ServerAdapter serverAdapter;

  private static final Logger LOGGER = Logger.getLogger("Client");

  private Cell[][] fieldCache;
  private Location locationCache = new Location();
  private List<Player> playerListCache;
  private List<Cell> availableMoveCells;
  private HashMap<Cell, List<Integer>> availableBuildCells;
  private List<Phase> listOfPhasesCache;



  /* Constructor(s) */

  public Client() {}

  /* Methods */


  public static void main( String[] args )
  {
    boolean cli = false;
    boolean basicli = false;

    if (args.length > 0) {

      switch (args[0]) {

        case "cli":
          cli = true;
          break;
        case "basicli":
          basicli = true;
          break;
        case "gui":
          break;
        default:
          LOGGER.log(Level.WARNING, "After the name of the program write 'cli' if you want to use the console, 'gui' if you want to use the gui interface");
          System.exit(0);
          break;
      }
    }

    if (cli) {
      Client client = new Client();
      //CLI view = new CLI(client);
      CoolCLI view = new CoolCLI(client);
      client.setView(view);
      view.displaySetup(); // ask for server IP and Port
    }

    else if (basicli) {
      Client client = new Client();
      CLI view = new CLI(client);
      client.setView(view);
      view.displaySetup(); // ask for server IP and Port
    }

    else {
      Application.launch(GUI.class, args);
    }

  }

  /**
   * Instantiates a connection with the server
   */
  public void connectToServer() {
    /* open a connection to the server */
    Socket server;
    try {
      server = new Socket(getServerIP(), getServerPort());
      server.setSoTimeout(Configuration.clientTimeout *1000); // milliseconds

      /* Create the adapter that will allow communication with the server
       * in background, and start running its thread */
      serverAdapter = new ServerAdapter(server);
      serverAdapter.addObserver(this);
      Thread serverAdapterThread = new Thread(serverAdapter);
      serverAdapterThread.start();
      startHeartbeat();
      view.displayLogin();
    } catch (IOException e) {
        view.displaySetupFailure();
    }
  }

  public void close() {
    serverAdapter.stop();
    System.exit(0);
  }

  /**
   * Performs actions depeneding on the message type
   * @param message message received from the server
   */
  public void handleMessage(Message message){
    switch (message.getTypeOfMessage()) {
      case GENERIC_MESSAGE:
        view.displayGenericMessage((String)message.getPayload(String.class));
        break;

      case LOBBY_CREATED:
        view.displayLobbyCreated((String)message.getPayload(String.class));
        break;

      case LOGIN_SUCCESSFUL:
        setUUID(message.getUUID());
        view.displayLoginSuccessful();
        break;

      case LOGIN_FAILURE:
        view.displayLoginFailure((String)message.getPayload(String.class));
        break;

      case USER_JOINED:
        TuplaGenerics detailOfUserJoined = (TuplaGenerics) message.getPayload(new TypeToken<TuplaGenerics<String,Integer>>() {}.getType());
        view.displayUserJoined((String) detailOfUserJoined.getFirst(), (Integer) detailOfUserJoined.getSecond());
        break;

      case ADDED_TO_QUEUE:
        TuplaGenerics detailsOfLobby = (TuplaGenerics) message.getPayload(new TypeToken<TuplaGenerics<List<String>,Integer>>() {}.getType());
        view.displayAddedToQueue((List<String>) detailsOfLobby.getFirst(), (Integer) detailsOfLobby.getSecond());
        break;

      case PROPOSE_RESTORE_MATCH:
        view.displayProposeRestoreMatch();
        break;

      case START_MATCH:
        view.displayStartingMatch();
        break;

      case DISCONNECTED_SERVER_SIDE:
        view.displayDisconnected((String)message.getPayload(String.class));
        break;

      case SERVER_LOST:
        view.displayDisconnected("Connection lost"); // will close the socket and terminate the execution
        break;

      case HEARTBEAT: // I won't receive this here anymore. Check ServerAdapter.java
        //System.out.println("SERVER IS ALIVE");
        break;

      case CHOOSE_GAME_CARDS:
        Type type = new TypeToken<ChooseGameCardMessage>() {}.getType();
        ChooseGameCardMessage chooseGameCardMessage = (ChooseGameCardMessage)message.getPayload(type);
        view.displayCardSelection(chooseGameCardMessage.getCardMap(), chooseGameCardMessage.getNumberOfPlayer());
        break;

      case CHOOSE_PERSONAL_CARD:
        List<TuplaGenerics> listAvailableCardFromServer = (List<TuplaGenerics>) message.getPayload(new TypeToken<List<TuplaGenerics<Card,String>>>() {}.getType());
        List<Card> availableCards = new ArrayList<>();
        listAvailableCardFromServer.forEach(tupla -> {
          if (tupla.getSecond() == null) {
            availableCards.add((Card) tupla.getFirst());
          }
        });
        view.displayChoicePersonalCard(availableCards);
        break;

      case FORCED_CARD:
        Card card = (Card)message.getPayload(Card.class);
        view.displayForcedCard(card);
        break;
      /* case used to choose the first player to position his workers, is selected by the current player */
      case CHOOSE_FIRST_PLAYER:
        List<Player> payloadOfChooseFirstPlayer = (List<Player>) message.getPayload(new TypeToken<List<Player>>() {}.getType());
        view.displayAskFirstPlayer(payloadOfChooseFirstPlayer);
        break;

      case ISLAND_UPDATED:
        fieldCache = (Cell[][]) message.getPayload(Cell[][].class); //siam sicuri gli piaccia?
        break;

      case LIST_PLAYER_UPDATED:
        playerListCache = (List<Player>) message.getPayload(new TypeToken<List<Player>>() {}.getType());
        break;

      case PLAYER_UPDATED:
        Player playerFromServer = (Player) message.getPayload(Player.class);
        Player playerToUpdate = playerListCache.stream().filter(player -> player.getName().equals(playerFromServer.getName())).findFirst().orElse(null);
        if (playerToUpdate != null) {
          playerListCache.set(playerListCache.indexOf(playerToUpdate), playerFromServer);
        }
        break;

      case LOCATION_UPDATED:
        Location locationUpdate = (Location) message.getPayload(Location.class);
        setLocationCache(locationUpdate);
        view.displayLocationUpdated();
        break;

      case TOWER_UPDATED:
        Cell cellUpdated = (Cell) message.getPayload(Cell.class);
        view.displayCellUpdated(cellUpdated);
        break;

      case CHOOSE_POSITION_OF_WORKERS:
        List<Player> payloadOfChoosePositionOfWorkers = (List<Player>) message.getPayload(new TypeToken<List<Player>>() {}.getType());
        view.displaySetInitialPosition(payloadOfChoosePositionOfWorkers);
        break;

      case INIT_TURN:
        listOfPhasesCache = new ArrayList<>();
        listOfPhasesCache.add((Phase) message.getPayload(new TypeToken<Phase>() {}.getType()));
        view.displayChoiceOfAvailablePhases();
        break;

      case END_TURN:
        view.displayEndTurn();
        break;

      case NEXT_PHASE_AVAILABLE:
        listOfPhasesCache = (List<Phase>) message.getPayload(new TypeToken<List<Phase>>() {}.getType());
        view.displayChoiceOfAvailablePhases();
      break;

      case AVAILABLE_CELL_FOR_MOVE:
        availableMoveCells =  (List<Cell>) message.getPayload(new TypeToken<List<Cell>>() {}.getType());
        view.displayChoiceOfAvailableCellForMove();
        break;

      case AVAILABLE_CELL_FOR_BUILD:
        availableBuildCells =  (HashMap<Cell, List<Integer>>) message.getPayload(new TypeToken<HashMap<Cell, List<Integer>>>() {}.getType());
        view.displayChoiceOfAvailableCellForBuild();
        break;

      case WINNING_PLAYER_UPDATED:
        Player winningPlayer = (Player) message.getPayload(Player.class);
        if (winningPlayer.getName().equals(username)) {
          view.displayWinnerMessage();
        } else {
          view.displayLoserMessage(winningPlayer);
        }
        break;

      case PLAYER_HAS_LOST:
        Player player = (Player) message.getPayload(Player.class);
        if (player.getName().equals(username)) {
          view.displayLoserMessage(null);
        } else {
          view.displayLoserPlayer(player);
        }
        break;

      default:
        break;

    }
  }

  // Used to notify connection to the server
  private void startHeartbeat() {
    Timer timer = new Timer();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        sendToServer(new Message(TypeOfMessage.HEARTBEAT));
        //System.out.println("PING");
      }
    }, 1000, Configuration.serverTimeout / 2 *1000); // this must be lower than (half should be ok) the value used server side in setSoTimeout()
  }

  public void sendToServer(Message message) {
    message.setUsername(this.username); // add the username to each message
    message.setUUID(this.UUID); // add the UUID to each message. Used to validate the user server side
    serverAdapter.send(message);
  }

  public void setServerIP(String serverIP) {
    this.serverIP = serverIP;
  }

  public String getServerIP() {
    return serverIP;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public int getServerPort() {
    return serverPort;
  }

  public ViewInterface getView() {
    return view;
  }

  public void setView(ViewInterface view) {
    this.view = view;
  }

  public String getUUID() {
    return UUID;
  }

  public void setUUID(String UUID) {
    this.UUID = UUID;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  private void setLocationCache(Location location) {
    this.locationCache = location;
    updateModifiedWorkersCache();
  }

  public Location getLocationCache() {
    return this.locationCache;
  }

  private List<Worker> modifiedWorkersCache = new ArrayList<>();

  private void updateModifiedWorkersCache() {
    if(locationCache != null)
      modifiedWorkersCache.addAll(new ArrayList<>(locationCache.getModifiedWorkers()));
  }

  public void clearModifiedWorkersCache() {
    this.modifiedWorkersCache.clear();
  }

  public List<Worker> getModifiedWorkersCache() {
    return modifiedWorkersCache;
  }

  public Cell[][] getFieldCache() {
    return fieldCache;
  }

  public List<Player> getPlayerListCache(){
    return playerListCache;
  }

  public List<Phase> getListOfPhasesCache(){
    return listOfPhasesCache;
  }

  public List<Worker> getMyWorkerCached(){
    List<Worker> myWorkers = playerListCache.stream().filter(player -> player.getName().equals(username)).flatMap(player -> player.getWorkers().stream()).collect(Collectors.toList());
    return myWorkers;
  }

  public List<Cell> getAvailableMoveCells() {
    return availableMoveCells;
  }
  public HashMap<Cell, List<Integer>> getAvailableBuildCells() {
    return availableBuildCells;
  }

  public boolean getWaiting(){
    return this.waiting;
  }
}
