package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.cli.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.function.UnaryOperator;

public class SetupScreenController {
    /* Attributes */

    private Client client;
    private HashMap<Control, Boolean> validationMap = new HashMap<>();

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
        validationMap.put(ipAddressTextField, false);
        validationMap.put(portTextField, false);
    }


    public void setClient(Client client) {
        this.client = client;
    }


    @FXML
    public void handleConnectButton(ActionEvent actionEvent) {
        System.out.println("Clicked");
    }

    @FXML
    public void onEnterIpAddress(ActionEvent actionEvent) {
        portTextField.requestFocus();
    }

    @FXML
    public void onEnterPortText(ActionEvent actionEvent) {
        connectButton.fire();
    }

    @FXML
    public void ipAddressChanged(KeyEvent keyEvent) {
        boolean hasInsertedValidIp = Utils.isValidIp(ipAddressTextField.getText());
        validationMap.put(ipAddressTextField, hasInsertedValidIp);
        if (hasInsertedValidIp) {
            UtilsGUI.removeClassToElement(ipAddressTextField, "error-text");
        } else {
            UtilsGUI.addClassToElement(ipAddressTextField, "error-text");
        }
        validateFields();
    }

    @FXML
     public void portChanged(KeyEvent keyEvent) {
        boolean hasInsertedValidPort = Utils.isValidPort(Integer.parseInt("0" + portTextField.getText().trim()));
        if (hasInsertedValidPort) {
            UtilsGUI.removeClassToElement(portTextField, "error-text");
        } else {
            UtilsGUI.addClassToElement(portTextField, "error-text");
        }
        validationMap.put(portTextField, hasInsertedValidPort);
        validateFields();
    }



    private void validateFields() {
        if (validationMap.values().stream().filter(valid -> valid.equals(Boolean.FALSE)).findFirst().orElse(true)) {
            connectButton.setDisable(false);
        } else {
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
