package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Dome extends Block {

    Dome(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    Dome(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 4);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.domeWidth);
        this.setFitHeight(GUIProperties.domeHeight);
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
                this.setImage(GUIProperties.image_dome_dx);
                break;

            case LEFT:
                this.setImage(GUIProperties.image_dome_sx);
                break;

            case TOP:
                break;
        }
    }

    @Override
    void display(int row, int col) {
        this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level3XFix + GUIProperties.domeXFix);
        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix + GUIProperties.domeYFix);
    }

    @Override
    Dome copy() {
        return new Dome(this.row, this.col);
    }

    @Override
    Dome copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Dome(this.row, this.col, cameraType);
    }
}
