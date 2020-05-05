package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Level1 extends Block {

    Level1(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    Level1(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 1);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.level1Width);
        this.setFitHeight(GUIProperties.level1Height);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    void handleClick() {
        GameScreenController.blockClicked(row, col, z);
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                this.setImage(new Image(getClass().getResource("/images/level1_dx.png").toString()));
                break;

            case LEFT:
                this.setImage(new Image(getClass().getResource("/images/level1_sx.png").toString()));
                break;

            case TOP:
                break;
        }
    }

    @Override
    void display(int row, int col) {
        this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing));
        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf);
    }

    @Override
    Level1 copy() {
        return new Level1(this.row, this.col);
    }

    @Override
    Level1 copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Level1(this.row, this.col, cameraType);
    }
}
