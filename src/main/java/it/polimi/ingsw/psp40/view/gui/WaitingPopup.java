package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.model.Player;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

;

public class WaitingPopup extends PopupStage {

    private String details;

    /**
     * Constructor
     * @param ownerStage
     * @param details
     */
    WaitingPopup(Stage ownerStage, String details) {
        super(ownerStage);
        UtilsGUI.addClassToElement(vBox, "vbox-popup-stage"); //default class
        this.details = details;
        build();
    }


    private void build() {
        // Create scene
        Text text = createText(details);
        text.setFont(new Font(16));
        this.vBox.getChildren().add(text);
        UtilsGUI.addClassToElement(vBox, "waiting-popup");
    }

}
