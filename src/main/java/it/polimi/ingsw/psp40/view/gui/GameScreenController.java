package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.*;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sup3rgiu
 */


public class GameScreenController extends ScreenController {

    /* FXML Attributes */

    @FXML
    public ImageView island_sx;
    @FXML
    public ImageView island_dx;

    @FXML
    private Button rightViewButton;
    @FXML
    private Button leftViewButton;
    @FXML
    private Button topViewButton;

    @FXML
    public Button placeWorkerButton;
    @FXML
    public Button moveButton;

    @FXML
    private Pane myPane_dx;
    @FXML
    private Pane myPane_sx;

    @FXML
    private BorderPane borderPane;

    @FXML
    private AnchorPane rightAnchorPane;
    @FXML
    private AnchorPane leftAnchorPane;


    /* BORDER PANE: CENTER */
    @FXML
    private AnchorPane centerAnchorPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Group islandsGroup;
    @FXML
    private TextArea instructionsTextArea;

    /* Attributes */

    //private  final java.util.Map<Integer, List<Block>> levelsMap_dx = new HashMap<>();
    //private  final java.util.Map<Integer, List<Block>> levelsMap_sx = new HashMap<>();

    private final List<Block> levels_dx = new ArrayList<>();
    private final List<Block> levels_sx = new ArrayList<>();

    private final List<Worker> workers_dx = new ArrayList<>();
    private final List<Worker> workers_sx = new ArrayList<>();

    private Map map_dx;
    private Map map_sx;

    private Worker selectedWorker = null;
    private boolean waiting = false;
    private PhaseType currentPhase = null;
    private boolean shouldPositionWorkers = false;
    private Colors chosenColor = null;

    private ColorAdjust grayscale = new ColorAdjust(0, -1, 0, 0); // grayscale.setSaturation(-1);
    private boolean isMapDisabled = false;

    private List<ImageView> listOfPlayerImage = new ArrayList<>();

    /* Methods */

