package it.polimi.ingsw.psp40.commons;

import it.polimi.ingsw.psp40.model.Component;
import org.junit.Test;

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
