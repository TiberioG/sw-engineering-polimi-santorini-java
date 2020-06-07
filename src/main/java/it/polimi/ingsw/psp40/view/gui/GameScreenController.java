package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.*;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

        UtilsGUI.buttonHoverEffect(leftViewButton);
        UtilsGUI.buttonHoverEffect(rightViewButton);
        UtilsGUI.buttonHoverEffect(topViewButton);
        
        buildInstructionsLabelTransition();

/*        rightAnchorPane.layoutXProperty().addListener((observable, oldValue, newValue) -> { // keep rightAnchorPane's elements in position when switch camera from right to left and viceversa
            rightAnchorPane.getChildren().forEach(node -> {
                double fixAnchor = (double) newValue > (double) oldValue ? (double) newValue - (double) oldValue : 0;
                AnchorPane.setRightAnchor(node, 10.0 + fixAnchor);
            });
        });*/

        disableMap(true); // start with map disabled
        buildBackgrounds();

    }

    /* METHODS TO HANDLE BLOCKS CLICK */

    public void blockClicked(int x, int y, int z) {
        System.out.println("Clicked: "+ x + ", " + y + ", " + z);
        if(!waiting) {
            if (checkLastSelectedPhase(PhaseType.MOVE_WORKER)) {
                moveWorker(x, y, z);
            } else if (checkLastSelectedPhase(PhaseType.BUILD_COMPONENT)) {
                build(x, y, z);
            } else if (shouldPositionWorkers && selectedPhases.size() == 0 && !hasEnoughWorkers()) {
                placeWorker(x, y, z);
            }
        }

    }

    public void workerClicked(Worker worker) {
        System.out.println("Worker clicked! " + worker.row + ", " + worker.col + ", " + worker.z);
        if(checkLastSelectedPhase(PhaseType.SELECT_WORKER) &&  !waiting) {
            if(worker.ownerUsername.equals(getClient().getUsername())) {
                highlightAvailableWorkersForSelection(false);
                selectedWorker = worker.copy();
                sendToServer(new Message(TypeOfMessage.SELECT_WORKER, selectedWorker.id));
            }
        }
        else if(checkLastSelectedPhase(PhaseType.MOVE_WORKER) &&  !waiting) { // handle click on worker if I'm in MOVE_WORKER phase. Useful when workers can be swapped
            moveWorker(worker.row, worker.col, worker.z - 1);
        }
    }

    private void highlightAvailableWorkersForSelection(boolean highlight) {
        highlightAvailableWorkersForSelectionInView(workers_dx, highlight);
        highlightAvailableWorkersForSelectionInView(workers_sx, highlight);
        highlightAvailableWorkersForSelectionInView(workers_top, highlight);
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
        vbButtons.setSpacing(5);
        vbButtons.setPadding(new Insets(10, 20, 10, 20));
        vbButtons.setPrefWidth(150);
        vbButtons.setTranslateY(50);

        colorsAvailable.forEach( color -> {
            Button button = new Button(color);
            button.setOnAction(actionEvent ->  {
                stackPane.getChildren().remove(vbButtons);
                disableMap(false);
                chosenColor = Colors.valueOf(color);
                setInstructionsLabelText("Posiziona i tuoi due worker in delle celle libere");
                highlightAvailableCellsInitialPosition();
            });
            UtilsGUI.buttonHoverEffect(button);
            button.setPrefWidth(vbButtons.getPrefWidth());
            vbButtons.getChildren().add(button);
        });

        disableMap(true);
        vbButtons.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(vbButtons);

        setInstructionsLabelText("Seleziona il colore dei tuoi worker");
    }

    private void highlightAvailableCellsInitialPosition() {
        List<Cell> availableCells = Arrays.stream(getClient().getFieldCache()).flatMap(Arrays::stream).collect(Collectors.toList()); // 2-dimensional array to List
        List<Cell> occupiedCells = new ArrayList<>(getClient().getLocationCache().getAllOccupied());
        availableCells.removeIf( cell -> occupiedCells.stream().anyMatch( occupiedCell -> cell.getCoordX() == occupiedCell.getCoordX() && cell.getCoordY() == occupiedCell.getCoordY()));
        highlightAvailableCells(availableCells);
        waiting = false;
        selectedPhases = new ArrayList<>(); // just to be sure
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

                    sendToServer(new Message(TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(chosenColor, workersCoord)));

                    setInstructionsLabelText("Aspetta il tuo turno...");
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
            GUI.showPopup(confirmPopup);
        }
    }

    /* METHODS TO HANDLE MOVE PHASE */

    private void moveWorker(int x, int y, int z) {
        if(selectedWorker != null) {
            List<Cell> availableCells = getClient().getAvailableMoveCells();
            boolean isAvailableCell = availableCells.stream().anyMatch( cell -> cell.getCoordX() == x && cell.getCoordY() == y);
            if(isAvailableCell) {
                moveSelectedWorkerInView(x, y, z, workers_dx);
                moveSelectedWorkerInView(x, y, z, workers_top);
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
        if (availableCells.size() > 0) {
            highlightAvailableCells(availableCells);
            waiting = false;
        } else {
            boolean moveAfterSelectWorker = selectedPhases.size() == 2;
            String text = "There are no cells available to move at this stage! Select another"  + (moveAfterSelectWorker ? "  worker." : " phase");
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
            GUI.showPopup(confirmPopup);
        }

    }

    /* METHODS TO HANDLE PHASE SELECTION */

    protected void askDesiredPhase() {
        List<Phase> phaseList = getClient().getListOfPhasesCache();
        if (phaseList.size() > 1) {
            double phaseViewWidth = 150;

            hboxPhases.getChildren().clear();
            hboxPhases.setSpacing(10);
            hboxPhases.setPadding(new Insets(10, 10, 10, 10));
            hboxPhases.setPrefWidth(phaseList.size() * phaseViewWidth + hboxPhases.getInsets().getLeft() + hboxPhases.getInsets().getRight() + hboxPhases.getSpacing() * (phaseList.size()-1));
            hboxPhases.setAlignment(Pos.CENTER);

            phaseList.forEach( phase -> {
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

            setInstructionsLabelText("Seleziona l'azione desiderata");

        } else phaseButtonClicked(phaseList.get(0).getType());
    }

    private void phaseButtonClicked(PhaseType selectedPhase) {
        switch (selectedPhase) {
            case SELECT_WORKER:
                waiting = false;
                selectedPhases.add(PhaseType.SELECT_WORKER);
                highlightAvailableWorkersForSelection(true);
                setInstructionsLabelText("Seleziona un worker");
                break;
            case MOVE_WORKER:
                selectedPhases.add(PhaseType.MOVE_WORKER);
                setInstructionsLabelText("Muovi il worker in una delle celle disponibili");
                sendToServer(new Message(TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
                break;
            case BUILD_COMPONENT:
                selectedPhases.add(PhaseType.BUILD_COMPONENT);
                setInstructionsLabelText("Costruisci in una delle celle disponibili");
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

    protected void endTurn() {
        selectedWorker = null;
        selectedPhases = new ArrayList<>();
        setInstructionsLabelText("Aspetta il tuo turno...");

        //disableMap(true); // todo farlo o no?
    }

    /* METHODS TO SHOW PLAYERS INFO */

    protected void setPlayersInfo(List<Player> playerList) {
        VBox myVbox = new VBox();

        VBox enemyVbox = new VBox();
        ImageView versusImage = new ImageView(new Image(getClass().getResource("/images/versus.png").toString()));
        versusImage.setPreserveRatio(true);
        versusImage.setFitHeight(45);
        enemyVbox.getChildren().add(versusImage);

        HBox enemyHbox = new HBox();

        playerList.forEach(player -> {
            ImageView imageView = new ImageView(new Image(getClass().getResource("/images/characterImage/image-card-" + player.getCurrentCard().getId() + ".png").toString()));
            imageView.setPreserveRatio(true);
            if(player.getName().equals(getClient().getUsername())) {
                imageView.setScaleX(-1); // reflect image
                imageView.setFitWidth(leftAnchorPane.getLayoutBounds().getWidth() * 0.80);
                myVbox.getChildren().addAll(imageView);
            } else {
                imageView.setFitWidth(rightAnchorPane.getLayoutBounds().getWidth() * 0.65);
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
        enemyVbox.setTranslateX(rightAnchorPane.getWidth() - hboxWidth);
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
        highlightAvailableCellsInView(availableCells, levels_top);
        highlightAvailableCellsInView(availableCells, map_top.getTiles());
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
        restoreHighlightInView(levels_top);
        restoreHighlightInView(map_top.getTiles());
    }

    private void restoreHighlightInView(List<Block> view) {
        view.forEach(block -> disableBlock(block, false));
    }

    //private Location tmpLocation = null;

    protected void updateWorkersPosition() {
        //tmpLocation = getClient().getLocationCache().copy(); // this helps when multiple locationUpdate arrive in short time
        updateWorkersInView(workers_dx);
        updateWorkersInView(workers_sx);
        updateWorkersInView(workers_top);
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
/*            if (disable) {
                islandsGroup.setEffect(grayscale);
            } else {
                islandsGroup.setEffect(null);
            }*/
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
        levels_dx.add(block);

        Block block_sx = block.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        levels_sx.add(block_sx);

        Block block_top = block.copyAndSetCamera(GUIProperties.CameraType.TOP);
        levels_top.add(block_top);
    }

    private void addWorker(Worker worker) {
        levels_dx.add(worker);
        workers_dx.add(worker);

        Worker worker_sx = worker.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        levels_sx.add(worker_sx);
        workers_sx.add(worker_sx);

        Worker worker_top= worker.copyAndSetCamera(GUIProperties.CameraType.TOP);
        levels_top.add(worker_top);
        workers_top.add(worker_top);
    }

    private void addWorkerAndRefresh(Worker worker) {
        addWorker(worker);
        refresh();
    }

    private void addAndRefresh(Block block) {
        addBlock(block);
        refresh();
    }

    private boolean checkLastSelectedPhase(PhaseType phaseType) {
        if (selectedPhases.size() == 0) return false;
        else return selectedPhases.get(selectedPhases.size() - 1).equals(phaseType);
    }

    private void refresh() {
        reorderLevels(levels_dx);
        reorderLevels(levels_sx);
        reorderLevels(levels_top);

        map_dx.getChildren().removeAll(levels_dx);
        map_dx.getChildren().addAll(levels_dx);

        map_sx.getChildren().removeAll(levels_sx);
        map_sx.getChildren().addAll(levels_sx);

        map_top.getChildren().removeAll(levels_top);
        map_top.getChildren().addAll(levels_top);
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
                } else if(b1.currentCamera == GUIProperties.CameraType.TOP) {
                    b1_sum = b1 instanceof Worker ? 99 : b1.z; // keep Workers always on front in top camera view
                    b2_sum = b2 instanceof Worker ? 99 : b2.z;
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
        BackgroundSize bgSize = new BackgroundSize(height*16/9, height, false, false, false, false);

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
        map_top.getChildren().removeAll(levels_top);
        levels_dx.clear();
        levels_sx.clear();
        levels_top.clear();
    }

    @FXML
    public void rightViewButtonClicked(ActionEvent actionEvent) {
        myPane_sx.setVisible(false);
        island_sx.setVisible(false);
        myPane_top.setVisible(false);
        island_top.setVisible(false);
        island_dx.setVisible(true);
        myPane_dx.setVisible(true);
        borderPane.setBackground(background_dx);
    }

    @FXML
    public void leftViewButtonClicked(ActionEvent actionEvent) {
        myPane_dx.setVisible(false);
        island_dx.setVisible(false);
        myPane_top.setVisible(false);
        island_top.setVisible(false);
        island_sx.setVisible(true);
        myPane_sx.setVisible(true);
        borderPane.setBackground(background_sx);
    }

    @FXML
    public void topViewButtonClicked(ActionEvent actionEvent) {
        myPane_dx.setVisible(false);
        island_dx.setVisible(false);
        myPane_sx.setVisible(false);
        island_sx.setVisible(false);
        myPane_top.setVisible(true);
        island_top.setVisible(true);
        borderPane.setBackground(background_top);
    }

}
