package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.TypeOfMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class ClientHandler implements Runnable {
  private Socket client;
  private Server server;
  private ObjectOutputStream outputStm;
  private ObjectInputStream inputStm;

  private String UUID;
  private boolean isConnected;

  ClientHandler(Socket client, Server server) {
    this.client = client;
    this.server = server;
    this.isConnected = true;
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
        System.out.println(e.getClass().getCanonicalName() + " TIMEOUT SCADUTO");
      }
      isConnected = false;
      server.userDisconnected(this.UUID);
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
}
