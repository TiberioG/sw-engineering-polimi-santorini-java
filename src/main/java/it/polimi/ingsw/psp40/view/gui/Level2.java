package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

public class Level2 extends Block {

    Level2(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    private Level2(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 2);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.level2Width);
        this.setFitHeight(GUIProperties.level2Height);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                this.setImage(GUIProperties.image_level2_dx);
                break;

            case LEFT:
                this.setImage(GUIProperties.image_level2_sx);
                break;

            case TOP:
                this.setFitWidth(GUIProperties.level2TopWidth);
                this.setImage(GUIProperties.image_level2_top);
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
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level2XFix);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level2YFix);
                UtilsGUI.slideInDownAnimation(this);
                break;
        }
    }

    @Override
    Level2 copy() {
        return new Level2(this.row, this.col);
    }

    @Override
    Level2 copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Level2(this.row, this.col, cameraType);
    }
}
