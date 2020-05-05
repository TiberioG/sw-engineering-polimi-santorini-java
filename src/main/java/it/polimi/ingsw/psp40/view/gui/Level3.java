package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

/**
 * @author sup3rgiu
 */

public class Level3 extends Block {

    Level3(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    Level3(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 3);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.level3Width);
        this.setFitHeight(GUIProperties.level3Height);
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
                this.setImage(new Image(getClass().getResource("/images/level3_dx.png").toString()));
                break;

            case LEFT:
                this.setImage(new Image(getClass().getResource("/images/level3_sx.png").toString()));
                break;

            case TOP:
                break;
        }
    }

    @Override
    void display(int row, int col) {
        this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level3XFix);
        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix);
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
