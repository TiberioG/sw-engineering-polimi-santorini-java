package it.polimi.ingsw.psp40.view.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class UtilsGUI {

    public static void addClassToElement(Node node, String nameOfClass) {
        ObservableList<String> listOfClasses = node.getStyleClass();
        if (listOfClasses.indexOf(nameOfClass) == -1) {
            listOfClasses.add(nameOfClass);
        }
    }


    public static void removeClassToElement(Node node, String nameOfClass) {
        node.getStyleClass().remove(nameOfClass);
    }
}
