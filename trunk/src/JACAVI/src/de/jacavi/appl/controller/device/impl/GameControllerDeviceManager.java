package de.jacavi.appl.controller.device.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.centralnexus.input.Joystick;



public class GameControllerDeviceManager {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(GameControllerDeviceManager.class);

    public class GameControllerDescriptor {

        public final int id;

        public final String name;

        public final int numAxes;

        public final int numButtons;

        public final String[] capabilities;

        public GameControllerDescriptor(int id, String name, int numAxes, int numButtons, String[] capabilities) {
            this.id = id;
            this.name = name;
            this.numAxes = numAxes;
            this.numButtons = numButtons;
            this.capabilities = capabilities;
        }
    }

    /**
     * Scans for connected game controllers and returns a list of descriptors, describing the properties of one device
     * each.
     * 
     * @return the list of descriptors, or null if the library could not be initialized
     */
    public GameControllerDescriptor[] scanForGameControllers() {
        try {
            // prepare a list for all detected game controllers
            List<GameControllerDescriptor> detectedGameControllers = new ArrayList<GameControllerDescriptor>();

            // iterate over the IDs for all *potentially* connected devices
            for(int id = 0; id < Joystick.getNumDevices(); id++) {

                // check if there's actually a device plugged in under that ID
                if(Joystick.isPluggedIn(id)) {

                    // create a Joystick instance for the device with that ID
                    Joystick gameController = Joystick.createInstance(id);

                    // create a descriptor for this device
                    String[] capabilities = determineDeviceCapabilities(gameController);
                    GameControllerDescriptor descriptor = new GameControllerDescriptor(id, gameController.toString(),
                            gameController.getNumAxes(), gameController.getNumButtons(), capabilities);

                    // add the device to the list
                    detectedGameControllers.add(descriptor);
                }
            }

            // return the result
            return detectedGameControllers.toArray(new GameControllerDescriptor[detectedGameControllers.size()]);
        } catch(Throwable t) {
            logger.warn("Game controller library failed to initialize, returning 0 game controllers.", t);
            return null;
        }
    }

    private String[] determineDeviceCapabilities(Joystick gameController) {
        List<String> capabilities = new ArrayList<String>();
        if(gameController.getCapability(Joystick.HAS_POV)) {
            if(gameController.getCapability(Joystick.HAS_POV4DIR))
                capabilities.add("Point-of-view hat with four discrete directions");
            else if(gameController.getCapability(Joystick.HAS_POVCONT))
                capabilities.add("Point-of-view hat with continuous degree bearings");
            else
                capabilities.add("Point-of-view hat");
        }
        if(gameController.getCapability(Joystick.HAS_R))
            capabilities.add("R-axis");
        if(gameController.getCapability(Joystick.HAS_U))
            capabilities.add("U-axis");
        if(gameController.getCapability(Joystick.HAS_V))
            capabilities.add("V-axis");
        if(gameController.getCapability(Joystick.HAS_Z))
            capabilities.add("Z-axis");
        return capabilities.toArray(new String[capabilities.size()]);
    }

    public Joystick getGameController(GameControllerDescriptor descriptor) {
        try {
            if(Joystick.isPluggedIn(descriptor.id)) {
                Joystick device = Joystick.createInstance(descriptor.id);
                if(device.toString().equals(descriptor.name))
                    return device;
                else
                    return null;
            } else
                return null;
        } catch(Exception e) {
            logger.warn("No plugged in game controller with the given ID could be found, returning null.");
            return null;
        }
    }
}
