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

    abstract void loadImage(GUIProperties.CameraType cameraType);

    abstract void display(int row, int col);

    abstract Block copy();

    abstract Block copyAndSetCamera(GUIProperties.CameraType cameraType);

    protected void setBlockEffect(Effect effect) {
        this.setEffect(effect);
    }

    protected void handleClick() {
        if(GUI.gameScreenController != null)
            GUI.gameScreenController.blockClicked(row, col, z);
    }

    protected void setXPosition(double x) {
        this.setX( x + GUIProperties.getIncrementalFix_x(this.row, this.col, currentCamera) ); // fix X position depending row, col and CameraType values
    }

    protected double getFixedXPosition(double x) {
        return x + GUIProperties.getIncrementalFix_x(this.row, this.col, currentCamera);
    }

    protected void setYPosition(double y) {
        this.setY( y + GUIProperties.getIncrementalFix_y(this.row, this.col, currentCamera) ); // fix Y position depending row, col and CameraType values
    }

    protected double getFixedYPosition(double y) {
        return y + GUIProperties.getIncrementalFix_y(this.row, this.col, currentCamera);
    }

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
