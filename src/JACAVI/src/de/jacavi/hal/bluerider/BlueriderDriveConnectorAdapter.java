package de.jacavi.hal.bluerider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.hal.SlotCarThrustAdjuster;
import de.jacavi.rcp.util.Check;



//Kanal Hinkanal R�ckkanal
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
//Restliche Message-Kan�le sind frei

/**
 * @author fro
 */
public class BlueriderDriveConnectorAdapter implements BlueriderDriveConnector, ComListener {

    private static Log log = LogFactory.getLog(BlueriderDriveConnectorAdapter.class);

    private ComManager comManager = null;

    private String comPort = "";

    private boolean isConnected = false;

    private final int maxHALSpeed = 255;

    private boolean frontLight = true;

    private boolean backLight = true;

    private int currentSpeed;

    public BlueriderDriveConnectorAdapter(String comPort) {
        Check.Require(comPort != null && !comPort.isEmpty(), "comPort may not be null or empty");
        this.comPort = comPort;
        comManager = ComManager.getInstanceOfCM();
    }

    @Override
    public boolean connectBlueRider() {
        Check.Require(comPort != null && !comPort.isEmpty(), "comPort may not be null or empty");
        try {
            log.info("Try to connect to Bluerider via bluetooth on port: " + comPort);
            comManager.connect(comPort);
            log.info("Successful connected to bluerider on port: " + comPort);
            comManager.addComListener(this, ComManager.FIX_0);
            isConnected = true;
        } catch(Exception e) {
            log.error("Could not connect to Bluerider on port" + comPort, e);
            /*
            ExceptionHandler.handleException(e, log, "error on connecting to bluerider on port: " + comPort, true,
                    "Bluerider connection Problem", new Status(IStatus.ERROR, "JACAVI", e.toString()));
            */
        }
        return isConnected;
    }

    @Override
    public void fullBreak() {

        try {
            comManager.setFixData(ComManager.FIX_0, (byte) 0);
        } catch(ComException e) {
            if(e.getReason() == ComException.FAILSAFE) {
                log.error("Bluerider FAILSAFE");
            }
        }
    }

    @Override
    public int getThrust() {
        Check.Ensure(currentSpeed >= 0 && currentSpeed <= maxHALSpeed, "currentSpeed is in the wrong Range");
        return SlotCarThrustAdjuster.denormalizeThrust(currentSpeed, maxHALSpeed);
    }

    @Override
    public boolean getSwitch() {
        // TODO: [ticket #173] Implement me if analogue track supports switching lane
        return false;
    }

    @Override
    public void setThrust(int speed) {
        log.debug("Bluerider speed in: " + speed);
        int normalizedSpeed = SlotCarThrustAdjuster.normalizeThrust(speed, maxHALSpeed);
        Check.Require(normalizedSpeed >= 0 && normalizedSpeed <= maxHALSpeed, "Speed is int the wrong range");

        log.debug("Bluerider adjusted speed: " + normalizedSpeed);
        byte value = (byte) normalizedSpeed;

        try {
            comManager.setFixData(ComManager.FIX_0, value);
        } catch(ComException e) {
            if(e.getReason() == ComException.FAILSAFE) {
                log.error("Bluerider FAILSAFE");
            }
        }
    }

    @Override
    public boolean toggleSwitch() {
        // TODO: [ticket #173] Implement me if analogue track supports switching lane
        return false;
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
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void switchBackLight() {
        Message m = new Message();
        if(backLight) {
            m.header = 2;
            m.payload[0] = 0;
            try {
                comManager.sendMessage(m, ComManager.MSG_0);
                backLight = false;
            } catch(ComException e1) {
                log.error(e1.getMessage());
            }
        } else {
            m.header = 2;
            m.payload[0] = 1;
            try {
                comManager.sendMessage(m, ComManager.MSG_0);
                backLight = true;
            } catch(ComException e1) {
                log.error(e1.getMessage());
            }
        }
    }

    @Override
    public void switchFrontLight() {
        Message m = new Message();
        if(frontLight) {
            m.header = 1;
            m.payload[0] = 0;
            try {
                comManager.sendMessage(m, ComManager.MSG_0);
                frontLight = false;
            } catch(ComException e1) {
                log.error(e1.getMessage());
            }
        } else {
            m.header = 1;
            m.payload[0] = 1;
            try {
                comManager.sendMessage(m, ComManager.MSG_0);
                frontLight = true;
            } catch(ComException e1) {
                log.error(e1.getMessage());
            }
        }
    }

    @Override
    public boolean isBackLightOn() {
        return backLight;
    }

    @Override
    public boolean isFrontLightOn() {
        return frontLight;
    }
}
