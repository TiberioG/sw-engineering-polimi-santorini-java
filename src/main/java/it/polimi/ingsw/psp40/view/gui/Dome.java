package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.model.Component;

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
        this.component = Component.DOME;
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
                this.setFitWidth(GUIProperties.domeTopWidth);
                this.setImage(GUIProperties.image_dome_top);
                break;
        }
    }

    /**
     * shows image of dome
     *
     * @param row row (coordinate X) of the island where to place the block
     * @param col column (coordinate Y) of the island where to place the block
     */
    @Override
    void display(int row, int col) {
        switch (currentCamera) {
            case TOP:
                this.setXPosition(col * (GUIProperties.tileTopWidth + GUIProperties.tileTopXSpacing));
                this.setYPosition(row * (GUIProperties.tileTopHeight + GUIProperties.tileTopYSpacing));
                break;

            default: // right and left
                switch (this.z) {
                    case 1:
                        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - GUIProperties.level1Height / 2 + GUIProperties.level3YFix + GUIProperties.domeYFix + 145);
                        break;

                    case 2:
                        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - GUIProperties.level1Height / 2 + GUIProperties.level3YFix + GUIProperties.domeYFix + 83);
                        break;

                    case 3:
                        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - GUIProperties.level1Height / 2 + GUIProperties.level3YFix + GUIProperties.domeYFix + 32);
                        break;

                    case 4:
                        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - GUIProperties.level1Height / 2 + GUIProperties.level3YFix + GUIProperties.domeYFix);
                        break;
                }

                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level3XFix + GUIProperties.domeXFix);
                UtilsGUI.slideInDownAnimation(this);
                break;
        }
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
