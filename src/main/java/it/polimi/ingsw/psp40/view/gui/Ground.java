package it.polimi.ingsw.psp40.view.gui;

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
                break;
        }
    }

    @Override
    void display(int row, int col) {
        this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing));
        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing));
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