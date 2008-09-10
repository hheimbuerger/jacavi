package de.jacavi.appl.controller.device.impl;

import org.apache.log4j.Logger;

import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.rcp.util.Check;



public class WiimoteDevice extends DeviceController implements WiimoteListener {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(WiimoteDevice.class);

    private final ControllerSignal currentControllerSignal = new ControllerSignal();

    private Wiimote wiimote = null;

    public WiimoteDevice(String name, Wiimote wiimote) {
        super(name);
        Check.Require(wiimote != null, "wiimote may not be null");
        this.wiimote = wiimote;
        wiimote.activateMotionSensing();
        wiimote.addWiiMoteEventListeners(this);
    }

    public Wiimote getWiimote() {
        return wiimote;
    }

    public void setWiimote(Wiimote wiimote) {
        this.wiimote = wiimote;
    }

    @Override
    public ControllerSignal poll() {
        return currentControllerSignal;
    }

    @Override
    public void onMotionSensingEvent(MotionSensingEvent arg0) {
        int speed = normaliseSpeedSignal((int) arg0.getOrientation().getPitch());
        currentControllerSignal.setSpeed(speed);
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        int retVal = 0;
        float tmpPitch = (deviceSpecificInputSpeedSignal + 90);

        if(tmpPitch >= 90)
            retVal = 100;
        else if(tmpPitch <= 0)
            retVal = 0;
        else {
            tmpPitch = tmpPitch * 1.1111111111111111111111f;
            retVal = (int) tmpPitch;
        }
        return retVal;
    }

    @Override
    public void onButtonsEvent(WiimoteButtonsEvent arg0) {
        // set trigger
        if(arg0.isButtonBJustPressed())
            currentControllerSignal.setTrigger(true);
        // switch front light on/off
        if(arg0.isButtonOneJustPressed())
            currentControllerSignal.setSwitchFrontLight(true);
        // set back light on/off
        if(arg0.isButtonTwoJustPressed())
            currentControllerSignal.setSwitchBackLight(true);

    }

    @Override
    public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onDisconnectionEvent(DisconnectionEvent arg0) {
        // TODO Auto-generated method stub
        log.warn("Wiiremote " + arg0.getWiimoteId() + " is disconnected and no longer available.");
    }

    @Override
    public void onExpansionEvent(ExpansionEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onIrEvent(IREvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onNunchukInsertedEvent(NunchukInsertedEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onNunchukRemovedEvent(NunchukRemovedEvent arg0) {
    // TODO Auto-generated method stub

    }

    @Override
    public void onStatusEvent(StatusEvent arg0) {
    // TODO Auto-generated method stub

    }

}
