package de.jacavi.hal.bluerider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarSpeedAdjuster;
import de.jacavi.rcp.util.Check;
import de.jacavi.rcp.util.ExceptionHandler;



//Kanal Hinkanal Rückkanal
//--------------------------------------------------------------------
//FixData_0 Motorleistung Motorleistung
//FixData_1 nicht belegt G-Kraft Y-Achse
//FixData_2 nicht belegt G-Kraft X-Achse
//FixData_3 nicht belegt nicht belegt
//FixData_4 nicht belegt nicht belegt
//FixData_5 nicht belegt nicht belegt
//FixData_6 nicht belegt nicht belegt
//FixData_7 nicht belegt nicht belegt
//MSG_0 Frontlicht & Hecklicht nicht belegt
//Restliche Message-Kanäle sind frei

/**
 * @author fro
 */
public class BlueriderConnectorAdapter implements BlueriderConnector, ComListener {

    private static Log log = LogFactory.getLog(BlueriderConnectorAdapter.class);

    private ComManager comManager = null;

    @SuppressWarnings("unused")
    private boolean isConnected = false;

    private final int maxHALSpeed = 255;

    private int currentSpeed;

    public BlueriderConnectorAdapter() {
        comManager = ComManager.getInstanceOfCM();
    }

    @Override
    public void connectBlueRider(String comPort) {
        Check.Require(comPort != null && !comPort.isEmpty(), "comPort may not be null or empty");
        try {
            log.info("Try to connect to Bluerider via bluetooth on port: " + comPort);
            comManager.connect(comPort);
            log.info("Successful connected to bluerider on port: " + comPort);
            comManager.addComListener(this, ComManager.FIX_0);
            isConnected = true;
        } catch(Exception e) {
            ExceptionHandler.handleException(e, log, "error on connecting to bluerider on port: " + comPort, true,
                    "Bluerider connection Problem", new Status(IStatus.ERROR, "JACAVI", e.toString()));
        }
    }

    @Override
    public void fullBreak(int carID) {

        try {
            comManager.setFixData(ComManager.FIX_0, (byte) 0);
        } catch(ComException e) {
            if(e.getReason() == ComException.FAILSAFE) {
                // TODO: ???
            }
        }
    }

    @Override
    public int getSpeed(int carID) {
        return SlotCarSpeedAdjuster.denormalizeSpeed(currentSpeed, maxHALSpeed);
    }

    @Override
    public int getSwitch(int carID) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSpeed(int carID, int speed) {
        int normalizedSpeed = SlotCarSpeedAdjuster.normalizeSpeed(speed, maxHALSpeed);
        byte value = (byte) normalizedSpeed;

        try {
            comManager.setFixData(ComManager.FIX_0, value);
        } catch(ComException e) {
            if(e.getReason() == ComException.FAILSAFE) {
                // TODO:
            }
        }
    }

    @Override
    public int toggleSwitch(int carID) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void fixDataReceived(byte b, int index) {
        // here we get the engine power back
        short a = (short) (0x00FF & b);
        currentSpeed = a;
    }

    @Override
    public void msgReceived(Message m, int index) {

    }

    @Override
    public FeedbackSignal pollFeedback() {
        // TODO Auto-generated method stub
        return null;
    }

}
