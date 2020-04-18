package it.polimi.ingsw.network.client;

import it.polimi.ingsw.commons.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerAdapter implements Runnable
{
  private enum Commands {
    SEND_MESSAGE,
    STOP
  }
  private Commands nextCommand;
  private Message messageToSend;

  private Socket server;
  private ObjectOutputStream outputStm;
  private ObjectInputStream inputStm;

  private List<ServerObserver> observers = new ArrayList<>();


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
    nextCommand = Commands.STOP;
    notifyAll();
  }

  @Override
  public void run()
  {
    try {
      outputStm = new ObjectOutputStream(server.getOutputStream());
      inputStm = new ObjectInputStream(server.getInputStream());
      handleServerConnection();
    } catch (IOException e) {
      System.out.println("Server has died");
      try {
        server.close();
      } catch (IOException ex) { }
    }

  }

  /**
   * Sends a message to the server
   * @param message
   */
  public synchronized void send(Message message) {
    nextCommand = Commands.SEND_MESSAGE;
    messageToSend = message;
    notifyAll();
  }

  /**
   * Runs two threads in background, one to listen for incoming messages from the server and one to send messages to the server
   */
  private synchronized void handleServerConnection() {
    new Thread(() -> {
      try {
        handleSendServerConnection();
      } catch (IOException e) {
        System.out.println("server has died");
      } catch (ClassCastException e) {
        System.out.println("protocol violation");
      }

      try {
        server.close();
      } catch (IOException e) { }
    }).start();

    new Thread(() -> {
      try {
        handleReceiveServerConnection();
      } catch (IOException e) {
        System.out.println("server has died");
      } catch (ClassCastException | ClassNotFoundException e) {
        System.out.println("protocol violation");
      }

      try {
        server.close();
      } catch (IOException e) { }
    }).start();
  }

  /**
   * Listens for incoming messages from the server
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private synchronized void handleReceiveServerConnection() throws IOException, ClassNotFoundException {
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
   * Listens for messages to be sent to server
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private synchronized void handleSendServerConnection() throws IOException
  {
    /* wait for commands */
    while (true) {
      nextCommand = null;
      try {
        wait();
      } catch (InterruptedException e) { }

      if (nextCommand == null)
        continue;

      switch (nextCommand) {
        case SEND_MESSAGE:
          outputStm.reset();
          outputStm.writeObject(messageToSend);
          outputStm.flush();
          break;

        case STOP:
          return;
      }
    }
  }

}
