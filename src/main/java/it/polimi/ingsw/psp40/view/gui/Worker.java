package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * @author sup3rgiu
 */

public class Worker extends Block {

    protected final Colors color;
    protected final String ownerUsername;
    protected final int id;

    Worker(int row, int col, String ownerUsername, int id, Colors color) {
        this(row, col, 1, GUIProperties.CameraType.RIGHT, ownerUsername, id, color);
    }

    private Worker(int row, int col, int z, GUIProperties.CameraType cameraType, String ownerUsername, int id, Colors color) {
        super(row, col, z);
        this.ownerUsername = ownerUsername;
        this.id = id;
        this.color = color;
        this.setPreserveRatio(true);
        this.setFitWidth(GUIProperties.workerWidth);
        this.setFitHeight(GUIProperties.workerHeight);
        setCamera(cameraType);
        this.setOnMouseClicked(event -> handleClick());
    }

    @Override
    protected void handleClick() {
        if(GUI.gameScreenController != null)
            GUI.gameScreenController.workerClicked(this);
    }

    @Override
    void loadImage(GUIProperties.CameraType cameraType) {
        switch (cameraType) {
            case RIGHT:
                if(z == 1)
                    this.setImage(GUIProperties.image_worker_dx);
                else if (z == 4)
                    this.setImage(GUIProperties.image_worker_cut_lvl3_dx);
                else
                    this.setImage(GUIProperties.image_worker_cut_dx);
                break;

            case LEFT:
                if(z == 1)
                    this.setImage(GUIProperties.image_worker_sx);
                else if (z == 4)
                    this.setImage(GUIProperties.image_worker_cut_lvl3_sx);
                else
                    this.setImage(GUIProperties.image_worker_cut_sx);
                break;

            case TOP:
                break;
        }
    }

    protected void move(int row, int col, int z) {
        this.z = z + 1;
        this.row = row;
        this.col = col;
        loadImage(currentCamera);
        switch (currentCamera) {
            case RIGHT:
                display(row, col);
                break;

            case LEFT:
                int leftRow = GUIProperties.getCorrespondingLeftRow(row, col);
                int leftCol = GUIProperties.getCorrespondingLeftCol(row, col);
                display(leftRow, leftCol);
                break;

            case TOP:
                // todo
                break;
        }
    }

    @Override
    void display(int row, int col) {
        switch(this.z) {
            case 1:
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 39);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf + 5);
                break;

            case 2:
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - 50);
                break;

            case 3:
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level2YFix - 49.5);
                break;

            case 4:
                this.setFitWidth(GUIProperties.worker_lvl3_Width);
                this.setFitHeight(GUIProperties.workerHeight);
                this.setXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                this.setYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix - 56.5); // 56.5
                break;
        }

        applyColor();
    }

    private void applyColor() {
        ImageView clipImage = new ImageView(this.getImage());
        clipImage.setPreserveRatio(true);
        clipImage.setFitWidth(this.getFitWidth());
        clipImage.setFitHeight(this.getFitHeight());
        clipImage.setX(this.getX());
        clipImage.setY(this.getY());
        this.setClip(clipImage);
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);
        Blend blush = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        this.getX(),
                        this.getY(),
                        this.getImage().getWidth(),
                        this.getImage().getHeight(),
                        this.color.getPaintColor()
                )
        );

        this.setEffect(blush);
    }

    @Override
    protected void setBlockEffect(Effect effect) {
        if(effect != null) { // apply custom effect
            this.setEffect(effect);
        } else { // restore worker color
            applyColor();
        }

    }

    @Override
    Worker copy() {
        return new Worker(this.row, this.col, this.z, this.currentCamera, this.ownerUsername, this.id, this.color);
    }

    @Override
    Worker copyAndSetCamera(GUIProperties.CameraType cameraType) {
        return new Worker(this.row, this.col, this.z, cameraType, this.ownerUsername, this.id, this.color);
    }
}
