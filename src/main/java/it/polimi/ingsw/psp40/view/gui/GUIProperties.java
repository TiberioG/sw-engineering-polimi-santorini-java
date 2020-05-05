package it.polimi.ingsw.psp40.view.gui;

/**
 * @author sup3rgiu
 */

public class GUIProperties {

    protected static int numRows = 5;
    protected static int numCols = 5;

    protected static double tileWidth = 162;
    protected static double tileWidthHalf = tileWidth / 2;
    protected static double tileHeight = 100;
    protected static double tileHeightHalf = tileHeight / 2;
    protected static double tileXSpacing = 7;
    protected static double tileYSpacing = 2.5;

    private static double blockWidth = 200;
    private static double blockHeight = 150;

    protected static double level1Width = blockWidth;
    protected static double level1Height = blockHeight;

    protected static double level2Width = blockWidth;
    protected static double level2Height = 135;
    protected static double level2XFix = 15;
    protected static double level2YFix = 23.5;

    protected static double level3Width = 132.5;
    protected static double level3Height = blockHeight;
    protected static double level3XFix = level2XFix ;
    protected static double level3YFix = level2YFix - 16;

    protected enum CameraType {
        RIGHT, // DEFAULT/ABSOLUTE VIEW
        LEFT,
        TOP
    }

      protected static double getIncrementalFix_x(int row, int col, CameraType cameraType) {
        if(cameraType == CameraType.LEFT) {
            col = getCorrespondingLeftCol(row, col);
            row = getCorrespondingLeftRow(row, col);
        } else if (cameraType == CameraType.TOP) {
            // todo
        }
        return (col - row) * (-2) - 3;
    }

    protected static double getIncrementalFix_y(int row, int col, CameraType cameraType) {
        if(cameraType == CameraType.LEFT) {
            row = getCorrespondingLeftRow(row, col);
        } else if (cameraType == CameraType.TOP) {
            // todo
        }
        return (row * 2.5) - 6;
    }

    protected static int getCorrespondingLeftRow(int row, int col) {
        return GUIProperties.numRows - 1 - col;
    }

    protected static int getCorrespondingLeftCol(int row, int col) {
        return row;
    }


}
