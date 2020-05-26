package it.polimi.ingsw.psp40.view.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class UtilsGUI {

    protected static void addClassToElement(Node node, String nameOfClass) {
        ObservableList<String> listOfClasses = node.getStyleClass();
        if (listOfClasses.indexOf(nameOfClass) == -1) {
            listOfClasses.add(nameOfClass);
        }
    }

    protected static void removeClassToElement(Node node, String nameOfClass) {
        node.getStyleClass().remove(nameOfClass);
    }

    protected static void buttonHoverEffect(Button button) {
        button.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(0.3);
                button.setEffect(colorAdjust);
            } else {
                button.setEffect(null);
            }
        });
    }

    protected static void slideInDownAnimation(Node node) {
        new Timeline(

                new KeyFrame(Duration.millis(0),
                        new KeyValue(node.translateYProperty(), -100, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(node.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                )).play();
    }
}
