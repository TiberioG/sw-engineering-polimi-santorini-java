package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import animatefx.animation.*;
import javafx.util.Duration;

/**
 * @author sup3rgiu
 */

public class Worker extends Block {

    protected final Colors color;
    protected final String ownerUsername;
    protected final int id;
    private AnimationFX selectionAnimation = null;
    protected Animation moveAnimation;

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
                else if (z == 4) {
                    this.setImage(GUIProperties.image_worker_cut_lvl3_dx);
                    this.setFitWidth(GUIProperties.worker_lvl3_Width);
                }
                else
                    this.setImage(GUIProperties.image_worker_cut_dx);
                break;

            case LEFT:
                if(z == 1)
                    this.setImage(GUIProperties.image_worker_sx);
                else if (z == 4) {
                    this.setImage(GUIProperties.image_worker_cut_lvl3_sx);
                    this.setFitWidth(GUIProperties.worker_lvl3_Width);
                }
                else
                    this.setImage(GUIProperties.image_worker_cut_sx);
                break;

            case TOP:
                break;
        }
    }

    private void loadDefaultWorkerImage() {
        switch (currentCamera) {
            case RIGHT:
                this.setImage(GUIProperties.image_worker_dx);
                break;

            case LEFT:
                this.setImage(GUIProperties.image_worker_sx);
                break;

            case TOP:
                break;
        }
    }

    protected void move(int row, int col, int z) {
        this.move(row, col, z, true);
    }

    protected void move (int row, int col, int z, boolean withAnimation) {
        this.z = z + 1;
        this.row = row;
        this.col = col;
        if(!withAnimation) { // load new image immediately only if worker is moving without animation
            loadImage(currentCamera);
        }
        switch (currentCamera) {
            case RIGHT:
                display(row, col, withAnimation);
                break;

            case LEFT:
                int leftRow = GUIProperties.getCorrespondingLeftRow(row, col);
                int leftCol = GUIProperties.getCorrespondingLeftCol(row, col);
                display(leftRow, leftCol, withAnimation);
                break;

            case TOP:
                // todo
                break;
        }
    }

    @Override
    void display(int row, int col) {
        display(row, col, false);
    }

    void display(int row, int col, boolean withAnimation) {
        double endX = 0;
        double endY = 0;
        switch(this.z) {
            case 1:
                endX = this.getFixedXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 39);
                endY = this.getFixedYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf + 5);
                break;

            case 2:
                endX = this.getFixedXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                endY = this.getFixedYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing) - GUIProperties.tileHeightHalf - 50);
                break;

            case 3:
                endX = this.getFixedXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                endY = this.getFixedYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level2YFix - 49.5);
                break;

            case 4:
                endX = this.getFixedXPosition((col - row) * (GUIProperties.tileWidthHalf + GUIProperties.tileXSpacing) + 43);
                endY = this.getFixedYPosition((col + row) * (GUIProperties.tileHeightHalf + GUIProperties.tileYSpacing)  - GUIProperties.tileHeightHalf - GUIProperties.level1Height/2 + GUIProperties.level3YFix - 56.5); // 56.5
                break;
        }

        if(withAnimation) {
            loadDefaultWorkerImage(); // use default worker while moving
            applyColor();
            startTransition(endX, endY);
        } else {
            this.setX(endX);
            this.setY(endY);
            applyColor();
        }
    }

    private void startTransition(double endX, double endY) {
        Timeline move1 = new Timeline();
        move1.getKeyFrames().add(new KeyFrame(
                Duration.seconds(0.5),
                new KeyValue(this.yProperty(), this.getY()-100)
        ));

        Timeline move2 = new Timeline();
        move2.getKeyFrames().add(new KeyFrame(
                Duration.seconds(1.5),
                new KeyValue(this.xProperty(), endX),
                new KeyValue(this.yProperty(), endY-100)
        ));

        Timeline move3 = new Timeline();
        move3.getKeyFrames().add(new KeyFrame(
                Duration.seconds(0.5),
                new KeyValue(this.yProperty(), endY)
        ));
        move3.setOnFinished(e -> {
            loadImage(currentCamera); // load new image only when animation is finished. Do not use moveAnimation.setOnFinished() because it's overridden in GameScreenController
            this.setDisable(false);
        });

        this.setDisable(true);
        moveAnimation = new SequentialTransition(move1, move2, move3);
        moveAnimation.play();
    }

    private void applyColor() {
        ImageView clipImage = new ImageView(this.getImage());
        clipImage.setPreserveRatio(true);
        clipImage.setFitWidth(this.getFitWidth());
        clipImage.setFitHeight(this.getFitHeight());
        clipImage.setX(this.getX());
        clipImage.setY(this.getY());
        clipImage.xProperty().bind(this.xProperty()); // follow worker when he is moving
        clipImage.yProperty().bind(this.yProperty());
        this.setClip(clipImage);
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);
        ColorInput colorInput = new ColorInput(
                this.getX(),
                this.getY(),
                this.getImage().getWidth(),
                this.getImage().getHeight(),
                this.color.getPaintColor()
        );
        colorInput.xProperty().bind(this.xProperty());
        colorInput.yProperty().bind(this.yProperty());
        Blend blush = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                colorInput
        );

        this.setEffect(blush);
    }

    @Override
    protected void setBlockEffect(Effect effect) {
        if(effect != null) {    // apply custom effect
            this.setEffect(effect);
        } else {                // restore worker color
            applyColor();
        }
    }

    protected void startSelectionAnimation() {
        selectionAnimation = new Tada(this);

        EventHandler<ActionEvent> eventHandler = e -> {
            selectionAnimation.setDelay(new Duration(500));
            selectionAnimation.play();
        };

        selectionAnimation.setOnFinished(eventHandler); // repeat, with delay between
        selectionAnimation.play();
    }

    protected void stopSelectionAnimation() {
        if(selectionAnimation != null) {
            selectionAnimation.stop();

            // restore image, otherwise it's scaled/rotated
            this.scaleXProperty().setValue(1);
            this.scaleYProperty().setValue(1);
            this.scaleZProperty().setValue(1);
            this.rotateProperty().setValue(0);
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
