package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.RemoveGroundLevelException;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.ArrayList;
import java.util.List;

public class Tower extends Publisher<Message> {

    /* Attributes */

    private List<Component> components;

    /* Constructor(s) */

    /**
     * dumb constructor
     */
    public Tower() {
        this.components = new ArrayList<>();
        try {
            addComponent(Component.GROUND);
        } catch (BuildLowerComponentException e) {
            e.printStackTrace(); // this can not happen
        }
    }


    public Tower(VirtualView virtualView) {
        this();
        this.addListener(virtualView);

    }


    /* Methods */

    /**
     * Returns components of the tower
     * @return components list
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Adds the given component at the top of the tower
     * @param component {@link Component} to be added
     * @throws BuildLowerComponentException if the given component is lower than the tower's latest component
     */
    public void addComponent(Component component) throws BuildLowerComponentException {
        if(components.size()!=0 && component.getComponentCode() <= components.get(components.size()-1).getComponentCode())
            throw new BuildLowerComponentException();

        components.add(component);
        this.update();
    }

    /**
     * Removes the last {@link Component} of the tower.
     * @return {@link Component} removed. Null if the tower has only the {@link Component#GROUND} component
     */
     public Component removeComponent() throws RemoveGroundLevelException {
        if(components.size() > 1) { // greater than 1 because first level can not be removed (GROUND)
           Component component =  components.remove(components.size() - 1);
           this.update();
           return component;
        }
        else {
           throw new RemoveGroundLevelException();
        }

    }


    public Component getTopComponent (){
        return this.components.get(components.size() -1);
    }

    private void update(){
        publish(new Message("ALL", TypeOfMessage.TOWER_UPDATED));
    }


}
