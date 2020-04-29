package it.polimi.ingsw.psp40.network.client;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.commons.messages.ChooseGameCardMessage;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TuplaGenerics;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.view.ViewInterface;
import it.polimi.ingsw.psp40.view.cli.CLI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements ServerObserver {


  /* Attributes */

  private ViewInterface view;

  private String serverIP;

  private int serverPort;

  private String UUID = null;

  private String username = null;

  ServerAdapter serverAdapter;

  private static final Logger LOGGER = Logger.getLogger("Client");

  private Cell[][] fieldCache;
  private Location locationCache;



  /* Constructor(s) */

  public Client() {}

  /* Methods */


  public static void main( String[] args )
  {
    Client client = new Client();
    boolean cli = true;

    if (args.length > 0) {

      switch (args[0]) {

        case "cli":
          cli = true;
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
      CLI view = new CLI(client);
      client.setView(view);
      view.displaySetup(); // ask for server IP and Port
    }

    /*else {
      Application.launch(GUI.class, args);
    }*/

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
    /*case CARD_GET:
      //deserializzare qui
      view.cardSelection();
      //todo passare una lista di carte

    case REQUEST_INITIAL_POSITION:
      //deserial
      view.setInitialPositionco(List < CoordinatesMessage >); //ci piace??*/

        case GENERIC_MESSAGE:
          view.displayGenericMessage((String) message.getPayload(String.class));
          break;

        case LOGIN_SUCCESSFUL:
          setUUID(message.getUUID());
          view.displayLoginSuccessful();
          break;

        case LOGIN_FAILURE:
          view.displayLoginFailure((String) message.getPayload(String.class));
          break;

        case USER_JOINED:
          view.displayUserJoined((String) message.getPayload(String.class));
          break;

        case ADDED_TO_QUEUE:
          view.displayAddedToQueue((String) message.getPayload(String.class));
          break;

        case START_MATCH:
          view.displayStartingMatch();
          break;

        case DISCONNECTED_SERVER_SIDE:
          view.displayDisconnected((String) message.getPayload(String.class));
          break;

        case SERVER_LOST:
          view.displayDisconnected("Connection lost"); // will close the socket and terminate the execution
          break;

        case HEARTBEAT:
          //System.out.println("SERVER IS ALIVE");
          break;

        case CHOOSE_GAME_CARDS:
          Type type = new TypeToken<ChooseGameCardMessage>() {
          }.getType();
          ChooseGameCardMessage chooseGameCardMessage = (ChooseGameCardMessage) message.getPayload(type);
          view.displayCardSelection(chooseGameCardMessage.getCardMap(), chooseGameCardMessage.getNumberOfPlayer());
          break;

        case CHOOSE_PERSONAL_CARD:
          List<TuplaGenerics> listAvailableCardFromServer = (List<TuplaGenerics>) message.getPayload(new TypeToken<List<TuplaGenerics<Card, String>>>() {}.getType());
          //todo migrate this to cli and gui for different logic
          List<Card> availableCards = new ArrayList<>();
          listAvailableCardFromServer.forEach(tupla -> {
            if (tupla.getSecond() == null) {
              availableCards.add((Card) tupla.getFirst());
            }
          });
          view.displayChoicePersonalCard(availableCards);
          break;

        /* case used to choose the first player to position his workers, is selected by the current player */
        case CHOOSE_FIRST_PLAYER:
          List<Player> allPlayers = (List<Player>) message.getPayload(new TypeToken<List<Player>>() {}.getType());
          view.displayAskFirstPlayer(allPlayers);
          break;

        case ISLAND_UPDATED:
          //this.fieldCache =  (Cell[][]) message.getPayload(Cell[][].class); //siam sicuri gli piaccia?
          setFieldCache((Cell[][]) message.getPayload(Cell[][].class));
          break;

        case LOCATION_UPDATED:
          //this.locationCache = (Location) message.getPayload(Location.class);
          setLocationCache((Location) message.getPayload(Location.class));
          break;

        case CHOOSE_POSITION_OF_WORKERS:
          List<Player> allPlayers1 = (List<Player>) message.getPayload(new TypeToken<List<Player>>() {}.getType());
          view.displaySetInitialPosition(allPlayers1);
          break;

        default:
          break;

      }

  }

  public synchronized void setFieldCache(Cell[][] fieldCache) {
    this.fieldCache = fieldCache;
  }

  public synchronized void setLocationCache(Location locationCache) {
    this.locationCache = locationCache;
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

  public Location getLocationCache() {
    return this.locationCache;
  }

  public Cell[][] getFieldCache() {
    return fieldCache;
  }

  //todo local check
}
