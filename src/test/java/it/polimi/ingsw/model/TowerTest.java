package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.RemoveGroundLevelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TowerTest {

    private Tower tower;

    @Before
    public void setUp() {
        tower = new Tower();
    }

    @After
    public void tearDown() {
        tower = null;
    }

    @Test
    public void addLevel() throws BuildLowerComponentException {
        List<Component> localComponents = new ArrayList<>();
        localComponents.add(Component.GROUND);
        localComponents.add(Component.FIRST_LEVEL);
        localComponents.add(Component.SECOND_LEVEL);
        tower.addComponent(Component.FIRST_LEVEL);
        tower.addComponent(Component.SECOND_LEVEL);
        assertEquals(localComponents, tower.getComponents());
    }

    @Test (expected = BuildLowerComponentException.class)
    public void addLevel_BuildLevelsNotInOrder_throwsBuildLowerComponentException () throws BuildLowerComponentException {
        tower.addComponent(Component.SECOND_LEVEL);
        tower.addComponent(Component.FIRST_LEVEL);
    }

    @Test (expected = BuildLowerComponentException.class)
    public void addLevel_BuildTwiceSameLevel_throwsBuildLowerComponentException () throws BuildLowerComponentException {
        tower.addComponent(Component.FIRST_LEVEL);
        tower.addComponent(Component.FIRST_LEVEL);
    }

    @Test
    public void removeLevel() throws BuildLowerComponentException, RemoveGroundLevelException {
        List<Component> localComponents = new ArrayList<>();
        localComponents.add(Component.GROUND);
        localComponents.add(Component.FIRST_LEVEL);
        tower.addComponent(Component.FIRST_LEVEL);
        tower.addComponent(Component.SECOND_LEVEL);
        tower.removeComponent();
        assertEquals(localComponents, tower.getComponents());
    }

    @Test (expected = RemoveGroundLevelException.class)
    public void removeLevel_RemoveIfNoComponentsBuilt_throwsRemoveGroundLevelException () throws RemoveGroundLevelException {
        tower.removeComponent();
    }

    @Test
    public void getTopComponent() throws BuildLowerComponentException{
        tower.addComponent(Component.FIRST_LEVEL);
        tower.addComponent(Component.SECOND_LEVEL);

        assertEquals(Component.SECOND_LEVEL, tower.getTopComponent());

    }
}
