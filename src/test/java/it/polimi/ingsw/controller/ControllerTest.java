package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Configuration;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class ControllerTest {
    private Controller controller;
    private VirtualView virtualView;
    private boolean displayMessageHasCalled;

    public class VirtualViewForTest extends VirtualView {
        public VirtualViewForTest(Server server) {
            super(server);
        }

        @Override
        public void displayMessage(Message message) {
            displayMessageHasCalled = true;
        }

    }

    @Before
    public void setUp() {
        virtualView = new VirtualViewForTest(new Server());
        controller = new Controller(virtualView);
        displayMessageHasCalled = false;
    }

    @Test
    public void startNewMatchTest() {
        HashMap<String, Date> hashMapOfNewPlayers = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat(Configuration.formatDate);
        try {
            hashMapOfNewPlayers.put("one", dateFormat.parse("01/01/1999"));
            hashMapOfNewPlayers.put("second", dateFormat.parse("01/01/1996"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        controller.update(new Message("ALL", TypeOfMessage.START_MATCH, hashMapOfNewPlayers));
        assertTrue(displayMessageHasCalled);
    }
}
