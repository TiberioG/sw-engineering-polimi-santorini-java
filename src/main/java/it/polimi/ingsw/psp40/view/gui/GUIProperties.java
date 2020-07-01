package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.controller.Phase;
import javafx.scene.image.Image;

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

    protected static double blockTopWidth = 96;
    protected static double tileTopWidth = blockTopWidth;
    protected static double tileTopHeight = blockTopWidth;
    protected static double tileTopXSpacing = 11.5;
    protected static double tileTopYSpacing = 9.5;
    protected static double level1TopWidth = blockTopWidth, level2TopWidth = blockTopWidth, level3TopWidth = blockTopWidth, domeTopWidth = blockTopWidth, workerTopWidth = blockTopWidth;

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
    protected static double level3XFix = level2XFix;
    protected static double level3YFix = level2YFix - 16;

    protected static double domeWidth = 80;
    protected static double domeHeight = blockHeight;
    protected static double domeXFix = 26;
    protected static double domeYFix = -11;

    protected static double workerWidth = 80;
    protected static double worker_lvl3_Width = 76;
    protected static double workerHeight = blockHeight;

    protected enum CameraType {
        RIGHT, // DEFAULT/ABSOLUTE VIEW
        LEFT,
        TOP
    }

    /* IMAGES */

    protected static Image image_ground_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/tile_dx.png").toString());
    protected static Image image_ground_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/tile_sx.png").toString());
    protected static Image image_ground_top = new Image(GUIProperties.class.getResource("/images/blocks/top/tile_top.png").toString());
    protected static Image image_level1_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/level1_dx.png").toString());
    protected static Image image_level1_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/level1_sx.png").toString());
    protected static Image image_level1_top = new Image(GUIProperties.class.getResource("/images/blocks/top/level1_top.png").toString());
    protected static Image image_level2_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/level2_dx.png").toString());
    protected static Image image_level2_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/level2_sx.png").toString());
    protected static Image image_level2_top = new Image(GUIProperties.class.getResource("/images/blocks/top/level2_top.png").toString());
    protected static Image image_level3_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/level3_dx.png").toString());
    protected static Image image_level3_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/level3_sx.png").toString());
    protected static Image image_level3_top = new Image(GUIProperties.class.getResource("/images/blocks/top/level3_top.png").toString());
    protected static Image image_dome_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/dome_dx.png").toString());
    protected static Image image_dome_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/dome_sx.png").toString());
    protected static Image image_dome_top = new Image(GUIProperties.class.getResource("/images/blocks/top/dome_top.png").toString());
    protected static Image image_worker_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/worker_dx.png").toString());
    protected static Image image_worker_cut_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/worker_cut_dx.png").toString());
    protected static Image image_worker_cut_lvl3_dx = new Image(GUIProperties.class.getResource("/images/blocks/dx/worker_cut_lvl3_dx.png").toString());
    protected static Image image_worker_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/worker_sx.png").toString());
    protected static Image image_worker_cut_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/worker_cut_sx.png").toString());
    protected static Image image_worker_cut_lvl3_sx = new Image(GUIProperties.class.getResource("/images/blocks/sx/worker_cut_lvl3_sx.png").toString());
    protected static Image image_worker_top = new Image(GUIProperties.class.getResource("/images/blocks/top/worker_top.png").toString());

    protected static Image image_move_phase = new Image(GUIProperties.class.getResource("/images/phases/move_phase.png").toString());
    protected static Image image_build_phase = new Image(GUIProperties.class.getResource("/images/phases/build_phase.png").toString());
    protected static Image image_select_worker_phase = new Image(GUIProperties.class.getResource("/images/phases/select_worker_phase.png").toString());
    protected static Image image_end_turn_phase = new Image(GUIProperties.class.getResource("/images/phases/end_turn_phase.png").toString());

    /**
     * Method to get the image for a specified type of phase
     *
     * @param phase the phase you want to have the image of
     * @return the image associated to the phase type
     */
    protected static Image getImageForPhase(Phase phase) {
        Image image = null;
        switch (phase.getType()) {
            case MOVE_WORKER:
                image = image_move_phase;
                break;

            case BUILD_COMPONENT:
                image = image_build_phase;
                break;

            case SELECT_WORKER:
                image = image_select_worker_phase;
                break;

            case END_TURN:
                image = image_end_turn_phase;
                break;
        }

        return image;
    }

    /**
     * Returns the X (position) value to be used to place the {@link Block} in the map, fixed with a corrective value depending on row, col and cameraType
     * @param row
     * @param col
     * @param cameraType
     * @return fixed position value
     */
    protected static double getIncrementalFix_x(int row, int col, CameraType cameraType) {
        double incrementalFix = 0;
        if (cameraType == CameraType.LEFT) {
            col = getCorrespondingLeftCol(row, col);
            row = getCorrespondingLeftRow(row, col);
            incrementalFix = (col - row) * (-2) - 3;
        } else if (cameraType == CameraType.TOP) {
            incrementalFix = row * (-0.2);
        }
        return incrementalFix;
    }

    /**
     * Returns the Y (position) value to be used to place the {@link Block} in the map, fixed with a corrective value depending on row, col and cameraType
     * @param row
     * @param col
     * @param cameraType
     * @return fixed position value
     */
    protected static double getIncrementalFix_y(int row, int col, CameraType cameraType) {
        if (cameraType == CameraType.LEFT) {
            row = getCorrespondingLeftRow(row, col);
        } else if (cameraType == CameraType.TOP) {
            // no special treatment required
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
