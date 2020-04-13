package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.network.server.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

//todo creare qui o cli o gui qui
//todo metodo sendtoServer -> adapter -> server
// qui faccio conversione a stringa

public class Client implements Runnable, ServerObserver
{
  /* auxiliary variable used for implementing the consumer-producer pattern*/
  private String response = null;


  public static void main( String[] args )
  {
    /* Instantiate a new Client which will also receive events from
     * the server by implementing the ServerObserver interface */
    Client client = new Client();
    client.run();
  }


  @Override
  public void run()
  {
    /*
     * WARNING: this method executes IN THE CONTEXT OF THE MAIN THREAD
     */

    Scanner stdin = new Scanner(System.in);

    System.out.println("IP address of server?");
    String ip = readIp(stdin);

    System.out.println("Port number?");
    int port = readPort(stdin);


    /* open a connection to the server */
    Socket server;
    try {
      server = new Socket(ip, port);
    } catch (IOException e) {
      System.out.println("Server unreachable");
      return;
    }
    System.out.println("Connected");

    /* Create the adapter that will allow communication with the server
     * in background, and start running its thread */
    ServerAdapter serverAdapter = new ServerAdapter(server);
    serverAdapter.addObserver(this);
    Thread serverAdapterThread = new Thread(serverAdapter);
    serverAdapterThread.start();

    String str = stdin.nextLine();
    while (!"".equals(str)) {

      synchronized (this) {
        /* reset the variable that contains the next string to be consumed
         * from the server */
        response = null;

        serverAdapter.requestConversion(str);

        /* While we wait for the server to respond, we can do whatever we want.
         * In this case we print a count-up of the number of seconds since we
         * requested the conversion to the server. */
        int seconds = 0;
        while (response == null) {
          System.out.println("been waiting for " + seconds + " seconds");
          try {
            wait(1000);
          } catch (InterruptedException e) { }
          seconds++;
        }

        /* we have the response, print it */
        System.out.println(response);
      }

      str = stdin.nextLine();
    }

    serverAdapter.stop();
  }


  @Override
  public synchronized void didReceiveConvertedString(String oldStr, String newStr)
  {
    /*
     * WARNING: this method executes IN THE CONTEXT OF `serverAdapterThread`
     * because it is called from inside the `run` method of ServerAdapter!
     */

    /* Save the string and notify the main thread */
    response = newStr;
    notifyAll();
  }

  private String readIp(Scanner stdin) {
    String ip;
    ip = stdin.nextLine();

    while (!isValidIp(ip)) {
      System.out.println("This is not a valid IPv4 address. Please, try again:");
      ip = stdin.nextLine();
    }
    return ip;
  }

  private static boolean isValidIp(String input) {
    return input.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$");
  }

  private int readPort(Scanner stdin) {
    int output;
    try {
      output = stdin.nextInt(); //Integer.parseInt(stdin.nextLine());
    } catch (InputMismatchException e) {
      output = Server.MIN_PORT - 1;
      stdin.nextLine();
    }
    while (output > Server.MAX_PORT || output < Server.MIN_PORT) {
      System.out.println("Value must be between " + Server.MIN_PORT + " and " + Server.MAX_PORT + ". Please, try again:");
      try {
        output = stdin.nextInt();
      } catch (InputMismatchException e) {
        output = Server.MIN_PORT - 1;
        stdin.nextLine();
      }
    }
    stdin.nextLine(); // handle nextInt()
    return output;
  }



  public void handleMessage(Message message){
    switch (message.getTypeOfMessage()) {
      case CARD_GET:
        //deserializzare qui
        viewInterface.cardSelection();
        //todo passare una lista di carte
        //todo attributo

      case REQUEST_INITIAL_POSITION
        //deserial
        viewInterface.setInitialPosition(List < CoordinatesMessage >); //ci piace??

    }
  }
}
