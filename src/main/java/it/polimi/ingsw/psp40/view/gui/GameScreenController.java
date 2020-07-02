package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.*;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
    public ImageView island_top;

    @FXML
    private Button rightViewButton;
    @FXML
    private Button leftViewButton;
    @FXML
    private Button topViewButton;

    @FXML
    private Pane myPane_dx;
    @FXML
    private Pane myPane_sx;
    @FXML
    private Pane myPane_top;

    @FXML
    private BorderPane borderPane;
    @FXML
    private AnchorPane rightAnchorPane;
    @FXML
    private AnchorPane leftAnchorPane;

    /* BORDER PANE: RIGHT */
    @FXML
    private HBox hboxPhases;
    @FXML
    private Label instructionsLabel;
    @FXML
    private ImageView helpBoardImageView;


    /* BORDER PANE: CENTER */
    @FXML
    private AnchorPane centerAnchorPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Group islandsGroup;

    /* Attributes */

    private final List<Block> levels_dx = new ArrayList<>();
    private final List<Block> levels_sx = new ArrayList<>();
    private final List<Block> levels_top = new ArrayList<>();

    private final List<Worker> workers_dx = new ArrayList<>();
    private final List<Worker> workers_sx = new ArrayList<>();
    private final List<Worker> workers_top = new ArrayList<>();

    private Map map_dx;
    private Map map_sx;
    private Map map_top;

    private Worker selectedWorker = null;
    private boolean waiting = false;
    private List<PhaseType> selectedPhases = new ArrayList<>();
    private boolean shouldPositionWorkers = false;
    private Colors chosenColor = null;

    private final ColorAdjust grayscale = new ColorAdjust(0, -1, 0, 0); // equivalent to grayscale.setSaturation(-1);
    private boolean isMapDisabled = false;

    private ConfirmPopup confirmPopup = null;

    private Transition instructionsLabelTransition = null;

    private Background background_dx = null;
    private Background background_sx = null;
    private Background background_top = null;

    private VBox myVbox = null;
    private VBox enemyVbox = null;
    private HBox enemyHbox = null;

    private GUIProperties.CameraType currentCamera = GUIProperties.CameraType.RIGHT;  // default camera
    private final BooleanProperty isRightCamera = new SimpleBooleanProperty(true); // default camera
    private final BooleanProperty isLeftCamera = new SimpleBooleanProperty(false);
    private final BooleanProperty isTopCamera = new SimpleBooleanProperty(false);

    /* Methods */

    @FXML
    public void initialize() {

        map_dx = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.RIGHT);
        map_sx = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.LEFT);
        map_top = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.TOP);

        myPane_dx.getChildren().add(map_dx);
        myPane_sx.getChildren().add(map_sx);
        myPane_top.getChildren().add(map_top);
        myPane_sx.setVisible(false);
        myPane_top.setVisible(false);

        UtilsGUI.buttonHoverEffectWithPersistence(leftViewButton, isLeftCamera);
        UtilsGUI.buttonHoverEffectWithPersistence(rightViewButton, isRightCamera);
        UtilsGUI.buttonHoverEffectWithPersistence(topViewButton, isTopCamera);

        buildInstructionsLabelTransition();

