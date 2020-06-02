package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.view.cli.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PlayerInfoPopup extends Stage {

    protected final Stage ownerStage;
    protected final VBox vBox = new VBox(5);

    private final Player player;

    PlayerInfoPopup(Stage ownerStage, Player player, Node node) {
        super();
        this.player = player;
        this.ownerStage = ownerStage;
        this.initOwner(ownerStage);
            buildStage(node);
        buildPopup();
    }

    private void buildStage(Node node) {
        this.initStyle(StageStyle.TRANSPARENT);

        // Hide the pop-up stage before it is shown and becomes relocated
        this.setOnShowing(ev -> this.hide());
        // Relocate the pop-up Stage
        this.setOnShown(ev -> {
            double x = node.localToScreen(node.getBoundsInLocal()).getMaxX();
            if((boolean) node.getUserData()) { // if is an image in right pane of BorderPane
                x = x - this.getWidth() - node.getBoundsInParent().getWidth();
            }
            double y = node.localToScreen(node.getBoundsInLocal()).getMaxY() - node.getBoundsInParent().getHeight();
            this.setX(x);
            this.setY(y);
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
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(20));
        UtilsGUI.addClassToElement(vBox, "popup-stage");

        vBox.getChildren().add(createText("User: " + player.getName()));
        vBox.getChildren().add(createText("God: " + player.getCurrentCard().getName()));
        vBox.getChildren().add(createText(player.getCurrentCard().getDescription()));

        // Set scene
        Scene scene = new Scene(vBox, Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        this.setScene(scene);
    }

    protected Text createText(String string) {
        Text text = new Text(string);
        text.setTextAlignment(TextAlignment.CENTER);
        double wrapWidth = this.ownerStage.getScene().getWidth() * 0.2;
        if(text.getLayoutBounds().getWidth() > wrapWidth) {
            text.setWrappingWidth(wrapWidth);
        }
        return text;
    }
}
