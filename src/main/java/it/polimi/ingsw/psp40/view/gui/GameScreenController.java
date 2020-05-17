package it.polimi.ingsw.psp40.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author sup3rgiu
 */


public class GameScreenController extends  ScreenController {

    /* Attributes */

    @FXML
    private Circle originLeft;
    @FXML
    private Circle originRight;
    @FXML
    private Circle originBottom;
    @FXML
    private Circle originUpper;

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
    private AnchorPane anchorPane;

    private static final java.util.Map<Integer, List<Block>> levelsMap_dx = new HashMap<>();
    private static final java.util.Map<Integer, List<Block>> levelsMap_sx = new HashMap<>();

    private static final List<Block> levels_dx = new ArrayList<>();
    private static final List<Block> levels_sx = new ArrayList<>();

    private static Map map_dx;
    private static Map map_sx;

    private static boolean wantMove = false;
    private static boolean wantPlaceWorker = false;
    private static Worker selectedWorker = null;

    /* Methods */

    @FXML
    public void initialize() {
        levelsMap_dx.put(0, new ArrayList<>());
        levelsMap_dx.put(1, new ArrayList<>());
        levelsMap_dx.put(2, new ArrayList<>());
        levelsMap_dx.put(3, new ArrayList<>());
        levelsMap_dx.put(4, new ArrayList<>());

        levelsMap_sx.put(0, new ArrayList<>());
        levelsMap_sx.put(1, new ArrayList<>());
        levelsMap_sx.put(2, new ArrayList<>());
        levelsMap_sx.put(3, new ArrayList<>());
        levelsMap_sx.put(4, new ArrayList<>());

        map_dx = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.RIGHT);
        map_sx = new Map(new Dimension2D(GUIProperties.numRows, GUIProperties.numCols), GUIProperties.CameraType.LEFT);

        myPane_dx.getChildren().add(map_dx);
        myPane_sx.getChildren().add(map_sx);
        myPane_sx.setVisible(false);

        //testGrid();
    }


    // todo implementare fase --> build o move
    public static void blockClicked(int x, int y, int z) {
       System.out.println("Clicked: "+ x + ", " + y + ", " + z);
       if(selectedWorker != null && wantMove) {
           if(z != 4)
            moveWorker(x, y, z);
       }
       else if(wantPlaceWorker) {
           if(z == 0) {
               Worker worker = new Worker(x, y);
               wantPlaceWorker = false;
               addAndRefresh(worker);
           }
       }
       else if(z == 0) {
           Level1 level1 = new Level1(x, y);
           addAndRefresh(level1);
       }

       else if (z == 1) {
           Level2 level2 = new Level2(x, y);
           addAndRefresh(level2);
       }

       else if (z == 2) {
           Level3 level3 = new Level3(x, y);
           addAndRefresh(level3);
       }

       else if (z == 3) {
           Dome dome = new Dome(x, y);
           addAndRefresh(dome);
       }
    }

    public static void workerClicked(Worker worker) {
        System.out.println("Worker clicked! " + worker.row + ", " + worker.col + ", " + worker.z);
        selectedWorker = worker.copy();
    }

    private static void moveWorker(int x, int y, int z) {
        moveWorkerInView(x, y , z, levels_dx); // no per favore no! Fare più dinamico!!
        moveWorkerInView(x, y , z, levels_sx);
        selectedWorker = null;
        wantMove = false;
        refresh();
    }

    // BRRR brividi questa funzione
    private static void moveWorkerInView(int x, int y, int z, List<Block> levels) {
        levels.forEach( level -> {
            if(level instanceof Worker) { // OH DIO NO!
                if(level.row == selectedWorker.row && level.col == selectedWorker.col && level.z == selectedWorker.z)
                    ((Worker) level).move(x, y, z);
            }
        });
    }

    @FXML
    public void placeWorkerButtonClicked(ActionEvent actionEvent) {
        wantPlaceWorker = true;
    }

    @FXML
    public void moveButtonClicked(ActionEvent actionEvent) {
        wantMove = true;
    }

    private static void addAndRefresh(Block block) {
        levelsMap_dx.get(block.z).add(block);
        levels_dx.add(block);

        Block block_sx = block.copyAndSetCamera(GUIProperties.CameraType.LEFT);
        levelsMap_sx.get(block.z).add(block_sx);
        levels_sx.add(block_sx);

        refresh();
    }

    private static void refresh() {
        reorderLevels(levels_dx);
        reorderLevels(levels_sx);

        map_dx.getChildren().removeAll(levels_dx);
        map_dx.getChildren().addAll(levels_dx);

        map_sx.getChildren().removeAll(levels_sx);
        map_sx.getChildren().addAll(levels_sx);
    }

    private static void reorderLevels(List<Block> levels) {
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

    // todo cancellami
    // la griglia viene come voglio io, ma è solo un effetto grafico. I click non vengono presi con le coordinate traslate, ma con quelle che si avrebbe senza l'effetto
    private void testGrid() {

        double ulx = originUpper.getLayoutX();
        double uly = originUpper.getLayoutY();
        double urx = originRight.getLayoutX();
        double ury = originRight.getLayoutY();
        double lrx = originBottom.getLayoutX();
        double lry = originBottom.getLayoutY();
        double llx = originLeft.getLayoutX();
        double lly = originLeft.getLayoutY();

        PerspectiveTransform perspectiveTransform = new PerspectiveTransform();
        perspectiveTransform.setUlx(ulx);
        perspectiveTransform.setUly(uly);
        perspectiveTransform.setUrx(urx);
        perspectiveTransform.setUry(ury);
        perspectiveTransform.setLrx(lrx);
        perspectiveTransform.setLry(lry);
        perspectiveTransform.setLlx(llx);
        perspectiveTransform.setLly(lly);

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        final int numCols = 5 ;
        final int numRows = 5 ;
        final double width = 100;
        final double height = 100;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(width);
            gridPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(height);
            gridPane.getRowConstraints().add(rowConst);
        }

        gridPane.setHgap(18);
        gridPane.setVgap(18);
        gridPane.setEffect(perspectiveTransform);

        for (int i = 0 ; i < numCols ; i++) {
            for (int j = 0; j < numRows; j++) {
                addPaneToGrid(gridPane, i, j);
            }
        }

        anchorPane.getChildren().add(gridPane);

    }

    private void addPaneToGrid(GridPane grid, int colIndex, int rowIndex) {
        Pane pane = new Pane();
        Label label = new Label("Label " + colIndex + "/" + rowIndex);
        //pane.getChildren().add(label);

        label.setOnMouseEntered(e -> {
            gridClicked(colIndex, rowIndex);
            System.out.println(colIndex + "," + rowIndex);
        });
        grid.add(label, colIndex, rowIndex);
    }


    private void gridClicked(int col, int row) {
        System.out.printf("Mouse entered cell [%d, %d]%n", col, row);
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
