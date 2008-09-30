package de.jacavi.appl.controller.device.impl;

import java.util.Arrays;
import java.util.List;

import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import de.jacavi.rcp.util.Check;



/**
 * @author fro Class is for managing multiple wiimotes
 */
public class WiimoteDeviceManager {

    private List<Wiimote> wiimotes = null;

    public WiimoteDeviceManager() {

    }

    /**
     * @return int the number of connected wiiremotes
     */
    public int scanForWiimotes() {
        // first shutdown to remove all existing
        WiiUseApiManager.shutdown();
        // get all wiimotes max 6
        Wiimote[] wiiremotes = WiiUseApiManager.getWiimotes(6, true, WiiUseApiManager.WIIUSE_STACK_UNKNOWN);
        // convert the array in the list
        wiimotes = Arrays.asList(wiiremotes);
        // return the number of connected wiimotes
        return WiiUseApiManager.getNbConnectedWiimotes();
    }

    /**
     * @param id
     *            the id of the Wiiremote to return
     * @return Wiimote
     */
    public Wiimote getWiimote(int id) {
        // check precondition
        Check.Require(id <= wiimotes.size() && id > 0, "id may not be <=0 or > available wiimotes");
        return wiimotes.get(id - 1);
    }

}
