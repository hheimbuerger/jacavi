package de.jacavi.appl.controller.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.jacavi.appl.controller.device.impl.GameControllerDevice;
import de.jacavi.appl.controller.device.impl.GameControllerDeviceManager;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.controller.device.impl.MouseDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDeviceManager;
import de.jacavi.appl.controller.device.impl.GameControllerDeviceManager.GameControllerDescriptor;



public class InputDeviceManager {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(InputDeviceManager.class);

    private final Map<UUID, DeviceController> inputDevices = new TreeMap<UUID, DeviceController>();

    private GameControllerDeviceManager gameControllerDeviceManager;

    private WiimoteDeviceManager wiimoteDeviceManager;

    public InputDeviceManager() {
        // TODO: should be read from configuration, just inserting some for testing here
        KeyboardDevice k1 = new KeyboardDevice("Keyboard 1");
        KeyboardDevice k2 = new KeyboardDevice("Keyboard 2");
        inputDevices.put(k1.getId(), k1);
        inputDevices.put(k2.getId(), k2);

        // a mouse is assumed to be always available
        MouseDevice m1 = new MouseDevice("Mouse");
        inputDevices.put(m1.getId(), m1);

        // game controllers have to be detected
        try {
            gameControllerDeviceManager = new GameControllerDeviceManager();
            redetectGameControllers();
        } catch(UnsatisfiedLinkError e) {
            logger
                    .warn(
                            "GameControllerDeviceManager could not be loaded, game controller support won't be available in this session",
                            e);
            gameControllerDeviceManager = null;
        }

        // and so do Wiimotes
        try {
            wiimoteDeviceManager = new WiimoteDeviceManager();
            redetectWiimotes();
        } catch(UnsatisfiedLinkError e) {
            logger.warn("WiimoteDeviceManager could not be loaded, Wiimote support won't be available in this session",
                    e);
            wiimoteDeviceManager = null;
        }
    }

    /**
     * Adds a new device controller to the internal list and initializes it.
     * 
     * @param controller
     *            the device controller to add
     */
    public void addInputDevice(DeviceController controller) {
        inputDevices.put(controller.getId(), controller);
        controller.initialize();
    }

    /**
     * Returns a list of all connected input devices, sorted by name in alphabetical order.
     * 
     * @return a sorted list of connected input devices
     */
    public List<DeviceController> getInputDevices() {
        List<DeviceController> result = new ArrayList<DeviceController>();
        for(DeviceController dc: inputDevices.values())
            result.add(dc);
        Collections.sort(result);
        return result;
    }

    /**
     * Returns a sorted list of all connected input devices of a certain type (XyzDevice).
     * 
     * @param type
     *            the class of devices to return
     * @return a sorted list of all connected input devices of the given type
     */
    public List<DeviceController> getInputDevicesByType(Class<?> type) {
        List<DeviceController> result = new ArrayList<DeviceController>();
        for(DeviceController dc: inputDevices.values())
            if(dc.getClass() == type)
                result.add(dc);
        Collections.sort(result);
        return result;
    }

    /**
     * Removes an input device from the internal list after deactivating it.
     * 
     * @param id
     *            the ID of the device to remove
     */
    public void removeInputDevice(UUID id) {
        if(inputDevices.containsKey(id)) {
            inputDevices.get(id).cleanup();
            inputDevices.remove(id);
        } else
            throw new IllegalArgumentException("tried removing an ID that does not exist!");
    }

    /**
     * Removes all input devices of a given type.
     * 
     * @param type
     *            the class of input devices to remove (XyzDevice)
     */
    public void removeInputDevicesByType(Class<?> type) {
        List<UUID> toRemove = new ArrayList<UUID>();
        for(UUID id: inputDevices.keySet())
            if(inputDevices.get(id).getClass() == type)
                toRemove.add(id);
        for(UUID id: toRemove)
            removeInputDevice(id);
    }

    /**
     * Returns true if the given ID is valid (corresponds to an available input device), false otherwise.
     * 
     * @param id
     *            the ID to check
     * @return true if the given ID is valid (corresponds to an available input device), false otherwise
     */
    public boolean isIdValid(UUID id) {
        return inputDevices.containsKey(id);
    }

    /**
     * Determines if support for game controllers is available.
     * 
     * @return true if the game controller library could be loaded, false otherwise
     */
    public boolean isGameControllerSupportAvailable() {
        return gameControllerDeviceManager != null;
    }

    /**
     * Determines if support for Wiimotes is available.
     * 
     * @return true if the Wiimote library could be loaded, false otherwise
     */
    public boolean isWiimoteSupportAvailable() {
        return wiimoteDeviceManager != null;
    }

    /**
     * Redetects all available game controllers.
     * <p>
     * FIXME: Currently, previously connected devices are always thrown away. So devices that are still connected are
     * given new IDs.
     */
    public void redetectGameControllers() {
        // run the detection
        removeInputDevicesByType(GameControllerDevice.class);
        GameControllerDescriptor[] gameControllers = gameControllerDeviceManager.scanForGameControllers();

        // add the detected devices to the device manager
        if(gameControllers != null)
            for(GameControllerDescriptor gameController: gameControllers)
                addInputDevice(new GameControllerDevice(gameController.name, gameControllerDeviceManager
                        .getGameController(gameController)));
    }

    /**
     * Redetects all available Wiimotes.
     * <p>
     * FIXME: Currently, previously connected devices are always thrown away. So devices that are still connected are
     * given new IDs.
     */
    public void redetectWiimotes() {
        // run the detection
        removeInputDevicesByType(WiimoteDevice.class);
        int numWiimotesConnected = wiimoteDeviceManager.scanForWiimotes();

        // add the detected devices to the device manager
        for(int i = 1; i <= numWiimotesConnected; i++)
            addInputDevice(new WiimoteDevice("Wiimote " + i, wiimoteDeviceManager.getWiimote(i)));
    }
}
