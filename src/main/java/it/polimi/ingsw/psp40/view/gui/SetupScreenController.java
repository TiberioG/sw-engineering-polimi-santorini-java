package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.cli.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;

import java.util.function.UnaryOperator;

public class SetupScreenController {

    /* Attributes */

    private Client client;

    @FXML
    private Button connectButton;

    @FXML
    private TextField ipAddressTextField;

    @FXML
    private TextField portTextField;

    /* Methods */

    @FXML
    public void initialize() {
        TextFormatter<Integer> textFormatter = new TextFormatter<>(integerFilter);
        portTextField.setTextFormatter(textFormatter); // portTextField will now accept only integers (or blank string)
    }


    public void setClient(Client client) {
        this.client = client;
    }


    @FXML
    public void handleConnectButton(ActionEvent actionEvent) {
        System.out.println("Clicked");
    }

    @FXML
    public void ipAddressChanged(KeyEvent keyEvent) {
        validateFields();
    }

    @FXML
    public void portChanged(KeyEvent keyEvent) {
        validateFields();
    }

    private void validateFields() {
        if (Utils.isValidIp(ipAddressTextField.getText()) && Utils.isValidPort(Integer.parseInt("0" + portTextField.getText().trim()))) {
            connectButton.setDisable(false);
        }
        else {
            connectButton.setDisable(true);
        }
    }

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.equals("") || newText.matches("([1-9][0-9]{0,4})")) {
            return change;
        }
        return null;
    };
}
