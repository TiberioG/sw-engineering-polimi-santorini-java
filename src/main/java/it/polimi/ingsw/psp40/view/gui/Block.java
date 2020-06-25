package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Component;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * @author sup3rgiu
 */

public abstract class Block extends ImageView {
    protected int row;
    protected int col;
    protected int z;
    protected Component component;

    GUIProperties.CameraType currentCamera = null;

    Block(int row, int col, int z) {
        this.row = row;
        this.col = col;
        this.z = z;
        this.setId(row + "," + col + "," + z);
        this.setPickOnBounds(false);
    }

    /**
     * Loads the correct image for the block depending on the given camera
     * @param cameraType
     */
    abstract void loadImage(GUIProperties.CameraType cameraType);

    /**
     * Places the block in the given position
     * @param row row (coordinate X) of the island where to place the block
     * @param col column (coordinate Y) of the island where to place the block
     */
    abstract void display(int row, int col);

    /**
     * Creates a copy of the block
     * @return a copy of the block
     */
    abstract Block copy();

    /**
     * Creates a copy of the block changing the camera
     * @param cameraType
     * @return a copy of the block
     */
    abstract Block copyAndSetCamera(GUIProperties.CameraType cameraType);

    /**
     * Sets the given effect to the block
     * @param effect effect to apply to the block
     */
    protected void setBlockEffect(Effect effect) {
        this.setEffect(effect);
    }

    /**
     * Handles block click
     */
    protected void handleClick() {
        if(GUI.gameScreenController != null)
            GUI.gameScreenController.blockClicked(row, col, z);
    }

    /**
     * Sets X position of the block
     * @param x
     */
    protected void setXPosition(double x) {
        this.setX( x + GUIProperties.getIncrementalFix_x(this.row, this.col, currentCamera) ); // fix X position depending row, col and CameraType values
    }

    /**
     * Returns the final X coordinate of the block, fixed with a corrective value
     * @param x initial X value (not yet fixed)
     * @return fixed X value
     */
    protected double getFixedXPosition(double x) {
        return x + GUIProperties.getIncrementalFix_x(this.row, this.col, currentCamera);
    }

    /**
     * Sets Y position of the block
     * @param y
     */
    protected void setYPosition(double y) {
        this.setY( y + GUIProperties.getIncrementalFix_y(this.row, this.col, currentCamera) ); // fix Y position depending row, col and CameraType values
    }

    /**
     * Returns the final Y coordinate of the block, fixed with a corrective value
     * @param y initial Y value (not yet fixed)
     * @return fixed Y value
     */
    protected double getFixedYPosition(double y) {
        return y + GUIProperties.getIncrementalFix_y(this.row, this.col, currentCamera);
    }

    /**
     * Sets the camera of the block
     * @param cameraType
     */
    protected final void setCamera(GUIProperties.CameraType cameraType) {
        if((cameraType != null) && (currentCamera == null || currentCamera != cameraType)) {
            currentCamera = cameraType;
            loadImage(cameraType);
            switch (cameraType) {
                case RIGHT:
                    display(row, col);
                    break;

                case LEFT:
                    int leftRow = GUIProperties.getCorrespondingLeftRow(row, col);
                    int leftCol = GUIProperties.getCorrespondingLeftCol(row, col);
                    display(leftRow, leftCol);
                    break;

                case TOP:
                    display(row, col);
                    break;
            }
            //System.out.println("x: " + this.getX() + ", y:" + this.getY());
            //System.out.println(this.getFitHeight());
        }
    }

    /**
     * Highlights the block
     */
    protected final void highlightBlock() {
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
                        Color.YELLOW // todo change me. Something better. Maybe use RGBA
                )
        );

        if(this.getOpacity() == 0) { // tiles (Ground elements)
            this.setOpacity(0.55);
        }

        this.setEffect(blush);
    }

}
