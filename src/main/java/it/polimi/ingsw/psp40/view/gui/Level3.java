package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Level3 extends Block {

    Level3(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    private Level3(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 3);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.level3Width);
        this.setFitHeight(GUIProperties.level3Height);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                this.setImage(GUIProperties.image_level3_dx);
                break;

            case LEFT:
                this.setImage(GUIProperties.image_level3_sx);
                break;

            case TOP:
                this.setFitWidth(GUIProperties.level3TopWidth);
                this.setImage(GUIProperties.image_level3_top);
                break;
        }
    }

    @Override
    void display(int row, int col) {
        switch (currentCamera) {
            case TOP:
                this.setXPosition(col * (GUIProperties.tileTopWidth + GUIProperties.tileTopXSpacing));
                this.setYPosition(row * (GUIProperties.tileTopHeight + GUIProperties.tileTopYSpacing));
                break;

            default: // right and left
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level3XFix);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix);
                UtilsGUI.slideInDownAnimation(this);
                break;
        }
    }

    @Override
    Level3 copy() {
        return new Level3(this.row, this.col);
    }

    @Override
    Level3 copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Level3(this.row, this.col, cameraType);
    }
}
