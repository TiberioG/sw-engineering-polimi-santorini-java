package it.polimi.ingsw.psp40.network.server;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Class used to handle connection with a client socket
 * @author sup3rgiu
 */
public class ClientHandler implements Runnable {
  private Socket client;
  private Server server;
  private ObjectOutputStream outputStm;
  private ObjectInputStream inputStm;

  private String UUID;
  private boolean isConnected;
  private boolean isLogged;

  ClientHandler(Socket client, Server server) {
    this.client = client;
    this.server = server;
    this.isConnected = true;
    this.isLogged = false;
    this.UUID = null;
  }


  @Override
  public void run() {
    try {
      outputStm = new ObjectOutputStream(client.getOutputStream());
      inputStm = new ObjectInputStream(client.getInputStream());
      handleClientConnection();
    } catch (IOException e) {
      if(e instanceof SocketTimeoutException) {
        System.out.println("TIMEOUT SCADUTO - Questo client non è più raggiungibile");
      }
      isConnected = false;
      server.clientDisconnected(this);
      System.out.println("Client " + client.getInetAddress() + " connection dropped");
    }
  }

  protected void sendMessage(Message message){
    try {
      outputStm.reset();
      outputStm.writeObject(message);
      outputStm.flush();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }


  private void handleClientConnection() throws IOException {

    System.out.println("Connected to " + client.getInetAddress());
    server.addClient(this);

    try {
      while (true) {
        Message message = (Message) inputStm.readObject();

        // Will be done only at the first message received (login message)
        if(message.getTypeOfMessage() == TypeOfMessage.LOGIN && message.getUsername() != null && message.getUUID() == null) {
          String UUID = java.util.UUID.randomUUID().toString();
          this.UUID = UUID;
          server.associateClient(UUID, this);
          message.setUUID(UUID);
        }

        server.receivedMessage(message);

      }
    } catch (ClassNotFoundException | ClassCastException e) {
      e.printStackTrace();
      System.out.println("Invalid stream from client");
    }

    client.close();
  }

  protected synchronized void closeConnection(String message) {
    sendMessage(new Message(TypeOfMessage.DISCONNECTED_SERVER_SIDE, message)); // notify user about the server-side disconnection
    try {
      client.close();
    } catch (IOException e){
      System.err.println(e.getMessage());
    }
    isConnected = false;
  }

  public boolean isConnected() {
    return isConnected;
  }

  public boolean isLogged() {
    return isLogged;
  }

  public void setLogged(boolean logged) {
    isLogged = logged;
  }

  public String getUUID() {
    return UUID;
  }
}
