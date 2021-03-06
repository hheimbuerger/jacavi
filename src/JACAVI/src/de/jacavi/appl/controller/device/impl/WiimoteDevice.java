package de.jacavi.appl.controller.device.impl;

import java.util.List;

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
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;
import de.jacavi.rcp.util.Check;



public class WiimoteDevice extends DeviceController implements WiimoteListener {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(WiimoteDevice.class);

    private ControllerSignal currentControllerSignal = new ControllerSignal();

    private Wiimote wiimote = null;

    public WiimoteDevice(String name, Wiimote wiimote) {
        super(name);
        Check.Require(wiimote != null, "wiimote may not be null");
        this.wiimote = wiimote;
        wiimote.activateMotionSensing();
        wiimote.addWiiMoteEventListeners(this);
    }

    @Override
    public void activate(Track track, List<Player> players) {
        currentControllerSignal = new ControllerSignal();
    }

    @Override
    public void deactivate() {

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
        int speed = normaliseThrust((int) arg0.getOrientation().getPitch());
        currentControllerSignal.setThrust(speed);
    }

    @Override
    public int normaliseThrust(float deviceSpecificInputSpeedSignal) {
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
        currentControllerSignal.setTrigger(arg0.isButtonBPressed());
        // set reset
        currentControllerSignal.setReset(arg0.isButtonAPressed());
        // switch front light on/off
        currentControllerSignal.setSwitchFrontLight(arg0.isButtonOneJustPressed());
        // set back light on/off
        currentControllerSignal.setSwitchBackLight(arg0.isButtonTwoJustPressed());

    }

    @Override
    public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0) {

    }

    @Override
    public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0) {

    }

    @Override
    public void onDisconnectionEvent(DisconnectionEvent arg0) {
        // TODO: [ticket #74] handle wiiremote disconnection
        log.warn("Wiiremote " + arg0.getWiimoteId() + " is disconnected and no longer available.");
    }

    @Override
    public void onExpansionEvent(ExpansionEvent arg0) {

    }

    @Override
    public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0) {

    }

    @Override
    public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0) {

    }

    @Override
    public void onIrEvent(IREvent arg0) {

    }

    @Override
    public void onNunchukInsertedEvent(NunchukInsertedEvent arg0) {

    }

    @Override
    public void onNunchukRemovedEvent(NunchukRemovedEvent arg0) {

    }

    @Override
    public void onStatusEvent(StatusEvent arg0) {

    }
}
