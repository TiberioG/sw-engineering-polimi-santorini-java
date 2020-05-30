package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Dome extends Block {

    Dome(int row, int col) {
        this(row, col, 4);
    }

    Dome(int row, int col, int z) {
        this(row, col, z, GUIProperties.CameraType.RIGHT);
    }

    private Dome(int row, int col, int z, GUIProperties.CameraType cameraType) {
        super(row, col, z);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.domeWidth);
        this.setFitHeight(GUIProperties.domeHeight);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    protected void handleClick() {
        GUI.gameScreenController.blockClicked(row, col, z);
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
        switch (this.z) {
            case 1:
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix + GUIProperties.domeYFix + 145);
                break;

            case 2:
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix + GUIProperties.domeYFix + 83);
                break;

            case 3:
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix + GUIProperties.domeYFix + 32);
                break;

            case 4:
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix + GUIProperties.domeYFix);
                break;
        }

        this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level3XFix + GUIProperties.domeXFix);
        UtilsGUI.slideInDownAnimation(this);
    }


    @Override
    Dome copy() {
        return new Dome(this.row, this.col, this.z, this.currentCamera);
    }

    @Override
    Dome copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Dome(this.row, this.col, this.z, cameraType);
    }
}