/*        rightAnchorPane.layoutXProperty().addListener((observable, oldValue, newValue) -> { // keep rightAnchorPane's elements in position when switch camera from right to left and viceversa
            rightAnchorPane.getChildren().forEach(node -> {
                double fixAnchor = (double) newValue > (double) oldValue ? (double) newValue - (double) oldValue : 0;
                AnchorPane.setRightAnchor(node, 10.0 + fixAnchor);
            });
        });*/

        disableMap(true); // start with map disabled
        setInstructionsLabelText("Wait for your turn...");
        buildBackgrounds();

    }

    /* METHODS TO HANDLE BLOCKS CLICK */

    /**
     * Method called when a {@link Block} is clicked. Handles the action to be performed depending on the current phase
     *
     * @param x Coordinate X of the block
     * @param y Coordinate Y of the block
     * @param z Coordinate Z of the block
     */
    public void blockClicked(int x, int y, int z) {
        //System.out.println("Clicked: " + x + ", " + y + ", " + z);
        if (!waiting) {
            if (checkLastSelectedPhase(PhaseType.MOVE_WORKER)) {
                moveWorker(x, y, z);
            } else if (checkLastSelectedPhase(PhaseType.BUILD_COMPONENT)) {
                build(x, y, z);
            } else if (shouldPositionWorkers && selectedPhases.size() == 0 && !hasEnoughWorkers()) {
                placeWorker(x, y, z);
            }
        }

    }

    /**
     * Method called when a {@link Worker} is clicked. Handles the action to be performed depending on the current phase
     *
     * @param worker worker clicked
     */
    public void workerClicked(Worker worker) {
        //System.out.println("Worker clicked! " + worker.row + ", " + worker.col + ", " + worker.z);
        if (checkLastSelectedPhase(PhaseType.SELECT_WORKER) && !waiting) {
            if (worker.ownerUsername.equals(getClient().getUsername())) {
                highlightAvailableWorkersForSelection(false);
                selectedWorker = worker.copy();
                sendToServer(new Message(TypeOfMessage.SELECT_WORKER, selectedWorker.id));
            }
        } else if (checkLastSelectedPhase(PhaseType.MOVE_WORKER) && !waiting) { // handle click on worker if I'm in MOVE_WORKER phase. Useful when workers can be swapped
            moveWorker(worker.row, worker.col, worker.z - 1);
        }
    }

    /**
     * Highlights available {@link Worker}s for selection phase
     *
     * @param highlight true to highlight, false to remove highlight
     */
    private void highlightAvailableWorkersForSelection(boolean highlight) {
        highlightAvailableWorkersForSelectionInView(workers_dx, highlight);
        highlightAvailableWorkersForSelectionInView(workers_sx, highlight);
        highlightAvailableWorkersForSelectionInView(workers_top, highlight);
    }

    private void highlightAvailableWorkersForSelectionInView(List<Worker> workers_view, boolean highlight) {
        workers_view.forEach(worker -> {
            if (worker.ownerUsername.equals(getClient().getUsername())) {
                if (highlight) {
                    worker.startSelectionAnimation();
                } else {
                    worker.stopSelectionAnimation();
                }
            }
        });
    }

    /* METHODS TO HANDLE INITIAL POSITIONING OF MY WORKERS */

    /**
     * Asks to the player the color of its workers and where place them at the start of the game
     *
     * @param playerList list of the Players in the match
     */
    protected void setInitialPosition(List<Player> playerList) {
        List<String> colorAlreadyUsed = playerList.stream().flatMap(player -> player.getWorkers().stream()).map(worker -> worker.getColor().toString()).distinct().collect(Collectors.toList());
        List<String> colorsAvailable = Arrays.stream(Colors.allNames()).filter(colorAvailable -> !colorAlreadyUsed.contains(colorAvailable)).collect(Collectors.toList());

        VBox vbButtons = new VBox();
        vbButtons.setSpacing(5);
        vbButtons.setPadding(new Insets(10, 20, 10, 20));
        vbButtons.setPrefWidth(150);
        vbButtons.setTranslateY(50);

        colorsAvailable.forEach(color -> {
            Button button = new Button(color);
            button.setOnAction(actionEvent -> {
                stackPane.getChildren().remove(vbButtons);
                disableMap(false);
                chosenColor = Colors.valueOf(color);
                setInstructionsLabelText("Place your two workers in free cells");
                highlightAvailableCellsInitialPosition();
            });
            UtilsGUI.buttonHoverEffect(button);
            button.setPrefWidth(vbButtons.getPrefWidth());
            vbButtons.getChildren().add(button);
        });

        disableMap(true);
        vbButtons.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(vbButtons);

        setInstructionsLabelText("Select the color of your workers");
    }

    /**
     * Highlights available cells where to place workers at the start of the game
     */
    private void highlightAvailableCellsInitialPosition() {
        List<Cell> availableCells = Arrays.stream(getClient().getFieldCache()).flatMap(Arrays::stream).collect(Collectors.toList()); // 2-dimensional array to List
        List<Cell> occupiedCells = new ArrayList<>(getClient().getLocationCache().getAllOccupied());
        availableCells.removeIf(cell -> occupiedCells.stream().anyMatch(occupiedCell -> cell.getCoordX() == occupiedCell.getCoordX() && cell.getCoordY() == occupiedCell.getCoordY()));
        highlightAvailableCells(availableCells);
        waiting = false;
        selectedPhases = new ArrayList<>(); // just to be sure
        shouldPositionWorkers = true;
    }

    /**
     * Adds a {@link Worker} to the map in the given position
     *
     * @param x Coordinate X where to place the worker
     * @param y Coordinate Y where to place the worker
     * @param z Coordinate Z where to place the worker
     */
    private void placeWorker(int x, int y, int z) {
        if (z == 0) { // just to be sure, but z != 0 could not happen
            boolean isCellOccupied = getClient().getLocationCache().getAllOccupied().stream().anyMatch(cell -> (cell.getCoordX() == x && cell.getCoordY() == y));
            isCellOccupied = isCellOccupied || workers_dx.stream().anyMatch(worker -> worker.row == x && worker.col == y); // check if I'm not placing one worker over another worker just placed and not yet present in locationCache
            if (!isCellOccupied) {
                int id = (int) workers_dx.stream().filter(worker -> worker.ownerUsername.equals(getClient().getUsername())).count();
                Worker worker = new Worker(x, y, getClient().getUsername(), id, chosenColor);
                addWorkerAndRefresh(worker);
                if (hasEnoughWorkers()) { // send to server the coordinates of my workers
                    List<CoordinatesMessage> workersCoord = new ArrayList<>();
                    workers_dx.stream()
                            .filter(_worker -> _worker.ownerUsername.equals(getClient().getUsername())) // only my workers
                            .forEach(myWorker -> {
                                workersCoord.add(myWorker.id, new CoordinatesMessage(myWorker.row, myWorker.col));
                            });

                    sendToServer(new Message(TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(chosenColor, workersCoord)));

                    setInstructionsLabelText("Wait for your turn...");
                }
            } else {
                System.out.println("You can't place a worker here");
            }
        }
    }

    /**
     * Method used to check if the player has placed enough workers
     *
     * @return true if player has placed enough workers
     */
    private boolean hasEnoughWorkers() {
        return 2 == workers_dx.stream()
                .filter(worker -> worker.ownerUsername.equals(getClient().getUsername()))
                .count();
    }

    /* METHODS TO HANDLE BUILD PHASE */

    /**
     * Tries to build a {@link Block} in the given position. If position is not allowed, does nothing. If more than one component is available, asks which build
     *
     * @param x Coordinate X where to build
     * @param y Coordinate Y where to build
     * @param z Coordinate Z where to build
     */
    private void build(int x, int y, int z) {

        Cell desiredCell = getClient().getAvailableBuildCells().keySet().stream().filter(cell -> cell.getCoordX() == x && cell.getCoordY() == y).findFirst().orElse(null);
        if (desiredCell == null) {
            System.out.println("You can't build here");
        } else {
            List<Integer> listOfAvailableComponents = getClient().getAvailableBuildCells().get(desiredCell); // getting the components buildable in the selected cell
            if (listOfAvailableComponents.size() == 1) {
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

                if (block != null) { // just to be sure
                    addAndRefresh(block);
                    CoordinatesMessage buildCoord = new CoordinatesMessage(x, y);
                    sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.getComponent(listOfAvailableComponents.get(0)), buildCoord)));
                }
            } else {
                askWhichComponentBuild(listOfAvailableComponents, x, y, z);
            }
        }
    }

    /**
     * Asks to the player which component build in the given position
     *
     * @param listOfAvailableComponents list of available components
     * @param x                         Coordinate X where to build
     * @param y                         Coordinate Y where to build
     * @param z                         Coordinate Z where to build
     */
    private void askWhichComponentBuild(List<Integer> listOfAvailableComponents, int x, int y, int z) {
        VBox vbButtons = new VBox();
        vbButtons.setSpacing(10);
        vbButtons.setPadding(new Insets(10, 20, 10, 20));
        vbButtons.setPrefWidth(150);

        listOfAvailableComponents.forEach(componentCode -> {
            Button button = new Button(Component.getName(componentCode));
            button.setOnAction(actionEvent -> {
                stackPane.getChildren().remove(vbButtons);
                disableMap(false);
                buildComponent(x, y, z, componentCode);
            });
            button.setPrefWidth(vbButtons.getPrefWidth());
            vbButtons.getChildren().add(button);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent -> {
            stackPane.getChildren().remove(vbButtons);
            disableMap(false);
        });
        cancelButton.setPrefWidth(vbButtons.getPrefWidth());
        vbButtons.getChildren().add(cancelButton);

        disableMap(true);
        vbButtons.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(vbButtons);
    }

    /**
     * Builds the given component in the given position
     *
     * @param x             Coordinate X where to build
     * @param y             Coordinate Y where to build
     * @param z             Coordinate Z where to build
     * @param componentCode code of the {@link Component} to build
     */
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
                block = new Dome(x, y, z + 1);
                break;
        }

        if (block != null) { // just to be sure
            CoordinatesMessage buildCoord = new CoordinatesMessage(x, y);
            sendToServer(new Message(TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.getComponent(componentCode), buildCoord)));
            addAndRefresh(block);
        }
    }

    /**
     * Highlights available cells where to build a block
     */
    protected void highlightAvailableCellsForBuild() {
        List<Cell> availableCells = new ArrayList<>(getClient().getAvailableBuildCells().keySet());
        if (availableCells.size() > 0) {
            highlightAvailableCells(availableCells);
            waiting = false;
        } else {
            confirmPopup = new ConfirmPopup(getPrimaryStage(), "There are no cells available to build at this stage! Select another phase.\n", () -> {
                confirmPopup.hide();
                GUI.deletePopup();
                selectedPhases.remove(selectedPhases.size() - 1);
                askDesiredPhase();
            });
            GUI.showPopup(confirmPopup, 2);
        }
    }

    /* METHODS TO HANDLE MOVE PHASE */

    /**
     * Moves the selected worker in the given position
     *
     * @param x Coordinate X where to move the worker
     * @param y Coordinate Y where to move the worker
     * @param z Coordinate Z where to move the worker
     */
    private void moveWorker(int x, int y, int z) {
        if (selectedWorker != null) {
            List<Cell> availableCells = getClient().getAvailableMoveCells();
            boolean isAvailableCell = availableCells.stream().anyMatch(cell -> cell.getCoordX() == x && cell.getCoordY() == y);
            if (isAvailableCell) {
                moveSelectedWorkerInView(x, y, z, workers_dx);
                moveSelectedWorkerInView(x, y, z, workers_top);
                Worker worker = moveSelectedWorkerInView(x, y, z, workers_sx);
                if (worker != null) {
                    worker.moveAnimation.setOnFinished(e -> {
                        selectedWorker = worker.copy(); // update selectedWorker to update position after movement
                        CoordinatesMessage moveCoord = new CoordinatesMessage(x, y);
                        sendToServer(new Message(TypeOfMessage.MOVE_WORKER, moveCoord));
                    });
                }
                refresh();
            } else {
                System.out.println("You can't move here");
            }
        } else {
            System.out.println("Please first select a worker");
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

    /**
     * Highlights available cells where to move the selected worker
     */
    protected void highlightAvailableCellsForMove() {
        List<Cell> availableCells = getClient().getAvailableMoveCells();
        if (availableCells.size() > 0) {
            highlightAvailableCells(availableCells);
            waiting = false;
        } else {
            boolean moveAfterSelectWorker = selectedPhases.size() == 2;
            String text = "There are no cells available to move at this stage! Select another" + (moveAfterSelectWorker ? "  worker." : " phase");
            confirmPopup = new ConfirmPopup(getPrimaryStage(), text, () -> {
                confirmPopup.hide();
                GUI.deletePopup();
                if (moveAfterSelectWorker) {
                    selectedPhases = new ArrayList<>();
                    phaseButtonClicked(PhaseType.SELECT_WORKER);
                } else {
                    selectedPhases.remove(selectedPhases.size() - 1);
                    askDesiredPhase();
                }
            });
            GUI.showPopup(confirmPopup, 2);
        }

    }

    /* METHODS TO HANDLE PHASE SELECTION */

    /**
     * Asks to the player which phase choose
     */
    protected void askDesiredPhase() {
        List<Phase> phaseList = getClient().getListOfPhasesCache();
        if (phaseList.size() > 1) {
            double phaseViewWidth = 150;

            hboxPhases.getChildren().clear();
            hboxPhases.setSpacing(10);
            hboxPhases.setPadding(new Insets(10, 10, 10, 10));
            hboxPhases.setPrefWidth(phaseList.size() * phaseViewWidth + hboxPhases.getInsets().getLeft() + hboxPhases.getInsets().getRight() + hboxPhases.getSpacing() * (phaseList.size() - 1));
            hboxPhases.setAlignment(Pos.CENTER);

            phaseList.forEach(phase -> {
                ImageView phaseView = new ImageView(GUIProperties.getImageForPhase(phase));
                phaseView.setPreserveRatio(true);
                phaseView.setFitWidth(phaseViewWidth);
                phaseView.setSmooth(true);
                phaseView.setCache(true);
                UtilsGUI.nodeHoverEffect(phaseView);
                phaseView.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                    hboxPhases.setVisible(false);
                    disableMap(false);
                    phaseButtonClicked(phase.getType());
                });
                hboxPhases.getChildren().add(phaseView);
            });

            disableMap(true);
            hboxPhases.setVisible(true);

            setInstructionsLabelText("Select the desired action");

        } else {
            disableMap(false);
            phaseButtonClicked(phaseList.get(0).getType());
        }
    }

    /**
     * Handles the selection of a phase
     *
     * @param selectedPhase phase chosen by the player
     */
    private void phaseButtonClicked(PhaseType selectedPhase) {
        switch (selectedPhase) {
            case SELECT_WORKER:
                waiting = false;
                selectedPhases.add(PhaseType.SELECT_WORKER);
                highlightAvailableWorkersForSelection(true);
                setInstructionsLabelText("Select a worker");
                break;
            case MOVE_WORKER:
                selectedPhases.add(PhaseType.MOVE_WORKER);
                setInstructionsLabelText("Move the worker to one of the available cells");
                sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
                break;
            case BUILD_COMPONENT:
                selectedPhases.add(PhaseType.BUILD_COMPONENT);
                setInstructionsLabelText("Build in one of the available cells");
                sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_BUILD));
                break;
            case END_TURN:
                selectedPhases.add(PhaseType.END_TURN);
                setInstructionsLabelText("");
                sendToServer(new Message(TypeOfMessage.REQUEST_END_TURN));
                break;
        }
    }

    /* METHODS TO HANDLE END TURN */

    /**
     * Put the status of the game as "turn ended"
     */
    protected void endTurn() {
        selectedWorker = null;
        selectedPhases = new ArrayList<>();
        setInstructionsLabelText("Wait your turn...");
    }

    /* METHODS TO SHOW PLAYERS INFO */

    private double initialWidth_rightAnchorPane = 0;
    private double initialWidth_leftAnchorPane = 0;

    /**
     * Adds to the view all the info about the players in the match
     *
     * @param playerList list of players in the match
     */
    protected void setPlayersInfo(List<Player> playerList) {
        if (enemyHbox != null) { // check if we are updating playersInfo. In that case, update it and return
            boolean playerHasBeenRemoved = computeDifferencesAndRemovePlayersInfoIfNeeded(playerList);
            if (playerHasBeenRemoved)
                return;
        }

        myVbox = new VBox();
        enemyVbox = new VBox();
        enemyHbox = new HBox();

        ImageView versusImage = new ImageView(new Image(getClass().getResource("/images/versus.png").toString()));
        versusImage.setPreserveRatio(true);
        versusImage.setFitHeight(45);
        enemyVbox.getChildren().add(versusImage);

        if (initialWidth_leftAnchorPane == 0)
            initialWidth_leftAnchorPane = leftAnchorPane.getWidth();
        if (initialWidth_rightAnchorPane == 0)
            initialWidth_rightAnchorPane = rightAnchorPane.getWidth();

        playerList.forEach(player -> {
            ImageView imageView = new ImageView(new Image(getClass().getResource("/images/characterImage/image-card-" + player.getCurrentCard().getId() + ".png").toString()));
            imageView.setPreserveRatio(true);
            if (player.getName().equals(getClient().getUsername())) {
                imageView.setScaleX(-1); // reflect image
                imageView.setFitWidth(initialWidth_leftAnchorPane * 0.80);
                imageView.setUserData(player.getName());
                myVbox.getChildren().addAll(imageView);
            } else {
                imageView.setFitWidth(initialWidth_rightAnchorPane * 0.65);
                imageView.setUserData(player.getName());
                enemyHbox.getChildren().addAll(imageView);
            }
            addHoverHandler(player, imageView);
        });


        myVbox.setSpacing(15);
        myVbox.setAlignment(Pos.CENTER);
        leftAnchorPane.getChildren().add(myVbox);
        AnchorPane.setBottomAnchor(myVbox, 20.0);
        AnchorPane.setLeftAnchor(myVbox, 10.0);

        enemyHbox.setSpacing(10);
        enemyHbox.setAlignment(Pos.BOTTOM_CENTER);
        enemyVbox.setSpacing(15);
        enemyVbox.setAlignment(Pos.CENTER);
        enemyVbox.getChildren().add(enemyHbox);
        rightAnchorPane.getChildren().add(enemyVbox);
        AnchorPane.setBottomAnchor(enemyVbox, 20.0);
        //AnchorPane.setRightAnchor(enemyVbox, 10.0);
        double hboxWidth = enemyHbox.getChildrenUnmodifiable().stream().map(node -> node.boundsInLocalProperty().getValue().getWidth()).reduce(0.0, Double::sum); // sum the width of all children elements
        hboxWidth += enemyHbox.getSpacing() * (enemyHbox.getChildrenUnmodifiable().size() - 1) + 10;  // add the spacing value to the width + extra spacing
        enemyVbox.setTranslateX(initialWidth_rightAnchorPane - hboxWidth);
    }

    private boolean computeDifferencesAndRemovePlayersInfoIfNeeded(List<Player> playerList) {
        AtomicBoolean playerHasBeenRemoved = new AtomicBoolean(false);

        List<String> playersNames = playerList.stream().map(Player::getName).collect(Collectors.toList());
        enemyHbox.getChildrenUnmodifiable().forEach(node -> {
            if (!playersNames.contains(node.getUserData())) {
                enemyHbox.getChildren().remove(node);
                playerHasBeenRemoved.set(true);
            }
        });

        // update vbox position
        if (playerHasBeenRemoved.get()) {
            double hboxWidth = enemyHbox.getChildrenUnmodifiable().stream().map(node -> node.boundsInLocalProperty().getValue().getWidth()).reduce(0.0, Double::sum); // sum the width of all children elements
            hboxWidth += enemyHbox.getSpacing() * (enemyHbox.getChildrenUnmodifiable().size() - 1) + 10;  // add the spacing value to the width + extra spacing
            enemyVbox.setTranslateX(initialWidth_rightAnchorPane - hboxWidth);
        }

        return playerHasBeenRemoved.get();
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

    /* HELPER METHODS */

    private void highlightAvailableCells(List<Cell> availableCells) {
        highlightAvailableCellsInView(availableCells, levels_dx);
        highlightAvailableCellsInView(availableCells, map_dx.getTiles());
        highlightAvailableCellsInView(availableCells, levels_sx);
        highlightAvailableCellsInView(availableCells, map_sx.getTiles());
        highlightAvailableCellsInView(availableCells, levels_top);
        highlightAvailableCellsInView(availableCells, map_top.getTiles());
    }

    private void highlightAvailableCellsInView(List<Cell> availableCells, List<Block> view) {
        availableCells.forEach(cell -> {
            view.forEach(block -> {
                if ((block.row == cell.getCoordX() && block.col == cell.getCoordY())) {
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
        restoreHighlightInView(levels_top);
        restoreHighlightInView(map_top.getTiles());
    }

    private void restoreHighlightInView(List<Block> view) {
        view.forEach(block -> disableBlock(block, false));
    }

    //private Location tmpLocation = null;

    /**
     * Updates the position of the workers using the cache updated by the server
     */
    protected void updateWorkersPosition() {
        //tmpLocation = getClient().getLocationCache().copy(); // this helps when multiple locationUpdate arrive in short time
        cleanMaps(); // needed when a worker has been removed
        updateWorkersInView(workers_dx, levels_dx);
        updateWorkersInView(workers_sx, levels_sx);
        updateWorkersInView(workers_top, levels_top);
        getClient().clearModifiedWorkersCache();
        refresh();
    }

    private void updateWorkersInView(List<Worker> workers_view, List<Block> levels_view) {
        //Location location = getClient().getLocationCache();

        List<it.polimi.ingsw.psp40.model.Worker> workersCache = new ArrayList<>(getClient().getLocationCache().getAllOccupants());

        new ArrayList<>(getClient().getModifiedWorkersCache()).forEach(worker -> {
            if (workerExistsInCache(workersCache, worker)) { // one or more workers have been moved
                Cell newCell = cellOfModifiedWorker(worker);
                Worker worker_view = modifiedWorkerInView(worker, workers_view);
                if (newCell != null) {
                    if (worker_view != null) {
                        worker_view.move(newCell.getCoordX(), newCell.getCoordY(), newCell.getTower().getTopComponent().getComponentCode(), false);
                    } else { // this happens when the game starts and different players are positioning their workers
                        Worker newWorker = new Worker(newCell.getCoordX(), newCell.getCoordY(), worker.getPlayerName(), worker.getId(), worker.getColor());
                        addWorker(newWorker);
                    }
                }
            } else { // one or more workers have been removed
                Worker workerToRemove = modifiedWorkerInView(worker, workers_view);
                levels_view.remove(workerToRemove);
                workers_view.remove(workerToRemove);
            }
        });
    }

    private boolean workerExistsInCache(List<it.polimi.ingsw.psp40.model.Worker> workersCache, it.polimi.ingsw.psp40.model.Worker worker) {
        return workersCache.stream().anyMatch(_worker -> _worker.getId() == worker.getId() && _worker.getPlayerName().equals(worker.getPlayerName()));
    }

    private Cell cellOfModifiedWorker(it.polimi.ingsw.psp40.model.Worker worker) {
        // return location.getLocation(worker); // non posso fare così perchè la serializzazione con GSON non preserve le reference
        Location tmpLocation = getClient().getLocationCache();
        return tmpLocation.getAllOccupied().stream()
                .filter(cell -> tmpLocation.getOccupant(cell).getPlayerName().equals(worker.getPlayerName()) && tmpLocation.getOccupant(cell).getId() == worker.getId())
                .findFirst().orElse(null);
    }

    private Worker modifiedWorkerInView(it.polimi.ingsw.psp40.model.Worker modifiedWorker, List<Worker> workers_view) {
        return workers_view.stream().filter(worker -> modifiedWorker.getPlayerName().equals(worker.ownerUsername) && modifiedWorker.getId() == worker.id).findFirst().orElse(null);
    }

    /**
     * Updates the components built on the given cell
     *
     * @param cell the cell to update
     */
    protected void updateCell(Cell cell) {
        int x = cell.getCoordX();
        int y = cell.getCoordY();

        boolean cellAlreadyUpdated = levels_dx.stream().anyMatch(_block -> _block.row == x && _block.col == y && _block.component.getComponentCode() == cell.getTower().getTopComponent().getComponentCode()); // if the updateCell(cell) is called for the component that I've just built, skip the update because the view is already updated
        if (cellAlreadyUpdated) {
            return;
        }

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
                block = new Dome(x, y, cell.getTower().getComponents().size() - 1);
                break;
        }

        if (block != null) {
            addAndRefresh(block);
        }
    }

    /**
     * Rebuilds from scratch the whole island
     */
    protected void updateWholeIsland() {
        clearBoard();

        Location location = getClient().getLocationCache();
        Cell[][] field = getClient().getFieldCache();

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                Tower tower = field[i][j].getTower();
                List<Component> components = tower.getComponents();
                for (int k = 0; k < components.size(); k++) {
                    switch (components.get(k).getComponentCode()) {
                        case 1:
                            addBlock(new Level1(i, j));
                            break;

                        case 2:
                            addBlock(new Level2(i, j));
                            break;

                        case 3:
                            addBlock(new Level3(i, j));
                            break;

                        case 4:
                            addBlock(new Dome(i, j, k));
                    }
                }

                it.polimi.ingsw.psp40.model.Worker occupant = location.getOccupant(i, j);
                if (occupant != null) {
                    Worker worker = new Worker(i, j, occupant.getPlayerName(), occupant.getId(), occupant.getColor());
                    worker.move(i, j, tower.getComponents().size() - 1, false);
                    addWorker(worker);
                }
            }
        }
        refresh();
    }

    private void disableMap(boolean disable) {
        if (isMapDisabled != disable) {
            isMapDisabled = disable;
            islandsGroup.setDisable(disable);
            /*if (disable) {
                islandsGroup.setEffect(grayscale);
            } else {
                islandsGroup.setEffect(null);
            }*/
        }
    }

    private void disableBlock(Block block, boolean disable) {
        block.setDisable(disable);
        if (disable) {
            block.setBlockEffect(grayscale);
        } else {
            block.setBlockEffect(null);
        }
    }

    /**
     * Adds the given block to all the views (left, right, top)
     *
     * @param block
     */
    private void addBlock(Block block) {
        levels_dx.add(block);

        Block block_sx = block.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        levels_sx.add(block_sx);

        Block block_top = block.copyAndSetCamera(GUIProperties.CameraType.TOP);
        levels_top.add(block_top);
    }

    /**
     * Adds the given worker to all the views (left, right, top)
     *
     * @param worker
     */
    private void addWorker(Worker worker) {
        levels_dx.add(worker);
        workers_dx.add(worker);

        Worker worker_sx = worker.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        levels_sx.add(worker_sx);
        workers_sx.add(worker_sx);

        Worker worker_top = worker.copyAndSetCamera(GUIProperties.CameraType.TOP);
        levels_top.add(worker_top);
        workers_top.add(worker_top);
    }

    /**
     * Adds the given worker to the island and refresh the view to show it
     *
     * @param worker
     */
    private void addWorkerAndRefresh(Worker worker) {
        addWorker(worker);
        refresh();
    }

    /**
     * Adds the given block to the island and refresh the view to show it
     *
     * @param block
     */
    private void addAndRefresh(Block block) {
        addBlock(block);
        refresh();
    }

    private boolean checkLastSelectedPhase(PhaseType phaseType) {
        if (selectedPhases.size() == 0) return false;
        else return selectedPhases.get(selectedPhases.size() - 1).equals(phaseType);
    }

    /**
     * Refresh the views
     */
    private void refresh() {
        reorderLevels(levels_dx);
        reorderLevels(levels_sx);
        reorderLevels(levels_top);

        cleanMaps();

        map_dx.getChildren().addAll(levels_dx);
        map_sx.getChildren().addAll(levels_sx);
        map_top.getChildren().addAll(levels_top);
    }

    // clean all Maps keeping only the Ground Levels
    private void cleanMaps() {
        map_dx.getChildren().removeAll(levels_dx);
        map_sx.getChildren().removeAll(levels_sx);
        map_top.getChildren().removeAll(levels_top);
    }

    /**
     * Reorders blocks in the given view in order to keep blocks in the correct layer
     *
     * @param levels
     */
    private void reorderLevels(List<Block> levels) {
        levels.sort((b1, b2) -> {
            if (b1.currentCamera == b2.currentCamera) { // just to be sure, but "levels" list should contains only elements with the same CameraType
                int b1_sum = 0;
                int b2_sum = 0;
                // calculates view order priority depending on the CameraType
                if (b1.currentCamera == GUIProperties.CameraType.RIGHT) {
                    b1_sum = b1.row + b1.col + b1.z;
                    b2_sum = b2.row + b2.col + b2.z;
                } else if (b1.currentCamera == GUIProperties.CameraType.LEFT) {
                    b1_sum = GUIProperties.getCorrespondingLeftRow(b1.row, b1.col) + GUIProperties.getCorrespondingLeftCol(b1.row, b1.col) + b1.z;
                    b2_sum = GUIProperties.getCorrespondingLeftRow(b2.row, b2.col) + GUIProperties.getCorrespondingLeftCol(b2.row, b2.col) + b2.z;
                } else if (b1.currentCamera == GUIProperties.CameraType.TOP) {
                    b1_sum = b1 instanceof Worker ? 99 : b1.z; // keep Workers always on front in top camera view
                    b2_sum = b2 instanceof Worker ? 99 : b2.z;
                }

                if (b1_sum > b2_sum) {
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

    private void setInstructionsLabelText(String text) {
        instructionsLabel.setText(text);
        instructionsLabelTransition.stop();
        instructionsLabelTransition.play();
    }

    private void buildInstructionsLabelTransition() {
        Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
        Duration duration = Duration.millis(800);

        ScaleTransition restoreTransiton = new ScaleTransition(Duration.millis(0), instructionsLabel);
        restoreTransiton.setInterpolator(interpolator);
        restoreTransiton.setToX(1);
        restoreTransiton.setToY(1);

        ScaleTransition st1 = new ScaleTransition(duration, instructionsLabel);
        st1.setInterpolator(interpolator);
        st1.setToX(1.15);
        st1.setToY(1.15);

        ScaleTransition st2 = new ScaleTransition(duration, instructionsLabel);
        st2.setInterpolator(interpolator);
        st2.setToX(1);
        st2.setToY(1);

        SequentialTransition sequentialTransition = new SequentialTransition(restoreTransiton, st1, st2);
        sequentialTransition.setCycleCount(2);

        instructionsLabelTransition = new SequentialTransition(sequentialTransition, new PauseTransition(Duration.millis(5000)));
        instructionsLabelTransition.setCycleCount(Animation.INDEFINITE);
    }

    private void buildBackgrounds() {
        double height = 1450;

        //BackgroundSize(double width, double height, boolean widthAsPercentage, boolean heightAsPercentage, boolean contain, boolean cover)
        BackgroundSize bgSize = new BackgroundSize(height * 16 / 9, height, false, false, false, false);

        /* BUILD BACKGROUND FOR RIGHT CAMERA (and set it as default) */
        //public BackgroundImage(Image image, BackgroundRepeat repeatX, BackgroundRepeat repeatY, BackgroundPosition position, BackgroundSize size)
        Image imgDx = new Image(getClass().getResource("/images/island_background_dx.png").toString());
        BackgroundImage bgImgDx = new BackgroundImage(imgDx,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize);
        background_dx = new Background(bgImgDx);
        borderPane.setBackground(background_dx);

        /* BUILD BACKGROUND FOR LEFT CAMERA */
        Image imgSX = new Image(getClass().getResource("/images/island_background_sx.png").toString());
        BackgroundImage bgImgSX = new BackgroundImage(imgSX,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize);
        background_sx = new Background(bgImgSX);

        /* BUILD BACKGROUND FOR TOP CAMERA */
        Image imgTop = new Image(getClass().getResource("/images/island_background_top.png").toString());
        BackgroundImage bgImgTop = new BackgroundImage(imgTop,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize);
        background_top = new Background(bgImgTop);
    }

    /**
     * Sends a message to the server
     *
     * @param message message to be sent
     */
    private void sendToServer(Message message) {
        getClient().sendToServer(message);
        waiting = true;

        if (message.getTypeOfMessage() == TypeOfMessage.BUILD_CELL
                || message.getTypeOfMessage() == TypeOfMessage.MOVE_WORKER
                || message.getTypeOfMessage() == TypeOfMessage.SET_POSITION_OF_WORKER) {
            restoreHighlight();
        }
    }

    private void clearBoard() {
        map_dx.getChildren().removeAll(levels_dx);
        map_sx.getChildren().removeAll(levels_sx);
        map_top.getChildren().removeAll(levels_top);
        levels_dx.clear();
        levels_sx.clear();
        levels_top.clear();
    }

    /**
     * Handles "Right Camera" selection
     *
     * @param actionEvent
     */
    @FXML
    public void rightViewButtonClicked(ActionEvent actionEvent) {
        currentCamera = GUIProperties.CameraType.RIGHT;
        switchCamera();
        myPane_sx.setVisible(false);
        island_sx.setVisible(false);
        myPane_top.setVisible(false);
        island_top.setVisible(false);
        island_dx.setVisible(true);
        myPane_dx.setVisible(true);
        borderPane.setBackground(background_dx);
    }

    /**
     * Handles "Left Camera" selection
     *
     * @param actionEvent
     */
    @FXML
    public void leftViewButtonClicked(ActionEvent actionEvent) {
        currentCamera = GUIProperties.CameraType.LEFT;
        switchCamera();
        myPane_dx.setVisible(false);
        island_dx.setVisible(false);
        myPane_top.setVisible(false);
        island_top.setVisible(false);
        island_sx.setVisible(true);
        myPane_sx.setVisible(true);
        borderPane.setBackground(background_sx);
    }

    /**
     * Handles "Top Camera" selection
     *
     * @param actionEvent
     */
    @FXML
    public void topViewButtonClicked(ActionEvent actionEvent) {
        currentCamera = GUIProperties.CameraType.TOP;
        switchCamera();
        myPane_dx.setVisible(false);
        island_dx.setVisible(false);
        myPane_sx.setVisible(false);
        island_sx.setVisible(false);
        myPane_top.setVisible(true);
        island_top.setVisible(true);
        borderPane.setBackground(background_top);
    }

    private void switchCamera() {
        switch (currentCamera) {
            case TOP:
                isRightCamera.setValue(false);
                isLeftCamera.setValue(false);
                isTopCamera.setValue(true);
                break;

            case LEFT:
                isRightCamera.setValue(false);
                isLeftCamera.setValue(true);
                isTopCamera.setValue(false);
                break;

            case RIGHT:
                isRightCamera.setValue(true);
                isLeftCamera.setValue(false);
                isTopCamera.setValue(false);
                break;
        }
    }

}
