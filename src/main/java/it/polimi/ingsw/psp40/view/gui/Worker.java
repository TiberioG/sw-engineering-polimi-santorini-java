package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Worker extends Block {

    protected Colors color;
    protected String owner;

    Worker(int row, int col) {
        this(row, col, 1, GUIProperties.CameraType.RIGHT);
    }

    private Worker(int row, int col, int z, GUIProperties.CameraType cameraType) {
        super(row, col, z);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.workerWidth);
        this.setFitHeight(GUIProperties.workerHeight);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    void handleClick() {
        GameScreenController.workerClicked(this);
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                if(z == 1)
                    this.setImage(GUIProperties.image_worker_dx);
                else if (z == 4)
                    this.setImage(GUIProperties.image_worker_cut_lvl3_dx);
                else
                    this.setImage(GUIProperties.image_worker_cut_dx);
                break;

            case LEFT:
                if(z == 1)
                    this.setImage(GUIProperties.image_worker_sx);
                else if (z == 4)
                    this.setImage(GUIProperties.image_worker_cut_lvl3_sx);
                else
                    this.setImage(GUIProperties.image_worker_cut_sx);
                break;

            case TOP:
                break;
        }
    }

    protected void move(int row, int col, int z) {
        this.z = z + 1;
        this.row = row;
        this.col = col;
        loadImage(currentCamera);
        switch (currentCamera) {
            case RIGHT:
                display(row, col);
                break;

            case LEFT:
                int leftRow = GUIProperties.getCorrespondingLeftRow(row, col);
                int leftCol = GUIProperties.getCorrespondingLeftCol(row, col);
                display(leftRow, leftCol);
                break;

            case TOP:
                // todo
                break;
        }
    }

    @Override
    void display(int row, int col) {
        switch(this.z) {
            case 1:
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 39);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf + 5);
                break;

            case 2:
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - 50);
                break;

            case 3:
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level2YFix - 49.5);
                break;

            case 4:
                this.setFitWidth(GUIProperties.worker_lvl3_Width);
                this.setFitHeight(GUIProperties.workerHeight);
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix - 56.5); // 56.5
                break;
        }
    }

    @Override
    Worker copy() {
        return new Worker(this.row, this.col, this.z, this.currentCamera);
    }

    @Override
    Worker copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Worker(this.row, this.col, this.z, cameraType);
    }
}