    @FXML
    public void initialize() {
/*        levelsMap_dx.put(0, new ArrayList<>());
        levelsMap_dx.put(1, new ArrayList<>());
        levelsMap_dx.put(2, new ArrayList<>());
        levelsMap_dx.put(3, new ArrayList<>());
        levelsMap_dx.put(4, new ArrayList<>());

        levelsMap_sx.put(0, new ArrayList<>());
        levelsMap_sx.put(1, new ArrayList<>());
        levelsMap_sx.put(2, new ArrayList<>());
        levelsMap_sx.put(3, new ArrayList<>());
        levelsMap_sx.put(4, new ArrayList<>());*/


        map_dx = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.RIGHT);
        map_sx = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.LEFT);

        myPane_dx.getChildren().add(map_dx);
        myPane_sx.getChildren().add(map_sx);
        myPane_sx.setVisible(false);

        rightAnchorPane.layoutXProperty().addListener((observable, oldValue, newValue) -> { // keep me in position when switch camera from right to left and viceversa
            rightAnchorPane.getChildren().forEach(node -> {
                double fixAnchor = (double) newValue > (double) oldValue ? (double) newValue - (double) oldValue : 0;
                AnchorPane.setRightAnchor(node, 10.0 + fixAnchor);
            });
        });

        disableMap(true); // start with map disabled
    }

    /* METHODS TO HANDLE BLOCKS CLICK */

    public void blockClicked(int x, int y, int z) {
        System.out.println("Clicked: "+ x + ", " + y + ", " + z);
        if(!waiting) {
            if (currentPhase == PhaseType.MOVE_WORKER) {
                moveWorker(x, y, z);
            } else if (currentPhase == PhaseType.BUILD_COMPONENT) {
                build(x, y, z);
            } else if (shouldPositionWorkers && currentPhase == null && !hasEnoughWorkers()) {
                placeWorker(x, y, z);
            }
        }

    }

    public void workerClicked(Worker worker) {
        System.out.println("Worker clicked! " + worker.row + ", " + worker.col + ", " + worker.z);
        if(currentPhase == PhaseType.SELECT_WORKER &&  !waiting) {
            if(worker.ownerUsername.equals(getClient().getUsername())) {
                highlightAvailableWorkersForSelection(false);
                selectedWorker = worker.copy();
                sendToServer(new Message(TypeOfMessage.SELECT_WORKER, selectedWorker.id));
            }
        }
        else if(currentPhase == PhaseType.MOVE_WORKER &&  !waiting) { // handle click on worker if I'm in MOVE_WORKER phase. Useful when workers can be swapped
            moveWorker(worker.row, worker.col, worker.z - 1);
        }
    }

    private void highlightAvailableWorkersForSelection(boolean highlight) {
        highlightAvailableWorkersForSelectionInView(workers_dx, highlight);
        highlightAvailableWorkersForSelectionInView(workers_sx, highlight);
    }

    private void highlightAvailableWorkersForSelectionInView(List<Worker> workers_view, boolean highlight) {
        workers_view.forEach( worker -> {
            if(worker.ownerUsername.equals(getClient().getUsername())) {
                if(highlight) {
                    worker.startSelectionAnimation();
                } else {
                    worker.stopSelectionAnimation();
                }
            }
        });
    }

    /* METHODS TO HANDLE INITIAL POSITIONING OF MY WORKERS */

    protected void setInitialPosition(List<Player> playerList) {
        List<String> colorAlreadyUsed = playerList.stream().flatMap(player -> player.getWorkers().stream()).map(worker -> worker.getColor().toString()).distinct().collect(Collectors.toList());
        List<String> colorsAvailable = Arrays.stream(Colors.allNames()).filter(colorAvailable -> !colorAlreadyUsed.contains(colorAvailable)).collect(Collectors.toList());

        VBox vbButtons = new VBox();
        vbButtons.setSpacing(10);
        vbButtons.setPadding(new Insets(10, 20, 10, 20));
        vbButtons.setPrefWidth(150);

        colorsAvailable.forEach( color -> {
            Button button = new Button(color);
            button.setOnAction(actionEvent ->  {
                stackPane.getChildren().remove(vbButtons);
                disableMap(false);
                chosenColor = Colors.valueOf(color);
                instructionsTextArea.setText("Posiziona i tuoi due worker in delle celle libere");
                highlightAvailableCellsInitialPosition();
            });
            button.setPrefWidth(vbButtons.getPrefWidth());
            vbButtons.getChildren().add(button);
        });

        disableMap(true);
        vbButtons.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(vbButtons);
    }

    private void highlightAvailableCellsInitialPosition() {
        List<Cell> availableCells = Arrays.stream(getClient().getFieldCache()).flatMap(Arrays::stream).collect(Collectors.toList()); // 2-dimensional array to List
        List<Cell> occupiedCells = new ArrayList<>(getClient().getLocationCache().getAllOccupied());
        availableCells.removeIf( cell -> occupiedCells.stream().anyMatch( occupiedCell -> cell.getCoordX() == occupiedCell.getCoordX() && cell.getCoordY() == occupiedCell.getCoordY()));
        highlightAvailableCells(availableCells);
        waiting = false;
        currentPhase = null; // just to be sure
        shouldPositionWorkers = true;
    }


    private void placeWorker(int x, int y, int z) {
        if(z == 0) { // just to be sure, but z != 0 could not happen
            boolean isCellOccupied = getClient().getLocationCache().getAllOccupied().stream().anyMatch( cell -> (cell.getCoordX() == x && cell.getCoordY() == y));
            isCellOccupied = isCellOccupied || workers_dx.stream().anyMatch(worker -> worker.row == x && worker.col == y); // check if I'm not placing one worker over another worker just placed and not yet present in locationCache
            if(!isCellOccupied) {
                int id = (int)workers_dx.stream().filter(worker -> worker.ownerUsername.equals(getClient().getUsername())).count();
                Worker worker = new Worker(x, y, getClient().getUsername(), id, chosenColor);
                addWorkerAndRefresh(worker);
                if(hasEnoughWorkers()) { // send to server the coordinates of my workers
                    List<CoordinatesMessage> workersCoord = new ArrayList<>();
                    workers_dx.stream()
                            .filter(_worker -> _worker.ownerUsername.equals(getClient().getUsername())) // only my workers
                            .forEach(myWorker -> {
                                workersCoord.add(myWorker.id, new CoordinatesMessage(myWorker.row, myWorker.col));
                            });

                    sendToServer(new Message(TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(chosenColor, workersCoord)) );
                }
            } else {
                // todo: u can't place a worker here
            }
        }
    }

    private boolean hasEnoughWorkers() {
        return 2 == workers_dx.stream()
                .filter(worker -> worker.ownerUsername.equals(getClient().getUsername()))
                .count();
    }

    /* METHODS TO HANDLE BUILD PHASE */

    private void build(int x, int y, int z) {

        Cell desiredCell = getClient().getAvailableBuildCells().keySet().stream().filter( cell -> cell.getCoordX() == x && cell.getCoordY() == y).findFirst().orElse(null);
        if(desiredCell == null) {
            // todo: u can't build here
        }
        else {
            List<Integer> listOfAvailableComponents = getClient().getAvailableBuildCells().get(desiredCell); // getting the components buildable in the selected cell
            if(listOfAvailableComponents.size() == 1) {
                Block block = null;
                switch (listOfAvailableComponents.get(0)) {
                    case 1:
                        block = new Level1(x, y);
                        break;
                    case 2:
                        block = new Level2(x, y);
                        break;
                    case 3:
                        block = new Level3(x, y);
                        break;
                    case 4:
                        block = new Dome(x, y);
                        break;
                }

                if(block != null) { // just to be sure
                    addAndRefresh(block);
                    CoordinatesMessage buildCoord = new CoordinatesMessage(x, y);
                    sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.getComponent(listOfAvailableComponents.get(0)), buildCoord)));
                }
            }
            else {
                askWhichComponentBuild(listOfAvailableComponents, x, y, z);
            }
        }
    }

    private void askWhichComponentBuild(List<Integer> listOfAvailableComponents, int x, int y , int z) {
        VBox vbButtons = new VBox();
        vbButtons.setSpacing(10);
        vbButtons.setPadding(new Insets(10, 20, 10, 20));
        vbButtons.setPrefWidth(150);

        listOfAvailableComponents.forEach( componentCode -> {
            Button button = new Button(Component.getName(componentCode));
            button.setOnAction(actionEvent ->  {
                stackPane.getChildren().remove(vbButtons);
                disableMap(false);
                buildComponent(x, y, z, componentCode);
            });
            button.setPrefWidth(vbButtons.getPrefWidth());
            vbButtons.getChildren().add(button);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent ->  {
            stackPane.getChildren().remove(vbButtons);
            disableMap(false);
        });
        cancelButton.setPrefWidth(vbButtons.getPrefWidth());
        vbButtons.getChildren().add(cancelButton);

        disableMap(true);
        vbButtons.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(vbButtons);
    }

    private void buildComponent(int x, int y, int z, int componentCode) {
        Block block = null;
        switch (componentCode) {
            case 1:
                block = new Level1(x, y);
                break;
            case 2:
                block = new Level2(x, y);
                break;
            case 3:
                block = new Level3(x, y);
                break;
            case 4:
                block = new Dome(x, y, z+1);
                break;
        }

        if(block != null) { // just to be sure
            CoordinatesMessage buildCoord = new CoordinatesMessage(x, y);
            sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.getComponent(componentCode), buildCoord)));
            addAndRefresh(block);
        }
    }

    protected void highlightAvailableCellsForBuild() {
        List<Cell> availableCells = new ArrayList<>(getClient().getAvailableBuildCells().keySet());
        highlightAvailableCells(availableCells);
        currentPhase = PhaseType.BUILD_COMPONENT;
        waiting = false;
    }

    /* METHODS TO HANDLE MOVE PHASE */

    private void moveWorker(int x, int y, int z) {
        if(selectedWorker != null) {
            List<Cell> availableCells = getClient().getAvailableMoveCells();
            boolean isAvailableCell = availableCells.stream().anyMatch( cell -> cell.getCoordX() == x && cell.getCoordY() == y);
            if(isAvailableCell) {
                moveSelectedWorkerInView(x, y, z, workers_dx);
                Worker worker = moveSelectedWorkerInView(x, y, z, workers_sx);
                if(worker != null) {
                    worker.moveAnimation.setOnFinished(e -> {
                        selectedWorker = worker.copy(); // update selectedWorker to update position after movement
                        CoordinatesMessage moveCoord = new CoordinatesMessage(x, y);
                        sendToServer(new Message(TypeOfMessage.MOVE_WORKER, moveCoord));
                    });
                }
                refresh();
            }
            else {
                // todo: u can't move here (non dovrebbe poter capitare)
            }
        }
        else {
            // todo: please first select a worker (forse non può capitare)
        }
    }

    private Worker moveSelectedWorkerInView(int x, int y, int z, List<Worker> workers_view) {
        for (Worker worker : workers_view) {
            if (worker.row == selectedWorker.row && worker.col == selectedWorker.col && worker.z == selectedWorker.z) {
                worker.move(x, y, z);
                return worker;
            }
        }
        return null;
    }

    protected void highlightAvailableCellsForMove() {
        List<Cell> availableCells = getClient().getAvailableMoveCells();
        highlightAvailableCells(availableCells);
        currentPhase = PhaseType.MOVE_WORKER;
        waiting = false;
    }

    /* METHODS TO HANDLE PHASE SELECTION */

    protected void askDesiredPhase() {
        List<Phase> phaseList = getClient().getListOfPhasesCache();

        VBox vbButtons = new VBox();
        vbButtons.setSpacing(10);
        //vbButtons.setPadding(new Insets(10, 20, 10, 20));
        vbButtons.setPrefWidth(150);
        vbButtons.setAlignment(Pos.CENTER);

        phaseList.forEach( phase -> {
            Button button = new Button(phase.getType().toString());
            button.setOnAction(actionEvent ->  {
                //stackPane.getChildren().remove(vbButtons);
                rightAnchorPane.getChildren().remove(vbButtons);
                disableMap(false);
                phaseButtonClicked(phase.getType());
            });
            button.setPrefWidth(vbButtons.getPrefWidth());
            vbButtons.getChildren().add(button);
        });

        vbButtons.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        disableMap(true);
        //vbButtons.setAlignment(Pos.TOP_CENTER);
        //stackPane.getChildren().add(vbButtons);

        rightAnchorPane.getChildren().add(vbButtons);
        AnchorPane.setTopAnchor(vbButtons, 10.0);
        AnchorPane.setRightAnchor(vbButtons, 10.0);
    }

    private void phaseButtonClicked(PhaseType selectedPhase) {
        switch (selectedPhase) {
            case SELECT_WORKER:
                waiting = false;
                currentPhase = PhaseType.SELECT_WORKER;
                highlightAvailableWorkersForSelection(true);
                instructionsTextArea.setText("Seleziona su un tuo worker");
                break;
            case MOVE_WORKER:
                currentPhase = PhaseType.MOVE_WORKER;
                instructionsTextArea.setText("Muovi il worker in una delle celle disponibili");
                sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
                break;
            case BUILD_COMPONENT:
                currentPhase = PhaseType.BUILD_COMPONENT;
                instructionsTextArea.setText("Costruisci in una delle celle disponibili");
                sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_BUILD));
                break;
            case END_TURN:
                currentPhase = PhaseType.END_TURN;
                instructionsTextArea.setText("");
                sendToServer(new Message(TypeOfMessage.REQUEST_END_TURN));
                break;
        }
    }

    /* METHODS TO HANDLE END TURN */

    protected void endTurn() {
        selectedWorker = null;
        instructionsTextArea.setText("Aspetta il tuo turno...");
        //disableMap(true); // todo farlo o no?
    }

    /* METHODS TO SHOW PLAYERS INFO */

    protected void setPlayersInfo(List<Player> playerList) {
        VBox myVbox = new VBox();
        VBox enemyVbox = new VBox();
        playerList.forEach(player -> {
            ImageView imageView = new ImageView(new Image(getClass().getResource("/images/characterImage/image-card-" + player.getCurrentCard().getId() + ".png").toString()));
            imageView.setPreserveRatio(true);
            if(player.getName().equals(getClient().getUsername())) {
                imageView.setScaleX(-1); // reflect image
                imageView.setFitWidth(leftAnchorPane.getBoundsInLocal().getWidth() * 0.80);
                //Text myText = getUsernameText(player.getName(), leftAnchorPane, 20.0);
                //myVbox.getChildren().addAll(myText, imageView);
                myVbox.getChildren().addAll(imageView);
            } else {
                imageView.setFitWidth(rightAnchorPane.getBoundsInLocal().getWidth() * 0.65);
                ImageView versusImage = new ImageView(new Image(getClass().getResource("/images/versus.png").toString()));
                versusImage.setPreserveRatio(true);
                versusImage.setFitHeight(35);
                //Text enemyText = getUsernameText(player.getName(), rightAnchorPane, 15.0);
                //enemyVbox.getChildren().addAll(versusImage, enemyText, imageView);
                enemyVbox.getChildren().addAll(versusImage, imageView);
            }
            addHoverHandler(player, imageView);
        });

        myVbox.setSpacing(15);
        myVbox.setAlignment(Pos.CENTER);
        leftAnchorPane.getChildren().add(myVbox);
        AnchorPane.setBottomAnchor(myVbox, 20.0);
        AnchorPane.setLeftAnchor(myVbox, 10.0);

        enemyVbox.setSpacing(15);
        enemyVbox.setAlignment(Pos.CENTER);
        rightAnchorPane.getChildren().add(enemyVbox);
        AnchorPane.setBottomAnchor(enemyVbox, 20.0);
        AnchorPane.setRightAnchor(enemyVbox, 10.0);

    }

    private void addHoverHandler(Player player, ImageView imageView) {
            boolean isRightImage = !player.getName().equals(getClient().getUsername()); // image is in the right pane of the BorderPane
            imageView.setUserData(isRightImage);
            PlayerInfoPopup popup = new PlayerInfoPopup(getPrimaryStage(), player, imageView);

            Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
            Duration duration = Duration.millis(500);
            ScaleTransition st = new ScaleTransition(duration, imageView);
            st.setInterpolator(interpolator);
            st.setToX(Math.signum(imageView.getScaleX()) * 1.1); // keep the signum
            st.setToY(1.1);

            imageView.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    st.play();
                    popup.show();
                } else {
                    st.stop();
                    imageView.scaleXProperty().setValue(Math.signum(imageView.getScaleX()) * 1);
                    imageView.scaleYProperty().setValue(1);
                    popup.hide();
                }
            });

    }

    protected void setPlayersInfo_old(List<Player> playerList) {
        listOfPlayerImage = new ArrayList<>();
        playerList.forEach(player -> {
            ImageView imageView = new ImageView(new Image(getClass().getResource("/images/characterImage/image-card-" + player.getCurrentCard().getId() + ".png").toString()));
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            listOfPlayerImage.add(imageView);
        });

        Text textFirstPlayer = getUsernameText(playerList.get(0).getName(), rightAnchorPane, 20.0);
        VBox vBoxFirstPlayer = new VBox(textFirstPlayer, listOfPlayerImage.get(0));
        vBoxFirstPlayer.setSpacing(15);
        vBoxFirstPlayer.setAlignment(Pos.CENTER);
        rightAnchorPane.getChildren().add(vBoxFirstPlayer);
        AnchorPane.setBottomAnchor(vBoxFirstPlayer, 10.0);
        AnchorPane.setRightAnchor(vBoxFirstPlayer, 10.0);

        Text textSecondPlayer = getUsernameText(playerList.get(1).getName(), leftAnchorPane, 20.0);
        VBox vBoxSecondPlayer = new VBox(textSecondPlayer, listOfPlayerImage.get(1));
        vBoxSecondPlayer.setSpacing(15);
        vBoxSecondPlayer.setAlignment(Pos.CENTER);
        leftAnchorPane.getChildren().add(vBoxSecondPlayer);
        AnchorPane.setBottomAnchor(vBoxSecondPlayer, 10.0);
        AnchorPane.setLeftAnchor(vBoxSecondPlayer, 10.0);

        if (listOfPlayerImage.size() == 3) {
            Text textThirdPlayer = getUsernameText(playerList.get(2).getName(), leftAnchorPane, 20.0);
            VBox vBoxThirdPlayer = new VBox(listOfPlayerImage.get(2), textThirdPlayer);
            vBoxThirdPlayer.setSpacing(15);
            vBoxThirdPlayer.setAlignment(Pos.CENTER);
            vBoxThirdPlayer.setPadding(new Insets(0, 0, 0, 10));
            leftAnchorPane.getChildren().add(vBoxThirdPlayer);
            AnchorPane.setTopAnchor(vBoxThirdPlayer, 10.0);
            AnchorPane.setRightAnchor(vBoxFirstPlayer, 10.0);
        }
    }

    private Text getUsernameText(String username, AnchorPane pane, Double fontSize) {
        Label label = new Label(username);
        label.setFont(new Font(fontSize));
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        text.setTextAlignment(TextAlignment.CENTER);

        double textWidth = text.getBoundsInLocal().getWidth();
        double maxTextWidth = pane.getBoundsInLocal().getWidth() * 0.90;
        if(textWidth > maxTextWidth) { // if username is too long, truncate it
            double estimatedWidthForChar = textWidth / text.getText().length();
            int charsToRemove = (int) ((textWidth - maxTextWidth) / estimatedWidthForChar);
            String truncatedUsername = username.substring(0, username.length() - charsToRemove - 1);
            truncatedUsername += "...";
            text.setText(truncatedUsername);
        }

        return text;
    }

    /* HELPER METHODS */

    private void highlightAvailableCells(List<Cell> availableCells) {
        highlightAvailableCellsInView(availableCells, levels_dx);
        highlightAvailableCellsInView(availableCells, map_dx.getTiles());
        highlightAvailableCellsInView(availableCells, levels_sx);
        highlightAvailableCellsInView(availableCells, map_sx.getTiles());
    }

    private void highlightAvailableCellsInView(List<Cell> availableCells, List<Block> view) {
        availableCells.forEach( cell -> {
            view.forEach(block -> {
                if((block.row == cell.getCoordX() && block.col == cell.getCoordY())) {
                    block.highlightBlock();
                }
            });
        });
    }

    private void restoreHighlight() {
        restoreHighlightInView(levels_dx);
        restoreHighlightInView(map_dx.getTiles());
        restoreHighlightInView(levels_sx);
        restoreHighlightInView(map_sx.getTiles());
    }

    private void restoreHighlightInView(List<Block> view) {
        view.forEach(block -> disableBlock(block, false));
    }

    //private Location tmpLocation = null;

    protected void updateWorkersPosition() {
        //tmpLocation = getClient().getLocationCache().copy(); // this helps when multiple locationUpdate arrive in short time
        updateWorkersInView(workers_dx);
        updateWorkersInView(workers_sx);
        getClient().clearModifiedWorkersCache();
        refresh();
    }

    private void updateWorkersInView(List<Worker> workers_view) {
        //Location location = getClient().getLocationCache();

        new ArrayList<>(getClient().getModifiedWorkersCache()).forEach( worker -> {
            Cell newCell = cellOfModifiedWorker(worker);
            Worker worker_view = modifiedWorkerInView(worker, workers_view);
            if(newCell != null) {
                if (worker_view != null) {
                    worker_view.move(newCell.getCoordX(), newCell.getCoordY(), newCell.getTower().getTopComponent().getComponentCode(), false);
                } else { // this happens when the game starts and different players are positioning their workers
                    Worker newWorker = new Worker(newCell.getCoordX(), newCell.getCoordY(), worker.getPlayerName(), worker.getId(), worker.getColor());
                    addWorker(newWorker);
                }
            }
        });
    }

    private Cell cellOfModifiedWorker(it.polimi.ingsw.psp40.model.Worker worker) {
        // return location.getLocation(worker); // non posso fare così perchè la serializzazione con GSON non preserve le reference
        Location tmpLocation = getClient().getLocationCache();
        return tmpLocation.getAllOccupied().stream()
                .filter(cell -> tmpLocation.getOccupant(cell).getPlayerName().equals(worker.getPlayerName()) && tmpLocation.getOccupant(cell).getId() == worker.getId())
                .findFirst().orElse(null);
    }

    private Worker modifiedWorkerInView(it.polimi.ingsw.psp40.model.Worker modifiedWorker, List<Worker> workers_view) {
        return workers_view.stream().filter( worker -> modifiedWorker.getPlayerName().equals(worker.ownerUsername) && modifiedWorker.getId() == worker.id).findFirst().orElse(null);
    }

    // chiamato quando si riceve un'aggiornamento dell'isola
    // todo teoricamente vuol dire che è stato modificato il top component. Controllo più approfondito?
    protected void updateCell(Cell cell) {
        int x = cell.getCoordX();
        int y = cell.getCoordY();
        Block block = null;
        switch (cell.getTower().getTopComponent().getComponentCode()) {
            case 1:
                block = new Level1(x, y);
                break;

            case 2:
                block = new Level2(x, y);
                break;

            case 3:
                block = new Level3(x, y);
                break;

            case 4:
                block = new Dome(x, y, cell.getTower().getComponents().size()-1);
                break;
        }

        if(block != null) {
            addAndRefresh(block);
        }
    }

    protected void updateWholeIsland() {
        clearBoard();

        Location location = getClient().getLocationCache();
        Cell[][] field = getClient().getFieldCache();

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                Tower tower = field[i][j].getTower();
                List<Component> components = tower.getComponents();
                for(int k = 0; k < components.size(); k++) {
                    switch (components.get(k).getComponentCode()) {
                        case 1:
                            addBlock(new Level1(i,j));
                            break;

                        case 2:
                            addBlock(new Level2(i,j));
                            break;

                        case 3:
                            addBlock(new Level3(i,j));
                            break;

                        case 4:
                            addBlock(new Dome(i,j,k));
                    }
                }

                it.polimi.ingsw.psp40.model.Worker occupant = location.getOccupant(i, j);
                if (occupant != null) {
                    Worker worker = new Worker(i,j, occupant.getPlayerName(), occupant.getId(), occupant.getColor());
                    worker.move(i,j, tower.getComponents().size()-1, false);
                    addWorker(worker);
                }
            }
        }
        refresh();
    }

    private void disableMap(boolean disable) {
        if(isMapDisabled != disable) {
            isMapDisabled = disable;
            islandsGroup.setDisable(disable);
            if (disable) {
                islandsGroup.setEffect(grayscale);
            } else {
                islandsGroup.setEffect(null);
            }
        }
    }

    private void disableBlock(Block block, boolean disable) {
        block.setDisable(disable);
        if(disable) {
            block.setBlockEffect(grayscale);
        } else {
            block.setBlockEffect(null);
        }
    }

    private void addBlock(Block block) {
        //levelsMap_dx.get(block.z).add(block);
        levels_dx.add(block);

        Block block_sx = block.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        //levelsMap_sx.get(block.z).add(block_sx);
        levels_sx.add(block_sx);
    }

    private void addWorker(Worker worker) {
        //levelsMap_dx.get(block.z).add(block);
        levels_dx.add(worker);
        workers_dx.add(worker);

        Worker worker_sx = worker.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        //levelsMap_sx.get(block.z).add(block_sx);
        levels_sx.add(worker_sx);
        workers_sx.add(worker_sx);
    }

    private void addWorkerAndRefresh(Worker worker) {
        addWorker(worker);
        refresh();
    }

    private void addAndRefresh(Block block) {
        addBlock(block);
        refresh();
    }

    private void refresh() {
        reorderLevels(levels_dx);
        reorderLevels(levels_sx);

        map_dx.getChildren().removeAll(levels_dx);
        map_dx.getChildren().addAll(levels_dx);

        map_sx.getChildren().removeAll(levels_sx);
        map_sx.getChildren().addAll(levels_sx);
    }

    private void reorderLevels(List<Block> levels) {
        levels.sort((b1, b2) -> {
            if(b1.currentCamera == b2.currentCamera) { // just to be sure, but "levels" list should contains only elements with the same CameraType
                int b1_sum = 0;
                int b2_sum = 0;
                // calculates view order priority depending on the CameraType
                if(b1.currentCamera == GUIProperties.CameraType.RIGHT) {
                    b1_sum = b1.row + b1.col + b1.z;
                    b2_sum = b2.row + b2.col + b2.z;
                }
                else if(b1.currentCamera == GUIProperties.CameraType.LEFT) {
                    b1_sum = GUIProperties.getCorrespondingLeftRow(b1.row, b1.col) + GUIProperties.getCorrespondingLeftCol(b1.row, b1.col) + b1.z;
                    b2_sum = GUIProperties.getCorrespondingLeftRow(b2.row, b2.col) + GUIProperties.getCorrespondingLeftCol(b2.row, b2.col) + b2.z;
                } else if(b1.currentCamera == GUIProperties.CameraType.TOP){
                    // todo
                }

                if(b1_sum > b2_sum) {
                    b1.toFront();
                    b2.toBack();
                    return 1;
                } else if (b1_sum < b2_sum) {
                    b2.toFront();
                    b1.toBack();
                    return -1;
                }
                return Integer.compare(b1_sum, b2_sum);
            }
            return 0;
        });
    }

    private void sendToServer(Message message) {
        getClient().sendToServer(message);
        waiting = true;

        if(    message.getTypeOfMessage() == TypeOfMessage.BUILD_CELL
                || message.getTypeOfMessage() == TypeOfMessage.MOVE_WORKER
                || message.getTypeOfMessage() == TypeOfMessage.SET_POSITION_OF_WORKER) {
            restoreHighlight();
        }
    }

    private void clearBoard() {
        map_dx.getChildren().removeAll(levels_dx);
        map_sx.getChildren().removeAll(levels_sx);
        levels_dx.clear();
        levels_sx.clear();
    }

    @FXML
    public void rightViewButtonClicked(ActionEvent actionEvent) {
        myPane_sx.setVisible(false);
        island_sx.setVisible(false);
        island_dx.setVisible(true);
        myPane_dx.setVisible(true);
    }

    @FXML
    public void leftViewButtonClicked(ActionEvent actionEvent) {
        myPane_dx.setVisible(false);
        island_dx.setVisible(false);
        island_sx.setVisible(true);
        myPane_sx.setVisible(true);
    }

    @FXML
    public void topViewButtonClicked(ActionEvent actionEvent) {
        // todo
    }
}
