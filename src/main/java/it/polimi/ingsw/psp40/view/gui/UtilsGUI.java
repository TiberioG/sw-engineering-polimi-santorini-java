package it.polimi.ingsw.psp40.view.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.util.Duration;

public class UtilsGUI {

    static BooleanProperty trueBool = new SimpleBooleanProperty(true);


    /**
     * Method that allows you to add a class to a {@link Node}
     * @param node the node to which you want to add the class
     * @param nameOfClass the name of the class you want to add to the node
     */
    protected static void addClassToElement(Node node, String nameOfClass) {
        ObservableList<String> listOfClasses = node.getStyleClass();
        if (listOfClasses.indexOf(nameOfClass) == -1) {
            listOfClasses.add(nameOfClass);
        }
    }

    /**
     * Method that allows you to remove a class to a {@link Node}
     * @param node the node to which you want to remove the class
     * @param nameOfClass the name of the class you want to remove from the node
     */
    protected static void removeClassToElement(Node node, String nameOfClass) {
        node.getStyleClass().remove(nameOfClass);
    }

    /**
     * Method that allows you to make a hover effect to a specified button
     * @param button the button you want to apply the effect to
     */
    protected static void buttonHoverEffect(Button button) {
        nodeHoverEffect(button);
    }


    /**
     * Method that allows you to make a hover with a persistence effect to a specified button
     * @param button the button you want to apply the effect to
     */
    protected static void buttonHoverEffectWithPersistence(Button button, BooleanProperty persistence) {
        nodeHoverEffectWithPersistence(button, persistence);
    }

    /**
     * Method that allows you to make a hover with a persistence effect to a specified node
     * @param node the node you want to apply the effect to
     * @param persistence if persistence is true, keep the effect applied even when not hover
     */
    protected static void nodeHoverEffectWithPersistence(Node node, BooleanProperty persistence) {

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.3);

        applyEffectBinding(node, colorAdjust, persistence);

        node.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (!persistence.getValue()) {
                if (newValue) {
                    node.effectProperty().unbind();
                    node.setEffect(colorAdjust);
                } else {
                    applyEffectBinding(node, colorAdjust, persistence);
                }
            } else {
                applyEffectBinding(node, colorAdjust, persistence);
            }
        });
    }

    //TODO make javadoc
    private static void applyEffectBinding(Node node, Effect effect, BooleanProperty property) {
        node.effectProperty().bind(Bindings.when(
                property.isEqualTo(trueBool))
                .then(effect)
                .otherwise((Effect) null));
    }

    //TODO make javadoc
    protected static void nodeHoverEffect(Node node) {
        node.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(0.3);
                node.setEffect(colorAdjust);
            } else {
                node.setEffect(null);
            }
        });
    }

    /**
     * Method that allows you to make slideInDown animation to a specified node
     * @param node the node you want to apply the slide in down animation
     */
    protected static void slideInDownAnimation(Node node) {
        new Timeline(
            new KeyFrame(Duration.millis(0),
                    new KeyValue(node.translateYProperty(), -100, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
            ),
            new KeyFrame(Duration.millis(200),
                    new KeyValue(node.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
            )
        ).play();
    }
}
