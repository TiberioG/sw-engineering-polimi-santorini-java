package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.LoginMessage;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.view.cli.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class SetupScreenController extends ScreenController {

    /* Attributes */

    private HashMap<Control, Boolean> validationMap = new HashMap<>();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button connectButton;

    @FXML
    private VBox vBoxForServerProps;

    @FXML
    private TextField ipAddressTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private VBox vBoxForUserProps;

    @FXML
    private TextField usernameTextField;

    @FXML
    private DatePicker birthdayDatePicker;

    @FXML
    private ComboBox<Integer> numOfPlayerComboBox;


    /* Methods */

    @FXML
    public void initialize() {
        TextFormatter<Integer> textFormatter = new TextFormatter<>(integerFilter);
        portTextField.setTextFormatter(textFormatter); // portTextField will now accept only integers (or blank string)
        validationMap.put(ipAddressTextField, false);
        validationMap.put(portTextField, false);

        LocalDate minDate = LocalDate.parse(Configuration.minDate, DateTimeFormatter.ofPattern(Configuration.formatDate));
        LocalDate maxDate = LocalDate.now();
        birthdayDatePicker.setDayCellFactory(d ->
            new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isAfter(maxDate) || item.isBefore(minDate));
        }});

        numOfPlayerComboBox.getItems().addAll(2,3);
    }

    // todo remove me, just for testing
    protected void mockSendConnect() {
        getClient().setServerIP("localhost");
        getClient().setServerPort(Integer.parseInt("1234"));
        getClient().connectToServer();
    }
    // todo remove me, just for testing
    protected void mockSendLogin() {
        String username = (new Date()).toString();
        Date birthday = new Date();
        int numOfPlayers = 2;
        getClient().setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, birthday, numOfPlayers, TypeOfMessage.LOGIN);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getClient().sendToServer(loginMessage);
    }


    @FXML
    public void handleConnectButton(ActionEvent actionEvent) {
        getClient().setServerIP(ipAddressTextField.getText());
        getClient().setServerPort(Integer.parseInt("0" + portTextField.getText().trim()));
        getClient().connectToServer();
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

    public void displayUserForm() {
        vBoxForServerProps.setVisible(false);
        vBoxForUserProps.setVisible(true);
    }

    @FXML
    public void handleSendInfoButton(ActionEvent actionEvent) {
        String username = usernameTextField.getText();

        ZoneId defaultZoneId = ZoneId.systemDefault();
        birthdayDatePicker.setValue(birthdayDatePicker.getConverter().fromString(birthdayDatePicker.getEditor().getText()));
        LocalDate birthdayFromDataPicker = birthdayDatePicker.getValue();
        Date birthday = Date.from(birthdayFromDataPicker.atStartOfDay(defaultZoneId).toInstant());

        Integer numOfPlayers = numOfPlayerComboBox.getValue();

        getClient().setUsername(username);
        LoginMessage loginMessage = new LoginMessage(username, birthday, numOfPlayers, TypeOfMessage.LOGIN);
        getClient().sendToServer(loginMessage);
    }

    public void usernameBusy() {
        Platform.runLater(() -> {
            TilePane r = new TilePane();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Il nome è già utilizzato, inserito un'altro nome!");
            alert.show();
            anchorPane.getChildren().add(r);
            UtilsGUI.addClassToElement(usernameTextField, "error-text");
        });
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
