package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements ServerObserver {

  /* Attributes */

  ViewInterface view;

  String serverIP;

  int serverPort;

  ServerAdapter serverAdapter;

  private static final Logger LOGGER = Logger.getLogger("Client");


  /* auxiliary variable used for implementing the consumer-producer pattern*/
  private String response = null;

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
      view.displayLogin();
    } catch (IOException e) {
      //System.out.println("Server unreachable");
      view.displaySetupFailure();
    }
  }

  /* todo: SERVE?? Nel caso, implementarlo correttamente in serverAdapter */
  public void stop() {
    serverAdapter.stop();
  }


  public ViewInterface getView() {
    return view;
  }

  public void setView(ViewInterface view) {
    this.view = view;
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

      case LOGIN_SUCCESSFUL:
        view.displayLoginSuccessful((String)message.getObjectFromJson(String.class));
        stop();
    }
  }

  public void sendToServer(Message message) {
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
}
