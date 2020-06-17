package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Component;
import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Level1 extends Block {

    Level1(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    private Level1(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 1);
        this.component = Component.FIRST_LEVEL;
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.level1Width);
        this.setFitHeight(GUIProperties.level1Height);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                this.setImage(GUIProperties.image_level1_dx);
                break;

            case LEFT:
                this.setImage(GUIProperties.image_level1_sx);
                break;

            case TOP:
                this.setFitWidth(GUIProperties.level1TopWidth);
                this.setImage(GUIProperties.image_level1_top);
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
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing));
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf);
                UtilsGUI.slideInDownAnimation(this);
                break;
        }
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
