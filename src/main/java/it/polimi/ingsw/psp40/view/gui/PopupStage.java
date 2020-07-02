package it.polimi.ingsw.psp40.view.gui;

import animatefx.animation.AnimationFX;
import animatefx.animation.ZoomIn;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupStage extends Stage {

    protected final StackPane pane = new StackPane();
    protected final Stage ownerStage;
    protected final VBox vBox = new VBox(5);

    PopupStage(Stage ownerStage) {
        super();
        this.ownerStage = ownerStage;
        this.initOwner(ownerStage);
        buildStage();
        buildPopup();
    }

    private void buildStage() {
        // Blur and Disable the current scene
        this.ownerStage.getScene().getRoot().setEffect(new GaussianBlur());
        this.ownerStage.getScene().getRoot().setDisable(true);

        this.initStyle(StageStyle.TRANSPARENT);

        //popupStage.initModality(Modality.APPLICATION_MODAL);

        // Calculate the center position of the parent Stage
        double centerXPosition = ownerStage.getX() + ownerStage.getWidth() / 2d;
        double centerYPosition = ownerStage.getY() + ownerStage.getHeight() / 2d;
        // Hide the pop-up stage before it is shown and becomes relocated
        this.setOnShowing(ev -> this.hide());
        // Relocate the pop-up Stage
        this.setOnShown(ev -> {
            this.setX(centerXPosition - this.getWidth() / 2d);
            this.setY(centerYPosition - this.getHeight() / 2d);
            this.show();
        });

        // Relocate the pop-up if the primary stage is moved
        ownerStage.xProperty().addListener((obs, oldVal, newVal) -> {
            this.setX(this.getX() + (newVal.intValue() - oldVal.intValue()));
        });
        ownerStage.yProperty().addListener((obs, oldVal, newVal) -> {
            this.setY(this.getY() + (newVal.intValue() - oldVal.intValue()));
        });

    }

    private void buildPopup() {
        UtilsGUI.addClassToElement(pane, "popup-stage");
        vBox.setAlignment(Pos.CENTER);
        vBox.prefHeight(171);
        vBox.prefWidth(141);
        vBox.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().add(vBox);

        // Set scene
        Scene scene = new Scene(pane, Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        this.setScene(scene);
    }

    protected Text createText(String string) {
        Text text = new Text(string);
        text.setFont(new Font(18));
        text.setLineSpacing(-5);
        text.setTextAlignment(TextAlignment.CENTER);
        double wrapWidth = this.ownerStage.getScene().getWidth() * 0.2;
        if (text.getLayoutBounds().getWidth() > wrapWidth) {
            text.setWrappingWidth(wrapWidth);
        }
        return text;
    }

    protected void showWithAnimation() {
        showWithAnimation(1);
    }

    protected void showWithAnimation(double speedFactor) {
        this.show();
        AnimationFX animation = new ZoomIn(pane);
        animation.setSpeed(speedFactor);
        animation.play();
    }
}
