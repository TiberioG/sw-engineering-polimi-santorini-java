package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.TypeOfMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class ServerAdapter implements Runnable
{

  private Socket server;
  private ObjectOutputStream outputStm;
  private ObjectInputStream inputStm;

  private List<ServerObserver> observers = new ArrayList<>();

  private Object objReceive = new Object();
  private Object objSend = new Object();


  public ServerAdapter(Socket server)
  {
    this.server = server;
  }


  public void addObserver(ServerObserver observer)
  {
    synchronized (observers) {
      observers.add(observer);
    }
  }

  public void removeObserver(ServerObserver observer)
  {
    synchronized (observers) {
      observers.remove(observer);
    }
  }

  public synchronized void stop()
  {
    try {
      server.close();
    } catch (IOException e) {
    }
  }

  @Override
  public void run() {
    try {
      outputStm = new ObjectOutputStream(server.getOutputStream());
      inputStm = new ObjectInputStream(server.getInputStream());
      handleServerConnection();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      if (e instanceof SocketTimeoutException) {
        notifyServerLost();
      } else {
        System.out.println("Server has died or protocol violation");
        try {
          server.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * Listens for incoming messages from the server
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void handleServerConnection() throws IOException, ClassNotFoundException {
    while (true) {
      Message msg = (Message) inputStm.readObject();

      /* copy the list of observers in case some observers changes it from inside
       * the notification method */
      List<ServerObserver> observersCpy;
      synchronized (observers) {
        observersCpy = new ArrayList<>(observers);
      }

      /* notify the observers that we got a message from the server */
      for (ServerObserver observer : observersCpy) {
        observer.handleMessage(msg);
      }
    }
  }

  /**
   * Sends a message to the server
   * @param message message to be seent
   */
  public void send(Message message) {
    try {
      outputStm.reset();
      outputStm.writeObject(message);
      outputStm.flush();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  private void notifyServerLost() {
    Message msg = new Message(TypeOfMessage.SERVER_LOST);
    List<ServerObserver> observersCpy;
    synchronized (observers) {
      observersCpy = new ArrayList<>(observers);
    }

    /* notify the observers that we got a message from the server */
    for (ServerObserver observer : observersCpy) {
      observer.handleMessage(msg);
    }
  }


}
