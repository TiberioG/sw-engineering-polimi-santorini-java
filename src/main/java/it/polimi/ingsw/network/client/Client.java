package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
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

      /* Create the adapter that will allow communication with the server
       * in background, and start running its thread */
      serverAdapter = new ServerAdapter(server);
      serverAdapter.addObserver(this);
      Thread serverAdapterThread = new Thread(serverAdapter);
      serverAdapterThread.start();
      startHeartbeat();
      view.displayLogin();
    } catch (IOException e) {
      //System.out.println("Server unreachable");
      view.displaySetupFailure();
    }
  }

  /* todo: SERVE?? Nel caso, implementarlo correttamente in serverAdapter */
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
        view.displayGenericMessage((String)message.getPayload(String.class));
        break;

      case LOGIN_SUCCESSFUL:
        setUUID(message.getUUID());
        view.displayLoginSuccessful();
        break;

      case LOGIN_FAILURE:
        view.displayLoginFailure((String)message.getPayload(String.class));
        break;

      case HOW_MANY_PLAYERS:
        view.displayHowManyPlayers();
        break;

      case USER_JOINED:
        view.displayUserJoined((String)message.getPayload(String.class));
        break;

      case ADDED_TO_QUEUE:
        view.displayAddedToQueue((String)message.getPayload(String.class));
        break;

      case START_MATCH:
        view.displayStartingMatch();
        break;

      case DISCONNECTED_SERVER_SIDE:
        view.displayDisconnected((String)message.getPayload(String.class));
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
        System.out.println("PING");
      }
    }, 1000, 10*1000); // this must be lower than (half should be ok) the value used server side in setSoTimeout()
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
}
