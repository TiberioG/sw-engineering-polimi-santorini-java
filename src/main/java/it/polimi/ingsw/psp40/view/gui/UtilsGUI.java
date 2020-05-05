package it.polimi.ingsw.psp40.view.gui;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;

public class UtilsGUI {

    public static void addClassToElement(Control control, String nameOfClass) {
        ObservableList<String> listOfClasses = control.getStyleClass();
        if (listOfClasses.indexOf(nameOfClass) == -1) {
            listOfClasses.add(nameOfClass);
        }
    }


    public static void removeClassToElement(Control control, String nameOfClass) {
        control.getStyleClass().remove(nameOfClass);
    }
}
