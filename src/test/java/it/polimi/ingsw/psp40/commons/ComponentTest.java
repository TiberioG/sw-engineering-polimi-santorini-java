package it.polimi.ingsw.psp40.commons;

import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentTest {

    @Test
    public void getComponentCode() {
    }

    @Test
    public void allNames() {
        String [] list = Component.allNames();
        for (int i = 0; i < list.length; i++){
            System.out.println(list[i]);
        }
    }
}
