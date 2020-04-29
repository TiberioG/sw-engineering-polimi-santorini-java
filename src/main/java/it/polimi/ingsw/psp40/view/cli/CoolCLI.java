package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.messages.LoginMessage;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.ViewInterface;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class CoolCLI implements ViewInterface {
    private final Client client;
    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private Date date = null;
    private int numOfPlayers = 0;

    private static final int MIN_PORT = 1000; // todo usare quelli del server. Possibile?
    private static final int MAX_PORT = 50000;

    private final Utils utils = new Utils(in, out);

    private static Frame left = new Frame(new int[]{7,0}, new int[]{99, 58}, in, out);
    private static Frame center = new Frame(new int[]{7,58}, new int[]{99, 150}, in, out);
    private IslandAdapter myisland;

    /**
     * Constructor
     * @param client
     */
    public CoolCLI(Client client){
        this.client = client;
        Terminal.resize(110, 150);

        try {
            Utils.maketitle();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }



    @Override
    public void displaySetup() {
        center.clear();
        center.start();
        center.print(Utils.centerString(center.getColSpan(), "Enter address of server"));
        String ip = utils.readIp();
        System.out.println("Port number?");
        int port = utils.readNumbers( MIN_PORT, MAX_PORT);
        client.setServerIP(ip);
        client.setServerPort(port);
        client.connectToServer();
    }

    @Override
    public void displaySetupFailure() {
        left.clear();

        out.println("Can not reach the server, please try again");
        displaySetup();
    }

    @Override
    public void displayLogin() {
        left.clear();
        out.println("Choose your username:   ");
        in.nextLine();
        String username = utils.readnames();

        date = utils.readDate("birthdate");

        out.println("How many people do you want to play with?");
        numOfPlayers = utils.readNumbers(2, 3);

        client.setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, date, numOfPlayers, TypeOfMessage.LOGIN);
        client.sendToServer(loginMessage);

    }

    @Override
    public void displayLoginSuccessful() {
        left.clear();
        left.println("You have been logged in successfully");
    }

    @Override
    public void displayLoginFailure(String details) {

    }


    @Override
    public void displayUserJoined(String details) {
        left.clear();
        left.start();
        out.println(details);
    }

    @Override
    public void displayAddedToQueue(String details) {
        left.clear();
        left.start();
        out.println(details);
    }

    @Override
    public void displayStartingMatch() {
        left.clear();
        left.start();
        out.println("MATCH IS STARTING!!!!");
    }

    @Override
    public void displayGenericMessage(String message) {
        left.clear();
        left.start();
        out.println(message);
    }

    @Override
    public void displayDisconnected(String details) {
        out.println(details);
        client.close();
    }

    @Override
    public void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers) {
        left.clear();
        left.start();
        String[] names = cards.values().stream().map(Card::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Cards Available", names, 100);
        } catch (InterruptedException e) {
            //todo aggiunere frame per le eccezioni
        }
        System.out.println("Select " + numPlayers + " cards");

        //String[] selectedCards = IntStream.range(0, numPlayers).mapToObj(i -> names[utils.readNumbers(0, names.length - 1)]).toArray(String[]::new);
        List<Integer> selections = utils.readNotSameNumbers(0, names.length - 1, numPlayers );
        List<Integer> listOfIdCardSelected = new ArrayList<>();

        for (Integer selection : selections) {
            String nameSelected = names[selection];
            for (HashMap.Entry<Integer, Card> entry : cards.entrySet()) {
                if (nameSelected.equals(entry.getValue().getName())) {
                    listOfIdCardSelected.add(entry.getKey());
                }
            }
        }
        client.sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, listOfIdCardSelected));

    }

    @Override
    public void displayChoicePersonalCard(List<Card> availableCards) {
        left.clear();
        left.start();
        String[] names = availableCards.stream().map(Card::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Cards Available", names, 100);
        } catch (InterruptedException e) {
           //todo frame
        }

        System.out.println("Choose your personal card");

        int numberSelected = utils.readNumbers(0, names.length - 1);
        int cardIdSelected = -1;
        for (Card card : availableCards) {
            if (card.getName().equals(names[numberSelected])) {
                cardIdSelected = card.getId();
            }
        }
        client.sendToServer(new Message(TypeOfMessage.SET_CARD_TO_PLAYER, cardIdSelected));
        left.clear();
    }

    @Override
    public void displayCardInGame(List<Card> cardInGame) {
        //todo frame fisso?
        left.start();
        String[] names = cardInGame.stream().map(Card::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Card selected", names, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        left.clear();
    }

    @Override
    public void displaySetInitialPosition(List<Player> playerList) {
        List<String> colorsAvailable =  Arrays.asList(Colors.allNames());
        playerList.forEach(player -> {
            if (player.getWorkers().size() != 0){
                colorsAvailable.remove(player.getWorkers().get(0).getColor().name());
            }
        });
        String[] colorsAvailableArry = (String[]) colorsAvailable.toArray();

        left.start();
        out.println("It's time to choose one color for your workers, choose from following list:");

        try {
            utils.singleTableCool("options", colorsAvailableArry, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int choice = utils.readNumbers(0,colorsAvailableArry.length - 1);

        out.println("Wooow, you have selected color " + colorsAvailableArry[choice]+ " for your workers");


        //START ISLAND
        boolean debug = false;
        int curRow = 0;
        int curCol = 0;

        try {
            this.showIsland();
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
        }

        while (true) {
            try {
                if (System.in.available() != 0) {
                    int c = System.in.read();  //read one char at a time in ascii code

                    //GETTING Q to quit
                    if (c == 113) {   // if press Q -> quit this visualization
                        break;
                    }

                    //getting an ARROW KEY
                    else if (c == 27) { // first part of arrow key == ESC
                        int next1 = System.in.read();
                        int next2 = System.in.read();
                        if (next1 == 91) { //  read [
                            if (next2 == 65) {                     //UP  arrow
                                if (curRow > 0 && curRow <= 5) {
                                    curRow--;
                                }
                            } else if (next2 == 66) {              //DOWN arrow
                                if (curRow >= 0 && curRow < 4) {
                                    curRow++;
                                }
                            } else if (next2 == 67) {              //RIGHT arrow
                                if (curCol >= 0 && curCol < 4) {
                                    curCol++;
                                }
                            } else if (next2 == 68) {               //LEFT  arrow
                                if (curCol > 0 && curCol <= 5) {
                                    curCol--;
                                }
                            }
                        }
                        myisland.setSelected(curRow, curCol);
                        myisland.print();
                    }//end arrow management


                    // gettind D for debug option
                    else if (c == 100) {
                        debug = !debug;
                        if (!debug) {
                            myisland.print();
                        }
                    }

                    if (debug) {
                        myisland.debug();
                    }

                } //end system in available
            } catch (IOException | InterruptedException e) {
                //todo frame per except
            }
        }// end while true



    }

    @Override
    public void displayAskFirstPlayer(List<Player> allPlayers) {
        String[] names = allPlayers.stream().map(Player::getName).toArray(String[]::new);

        try {
            utils.singleTableCool("Players available", names, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //todo double column for also card display

        int selection = utils.readNumbers(0, names.length -1);
        client.sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, names[selection]));

    }


    public void showIsland() throws IOException, InterruptedException {
        myisland = new IslandAdapter(client.getFieldCache(), client.getLocationCache() );
        Terminal.noBuffer();
        Terminal.hideCursor();
        myisland.print();
    }



}
