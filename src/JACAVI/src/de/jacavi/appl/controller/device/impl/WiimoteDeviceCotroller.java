package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.widgets.Composite;

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



public class WiimoteDeviceCotroller implements DeviceController, WiimoteListener {

    private ControllerSignal currentControllerSignal = new ControllerSignal();

    private Wiimote wiimote = null;

    public WiimoteDeviceCotroller(Wiimote wiimote) {
        if(wiimote == null)
            throw new IllegalArgumentException("wiimote may not be null");
        this.wiimote = wiimote;
    }

    @Override
    public void cleanup() {
    // TODO Auto-generated method stub

    }

    @Override
    public boolean initialize(Composite guiElement) {
        wiimote.activateMotionSensing();
        wiimote.addWiiMoteEventListeners(this);

        return true;
    }

    @Override
    public ControllerSignal poll() {
        return currentControllerSignal;
    }

    @Override
    public void onMotionSensingEvent(MotionSensingEvent arg0) {
        int speed = prepareSignal(arg0.getOrientation().getPitch());
        currentControllerSignal.speed = speed;
    }

    private int prepareSignal(float pitch) {
        int retVal = 0;
        float tmpPitch = pitch - 1;
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
    // TODO Auto-generated method stub

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
