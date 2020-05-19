package it.polimi.ingsw.psp40.view.gui;

import javafx.geometry.Dimension2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

public class Map extends Group {

    public Map(Dimension2D dimension, GUIProperties.CameraType cameraType) {

        this.setCache(false);
        this.setCacheHint(CacheHint.SPEED);

        for (int row = 0; row < dimension.getWidth(); row++) {
            for (int col = 0; col < dimension.getHeight(); col++) {
                Ground tile = new Ground(row, col);
                tile.setCamera(cameraType);
                this.getChildren().add(tile);
            }
        }
    }

    protected List<Block> getTiles() {
        List<Block> tiles = new ArrayList<>();
        this.getChildren().forEach(node -> {
            if(node instanceof Block) {
                tiles.add((Block) node);
            }
        });
        return tiles;
    }

}

