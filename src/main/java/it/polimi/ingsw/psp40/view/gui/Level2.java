package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.Image;

public class Level2 extends Block {

    Level2(int row, int col) {
        this(row, col, GUIProperties.CameraType.RIGHT);
    }

    Level2(int row, int col, GUIProperties.CameraType cameraType) {
        super(row, col, 2);
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.level2Width);
        this.setFitHeight(GUIProperties.level2Height);
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
                this.setImage(new Image(getClass().getResource("/images/level2_dx.png").toString()));
                break;

            case LEFT:
                this.setImage(new Image(getClass().getResource("/images/level2_sx.png").toString()));
                break;

            case TOP:
                break;
        }
    }

    @Override
    void display(int row, int col) {
        this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + GUIProperties.level2XFix);
        this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level2YFix);
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
