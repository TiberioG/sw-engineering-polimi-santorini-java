package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Component;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Ground extends Block {

    Ground(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    private Ground(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 0);
        this.component = Component.GROUND;
        this.setOpacity(0);
        this.setFitWidth(GUIProperties.tileWidth);
        this.setFitHeight(GUIProperties.tileHeight);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                this.setImage(GUIProperties.image_ground_dx);
                break;

            case LEFT:
                this.setImage(GUIProperties.image_ground_sx);
                break;

            case TOP:
                this.setFitWidth(GUIProperties.tileTopWidth);
                this.setFitHeight(GUIProperties.tileTopHeight);
                this.setImage(GUIProperties.image_ground_top);
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
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing));
                break;
        }
    }

    @Override
    protected void setBlockEffect(Effect effect) {
        if(effect == null) {
            this.setOpacity(0);
        }
        this.setEffect(effect);
    }

    @Override
    Ground copy() {
        return new Ground(this.row, this.col);
    }

    @Override
    Ground copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Ground(this.row, this.col, cameraType);
    }
}