package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.image.ImageView;

/**
 * @author sup3rgiu
 */

public abstract class Block extends ImageView {
    protected int row;
    protected int col;
    protected int z;

    GUIProperties.CameraType currentCamera = null;

    Block(int row, int col, int z) {
        this.row = row;
        this.col = col;
        this.z = z;
        this.setId(row + "," + col + "," + z);
        this.setPickOnBounds(false);
    }

    abstract void handleClick();

    abstract void loadImage(GUIProperties.CameraType cameraType);

    abstract void display(int row, int col);

    abstract Block copy();

    abstract Block copyAndSetCamera(GUIProperties.CameraType cameraType);

    protected void setXPosition(double x) {
        this.setX( x + GUIProperties.getIncrementalFix_x(this.row, this.col, currentCamera) ); // fix X position depending row, col and CameraType values
    }

    protected void setYPosition(double y) {
        this.setY( y + GUIProperties.getIncrementalFix_y(this.row, this.col, currentCamera) ); // fix Y position depending row, col and CameraType values
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
                    // todo
                    break;
            }
            System.out.println("x: " + this.getX() + ", y:" + this.getY());
        }
    }

}
