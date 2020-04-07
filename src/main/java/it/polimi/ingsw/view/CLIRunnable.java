package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Sta classe serve solo per testare il flusso del gioco senza tutta la sbatta di usare MVC
 */
public class CLIRunnable {

    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private Match match;



    /* METHODS*/
    static public void startGame(){
        out.println("Welcome to Santorini");


    }

    public void userLogin(){
        out.println("Quanti  siete?");

       int  numb = Integer.parseInt(in.nextLine());

        for (int i = 0; i < numb ; i++){
            out.println("nome?");


        }




    }


    public void startMatch(){

    }

    public void showIsland(){
        String newLine = System.getProperty("line.separator");
        int indexPlayer = 1;
        for(Player player : match.getPlayers()) {
            int indexWorker = 1;
            for(Worker worker : player.getWorkers()) {
                System.out.println(newLine+"Worker" + indexPlayer + "_" + indexWorker++);
                List<Cell> availableCells = strategyMove.getAvailableCells(worker);
                if(availableCells.size() == 0) { System.out.println("This worker can not move :("); }
                for(Cell cell : availableCells) {
                    System.out.println("x: " + cell.getCoordX() + ", y: " + cell.getCoordY() );
                }
            }
            indexPlayer++;
        }

    }

    public static void main( String[] args )
    {
        CLIRunnable.startGame();

    }


}



