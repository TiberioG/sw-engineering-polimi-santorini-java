package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientHandler implements Runnable {
  private Socket client;
  private Server server;
  private ObjectOutputStream outputStm;
  private ObjectInputStream inputStm;



  ClientHandler(Socket client, Server server) {
    this.client = client;
    this.server = server;
  }


  @Override
  public void run() {
    try {
      outputStm = new ObjectOutputStream(client.getOutputStream());
      inputStm = new ObjectInputStream(client.getInputStream());
      handleClientConnection();
    } catch (IOException e) {
      System.out.println("Client " + client.getInetAddress() + " connection dropped");
    }
  }

  public void sendMessage(Message message){
    try{
      outputStm.reset();
      outputStm.writeObject(message);
      outputStm.flush();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }


  private void handleClientConnection() throws IOException {

    System.out.println("Connected to " + client.getInetAddress());

    try {
      while (true) {
        Message message = (Message) inputStm.readObject();

        // todo: genereare un uuid univoco e usarlo come key della mappa.
        //       Inviare l'uuid al client che dovrà usarlo per ogni messaggio successivo

        // Will be done only at the first message received (login message)
        if(message.getUsername() != null && !server.clientAssociationExists(message.getUsername())) {
          server.addClient(message.getUsername(), this);
        }

        server.receivedMessage(message);

      }
    } catch (ClassNotFoundException | ClassCastException e) {
      System.out.println("Invalid stream from client");
    }

    client.close();
  }
}
