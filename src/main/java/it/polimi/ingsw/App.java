package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        Controller controller = new Controller();

        System.out.println(  "\u001B[31m" + "Hello World!");
    }

    public static boolean foo (){
        return true;
    }
}
